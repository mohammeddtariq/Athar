package com.athar.app.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.content.res.Configuration
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.athar.app.MainActivity
import com.athar.app.R
import com.athar.app.data.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Locale

data class AppReleaseInfo(
    val tagName: String,
    val versionName: String,
    val releaseTitle: String,
    val changelogBody: String,
    val htmlUrl: String,
    val downloadUrl: String?,
    val assetSize: Long
)

sealed interface UpdateCheckResult {
    data class UpdateAvailable(val releaseInfo: AppReleaseInfo) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data class Error(val message: String) : UpdateCheckResult
}

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preReleaseType: String? = null,
    val preReleaseNumber: Int? = null
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        if (major != other.major) return major.compareTo(other.major)
        if (minor != other.minor) return minor.compareTo(other.minor)
        if (patch != other.patch) return patch.compareTo(other.patch)

        // A final release is higher than a pre-release with same major.minor.patch
        if (preReleaseType == null && other.preReleaseType != null) return 1
        if (preReleaseType != null && other.preReleaseType == null) return -1
        if (preReleaseType == null && other.preReleaseType == null) return 0

        // Both are pre-releases (e.g. beta vs rc, or beta.1 vs beta.2)
        val typeCompare = (preReleaseType ?: "").compareTo(other.preReleaseType ?: "")
        if (typeCompare != 0) return typeCompare

        return (preReleaseNumber ?: 0).compareTo(other.preReleaseNumber ?: 0)
    }

    companion object {
        fun parse(versionStr: String): SemanticVersion? {
            val clean = versionStr.trim().removePrefix("v").removePrefix("V")
            val regex = Regex("""^(\d+)\.(\d+)(?:\.(\d+))?(?:[-.]([a-zA-Z]+)(?:[-.]?(\d+))?)?""")
            val match = regex.find(clean) ?: return null
            val major = match.groupValues[1].toIntOrNull() ?: 0
            val minor = match.groupValues[2].toIntOrNull() ?: 0
            val patch = match.groups[3]?.value?.toIntOrNull() ?: 0
            val preType = match.groups[4]?.value?.lowercase()
            val preNum = match.groups[5]?.value?.toIntOrNull()
            return SemanticVersion(major, minor, patch, preType, preNum)
        }
    }
}

object AppUpdateManager {
    private const val GITHUB_REPO = "mohammeddtariq/Athar"
    private const val RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases?per_page=5"
    private const val LATEST_RELEASE_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
    private const val ATOM_FEED_URL = "https://github.com/$GITHUB_REPO/releases.atom"
    const val UPDATE_CHANNEL_ID = "athar_app_updates"
    private const val UPDATE_NOTIF_ID = 9001
    const val EXTRA_OPEN_UPDATER = "com.athar.app.OPEN_UPDATER"

