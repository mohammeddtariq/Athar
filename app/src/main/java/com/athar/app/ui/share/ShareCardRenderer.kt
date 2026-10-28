package com.athar.app.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Hands a rendered card to whatever the user wants to share it with.
 *
 * The card itself is drawn by [ShareCardSheet] in Compose and captured from the
 * very layer that is on screen, so this only has to write the PNG and open the
 * chooser.
 */
object ShareCardRenderer {

    fun shareBitmap(
        context: Context,
        bitmap: Bitmap,
        subject: String,
        chooserTitle: String
    ): Boolean = try {
        val dir = File(context.cacheDir, "shares").apply { mkdirs() }
        dir.listFiles()?.forEach { if (it.isFile) it.delete() }
        val file = File(dir, "athar-share-${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
        bitmap.recycle()
        true
    } catch (_: Throwable) {
        false
    }
}
