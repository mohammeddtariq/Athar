package com.athar.app.data

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * One-tap location: fresh high-accuracy fix, provider checks, and the
 * system dialog that turns location on immediately (no manual trip to
 * settings). Backed by Google Play Services Location (free SDK).
 */
object LocationHelper {

    fun hasPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isProvidersOn(context: Context): Boolean {
        return try {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Best-effort fresh location: high-accuracy single fix first,
     * Fused last location second, LocationManager last-known third.
     */
    suspend fun freshFix(context: Context): Pair<Double, Double>? =
        withContext(Dispatchers.IO) {
            if (!hasPermission(context)) return@withContext null
            // 1. Fresh high-accuracy fix (also warms up the chip for Qibla).
            withTimeoutOrNull(12_000) { currentFix(context) }?.let { return@withContext it }
            // 2. Fused last location.
            withTimeoutOrNull(4_000) { fusedLast(context) }?.let { return@withContext it }
            // 3. Platform last-known.
            platformLastKnown(context)
        }

    private suspend fun currentFix(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val tokenSource = com.google.android.gms.tasks.CancellationTokenSource()
                cont.invokeOnCancellation { tokenSource.cancel() }
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                    .addOnSuccessListener { loc ->
                        val value = if (loc != null) loc.latitude to loc.longitude else null
                        if (cont.isActive) cont.resumeWith(Result.success(value))
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resumeWith(Result.success(null))
                    }
            } catch (e: SecurityException) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            }
        }

    private suspend fun fusedLast(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            try {
                LocationServices.getFusedLocationProviderClient(context).lastLocation
                    .addOnSuccessListener { loc ->
                        val value = if (loc != null) loc.latitude to loc.longitude else null
                        if (cont.isActive) cont.resumeWith(Result.success(value))
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resumeWith(Result.success(null))
                    }
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWith(Result.failure(e))
            }
        }

    @Suppress("MissingPermission")
    private fun platformLastKnown(context: Context): Pair<Double, Double>? {
        return try {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
                try {
                    val loc = manager.getLastKnownLocation(provider)
                    if (loc != null) return loc.latitude to loc.longitude
                } catch (_: Exception) { /* next provider */ }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Resolves the human-readable city/locality name from GPS coordinates.
     * Uses Android Geocoder with the given language locale ("ar" or "en").
     * Falls back to matching against known preset cities when offline or Geocoder is unavailable.
     */
    suspend fun resolveCityName(
        context: Context,
        lat: Double,
        lng: Double,
        languageCode: String
    ): String? = withContext(Dispatchers.IO) {
        // 1. Try Android Geocoder first with timeout
        val geocoded = withTimeoutOrNull(4_000) {
            try {
                if (Geocoder.isPresent()) {
                    val locale = Locale.forLanguageTag(languageCode)
                    val geocoder = Geocoder(context, locale)
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null) {
                        val candidate = address.locality?.takeIf { it.isNotBlank() }
                            ?: address.subAdminArea?.takeIf { it.isNotBlank() }
                            ?: address.adminArea?.takeIf { it.isNotBlank() }
                            ?: address.countryName?.takeIf { it.isNotBlank() }
                        candidate
                    } else null
                } else null
            } catch (_: Exception) {
                null
            }
        }
        if (!geocoded.isNullOrBlank()) {
            return@withContext geocoded
        }

        // 2. Offline / Geocoder fallback: match nearest preset city within 40 km
        findNearestPresetCity(lat, lng, maxDistanceKm = 40.0)?.let {
            return@withContext it.display(languageCode)
        }

        null
    }

    fun findNearestPresetCity(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double = 40.0
    ): PresetCity? {
        var closestCity: PresetCity? = null
        var minDistance = Float.MAX_VALUE
        val results = FloatArray(1)

        for (city in presetCities) {
            Location.distanceBetween(lat, lng, city.lat, city.lng, results)
            val dist = results[0] // in meters
            if (dist < minDistance && dist <= maxDistanceKm * 1000) {
                minDistance = dist
                closestCity = city
            }
        }
        return closestCity
    }
}

data class PresetCity(val labelEn: String, val labelAr: String, val lat: Double, val lng: Double) {
    fun display(lang: String?): String = if (lang == "en") labelEn else labelAr
}

val presetCities = listOf(
    PresetCity("Riyadh", "الرياض", 24.7136, 46.6753),
    PresetCity("Makkah", "مكة المكرمة", 21.4225, 39.8262),
    PresetCity("Madinah", "المدينة المنورة", 24.5247, 39.5692),
    PresetCity("Jeddah", "جدة", 21.4858, 39.1925),
    PresetCity("Cairo", "القاهرة", 30.0444, 31.2357),
    PresetCity("Alexandria", "الإسكندرية", 31.2001, 29.9187),
    PresetCity("Abu Dhabi", "أبوظبي", 24.4539, 54.3773),
    PresetCity("Dubai", "دبي", 25.2048, 55.2708),
    PresetCity("Doha", "الدوحة", 25.2854, 51.5310),
    PresetCity("Kuwait", "الكويت", 29.3759, 47.9774),
    PresetCity("Manama", "المنامة", 26.2285, 50.5860),
    PresetCity("Muscat", "مسقط", 23.5880, 58.3829),
    PresetCity("Jerusalem", "القدس", 31.7683, 35.2137),
    PresetCity("Amman", "عَمّان", 31.9454, 35.9284),
    PresetCity("Baghdad", "بغداد", 33.3152, 44.3661),
    PresetCity("Beirut", "بيروت", 33.8938, 35.5018),
    PresetCity("Damascus", "دمشق", 33.5138, 36.2765),
    PresetCity("Tripoli", "طرابلس", 32.8872, 13.1913),
    PresetCity("Tunis", "تونس", 36.8065, 10.1815),
    PresetCity("Algiers", "الجزائر", 36.7538, 3.0588),
    PresetCity("Rabat", "الرباط", 34.0209, -6.8416),
    PresetCity("Casablanca", "الدار البيضاء", 33.5731, -7.5898),
    PresetCity("Istanbul", "إسطنبول", 41.0082, 28.9784),
    PresetCity("Jakarta", "جاكرتا", -6.2088, 106.8456),
    PresetCity("Kuala Lumpur", "كوالالمبور", 3.1390, 101.6869),
    PresetCity("London", "لندن", 51.5074, -0.1278),
    PresetCity("Paris", "باريس", 48.8566, 2.3522),
    PresetCity("New York", "نيويورك", 40.7128, -74.0060)
)

/**
 * Remembers a `requestEnableLocation()` launcher that pops the system
 * dialog turning location on with one tap. Calls [onEnabled] when the
 * providers are confirmed on.
 */
@Suppress("DEPRECATION") // setAlwaysShow: still the only way to force the enable dialog
@Composable
fun rememberLocationEnabler(onEnabled: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) onEnabled()
    }
    return remember(context) {
        {
            try {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()
                val settings = LocationSettingsRequest.Builder()
                    .addLocationRequest(request)
                    .setAlwaysShow(true)
                    .build()
                LocationServices.getSettingsClient(context)
                    .checkLocationSettings(settings)
                    .addOnSuccessListener { onEnabled() }
                    .addOnFailureListener { e ->
                        val resolvable = e as? ResolvableApiException
                        if (resolvable != null) {
                            try {
                                launcher.launch(
                                    IntentSenderRequest.Builder(resolvable.resolution).build()
                                )
                            } catch (_: Exception) { /* dialog unavailable */ }
                        }
                    }
            } catch (_: Exception) { /* play services unavailable */ }
        }
    }
}