    /**
     * Checks GitHub for the latest release and determines if an update is available.
     * Uses a multi-tiered discovery strategy:
     * 1. Query /releases?per_page=5 to instantly catch pre-releases and beta versions.
     * 2. Fall back to /releases/latest if the releases array isn't accessible.
     * 3. Fall back to GitHub's public Atom feed if unauthenticated API limits (HTTP 403) are encountered.
     */
    suspend fun checkForUpdate(currentVersionName: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            // Tier 1: Query recent releases list (includes pre-releases and betas)
            val releasesResult = fetchFromReleasesApi(RELEASES_API_URL, currentVersionName)
            if (releasesResult is UpdateCheckResult.UpdateAvailable || releasesResult is UpdateCheckResult.UpToDate) {
                return@withContext releasesResult
            }

            // Tier 2: Query latest release endpoint
            val latestResult = fetchFromReleasesApi(LATEST_RELEASE_URL, currentVersionName)
            if (latestResult is UpdateCheckResult.UpdateAvailable || latestResult is UpdateCheckResult.UpToDate) {
                return@withContext latestResult
            }

            // Tier 3: Unauthenticated rate-limit resilient Atom feed
            fetchFromAtomFeed(currentVersionName)
        } catch (e: Exception) {
            // Final safety net via Atom feed
            fetchFromAtomFeed(currentVersionName)
        }
    }

    private fun fetchFromReleasesApi(apiUrl: String, currentVersionName: String): UpdateCheckResult {
        return try {
            val connection = URI.create(apiUrl).toURL().openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 8000
                readTimeout = 8000
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return UpdateCheckResult.UpToDate
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return UpdateCheckResult.Error("HTTP $responseCode")
            }

            val jsonString = connection.inputStream.bufferedReader().use { it.readText() }.trim()
            if (jsonString.startsWith("[")) {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val releaseObj = jsonArray.getJSONObject(i)
                    if (releaseObj.optBoolean("draft", false)) continue
                    val res = parseReleaseJson(releaseObj, currentVersionName)
                    if (res is UpdateCheckResult.UpdateAvailable) {
                        return res
                    }
                }
                UpdateCheckResult.UpToDate
            } else {
                val json = JSONObject(jsonString)
                parseReleaseJson(json, currentVersionName)
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.localizedMessage ?: "Network error")
        }
    }

    private fun parseReleaseJson(json: JSONObject, currentVersionName: String): UpdateCheckResult {
        val tagName = json.optString("tag_name", "").trim()
        val releaseTitle = json.optString("name", tagName)
        val changelog = json.optString("body", "").trim()
        val htmlUrl = json.optString("html_url", "https://github.com/$GITHUB_REPO/releases")

        var downloadUrl: String? = null
        var assetSize = 0L

        val assets = json.optJSONArray("assets")
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.optString("name", "")
                if (name.endsWith(".apk", ignoreCase = true)) {
                    downloadUrl = asset.optString("browser_download_url")
                    assetSize = asset.optLong("size", 0L)
                    break
                }
            }
        }

        // If no direct asset attached, fallback to release download URL convention
        if (downloadUrl.isNullOrEmpty() && tagName.isNotEmpty()) {
            downloadUrl = "https://github.com/$GITHUB_REPO/releases/download/$tagName/athar-$tagName.apk"
        }

        val remoteVersion = SemanticVersion.parse(tagName)
        val localVersion = SemanticVersion.parse(currentVersionName)

        if (remoteVersion != null && localVersion != null) {
            if (remoteVersion > localVersion) {
                val info = AppReleaseInfo(
                    tagName = tagName,
                    versionName = tagName.removePrefix("v").removePrefix("V"),
                    releaseTitle = releaseTitle,
                    changelogBody = changelog,
                    htmlUrl = htmlUrl,
                    downloadUrl = downloadUrl,
                    assetSize = assetSize
                )
                return UpdateCheckResult.UpdateAvailable(info)
            } else {
                return UpdateCheckResult.UpToDate
            }
        }

        // Fallback check if SemVer couldn't parse
        if (tagName.isNotEmpty() && !tagName.equals(currentVersionName, ignoreCase = true) &&
            !tagName.equals("v$currentVersionName", ignoreCase = true)
        ) {
            val info = AppReleaseInfo(
                tagName = tagName,
                versionName = tagName.removePrefix("v").removePrefix("V"),
                releaseTitle = releaseTitle,
                changelogBody = changelog,
                htmlUrl = htmlUrl,
                downloadUrl = downloadUrl,
                assetSize = assetSize
            )
            return UpdateCheckResult.UpdateAvailable(info)
        }

        return UpdateCheckResult.UpToDate
    }

    private fun fetchFromAtomFeed(currentVersionName: String): UpdateCheckResult {
        return try {
            val connection = URI.create(ATOM_FEED_URL).toURL().openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Athar-Android")
                connectTimeout = 8000
                readTimeout = 8000
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return UpdateCheckResult.UpToDate
            }
            val content = connection.inputStream.bufferedReader().use { it.readText() }
            val entryRegex = Regex("""<entry>([\s\S]*?)</entry>""")
            val entryMatch = entryRegex.find(content) ?: return UpdateCheckResult.UpToDate
            val entry = entryMatch.groupValues[1]

            val tagRegex = Regex("""releases/tag/([^"/]+)""")
            val tagMatch = tagRegex.find(entry) ?: return UpdateCheckResult.UpToDate
            val tagName = tagMatch.groupValues[1].trim()

            val titleRegex = Regex("""<title>([^<]+)</title>""")
            val title = titleRegex.find(entry)?.groupValues?.get(1)?.trim() ?: tagName

            val remoteVersion = SemanticVersion.parse(tagName)
            val localVersion = SemanticVersion.parse(currentVersionName)

            if (remoteVersion != null && localVersion != null && remoteVersion > localVersion) {
                val apkDownloadUrl = "https://github.com/$GITHUB_REPO/releases/download/$tagName/athar-$tagName.apk"
                val info = AppReleaseInfo(
                    tagName = tagName,
                    versionName = tagName.removePrefix("v").removePrefix("V"),
                    releaseTitle = title,
                    changelogBody = "",
                    htmlUrl = "https://github.com/$GITHUB_REPO/releases/tag/$tagName",
                    downloadUrl = apkDownloadUrl,
                    assetSize = 0L
                )
                UpdateCheckResult.UpdateAvailable(info)
            } else {
                UpdateCheckResult.UpToDate
            }
        } catch (_: Exception) {
            UpdateCheckResult.UpToDate
        }
    }

    /**
     * Downloads the APK file with streaming progress updates and redirect handling.
     */
    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (progress: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        val targetFile = File(updatesDir, "athar_update.apk")
        val partFile = File(updatesDir, "athar_update.apk.part")

        try {
            connection = openConnectionWithRedirects(downloadUrl)
            val totalBytes = connection.contentLengthLong

            inputStream = connection.inputStream
            outputStream = FileOutputStream(partFile)

            val buffer = ByteArray(8192)
            var downloadedBytes = 0L
            var read: Int

            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
                downloadedBytes += read

                val progress = if (totalBytes > 0) {
                    (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }
                onProgress(progress, downloadedBytes, totalBytes)
            }

            outputStream.flush()
            outputStream.close()
            outputStream = null

            if (partFile.exists()) {
                if (targetFile.exists()) targetFile.delete()
                if (partFile.renameTo(targetFile)) {
                    Result.success(targetFile)
                } else {
                    Result.failure(IllegalStateException("Failed to finalize downloaded APK"))
                }
            } else {
                Result.failure(IllegalStateException("Downloaded file missing"))
            }
        } catch (e: Exception) {
            partFile.delete()
            Result.failure(e)
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { inputStream?.close() } catch (_: Exception) {}
            connection?.disconnect()
        }
    }

    /**
     * Follows HTTP 301/302/307/308 redirects (such as GitHub to AWS S3).
     */
    private fun openConnectionWithRedirects(initialUrl: String): HttpURLConnection {
        var currentUrl = initialUrl
        var redirects = 0

        while (redirects < 6) {
            val conn = URI.create(currentUrl).toURL().openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", "Athar-Android")
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.connect()

            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_MOVED_TEMP ||
                code == HttpURLConnection.HTTP_MOVED_PERM ||
                code == HttpURLConnection.HTTP_SEE_OTHER ||
                code == 307 || code == 308
            ) {
                val nextUrl = conn.getHeaderField("Location") ?: break
                conn.disconnect()
                currentUrl = nextUrl
                redirects++
            } else {
                return conn
            }
        }
        val conn = URI.create(currentUrl).toURL().openConnection() as HttpURLConnection
        conn.connect()
        return conn
    }

    /**
     * Checks permission and starts APK package installation.
     * Returns true if install intent was launched, false if permission settings needed.
     */
    fun installApk(context: Context, apkFile: File): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val manageIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(manageIntent)
                return false
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
        return true
    }

    /**
     * Posts a serene system notification to alert the user about a newly discovered update.
     */
    fun showUpdateNotification(context: Context, releaseInfo: AppReleaseInfo) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Localize notification according to app's selected language
        val prefs = AppPreferences(context.applicationContext)
        val langCode = runCatching { runBlocking { prefs.selectedLanguage.first() } }.getOrDefault("ar")
        val locale = Locale.forLanguageTag(langCode)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        val localizedContext = context.createConfigurationContext(config)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                localizedContext.getString(R.string.update_notif_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = localizedContext.getString(R.string.update_notif_channel_desc)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_UPDATER, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            UPDATE_NOTIF_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(localizedContext.getString(R.string.update_notif_title))
            .setContentText(localizedContext.getString(R.string.update_notif_body, releaseInfo.versionName))
            .setStyle(NotificationCompat.BigTextStyle().bigText(localizedContext.getString(R.string.update_notif_body, releaseInfo.versionName)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(UPDATE_NOTIF_ID, notification)
    }

    /**
     * Clears any active update notification.
     */
    fun clearUpdateNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(UPDATE_NOTIF_ID)
    }

    /**
     * Clears all cached downloaded APK and temporary files to free up storage space.
     */
    fun cleanupDownloadedApks(context: Context) {
        runCatching {
            val updatesDir = File(context.cacheDir, "updates")
            if (updatesDir.exists()) {
                updatesDir.deleteRecursively()
            }
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".apk", ignoreCase = true) || file.name.endsWith(".part", ignoreCase = true)) {
                    file.delete()
                }
            }
        }
    }
}
