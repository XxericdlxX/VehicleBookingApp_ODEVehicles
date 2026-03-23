package cat.copernic.odecoches.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max


object ImageBase64 {

    fun uriToBase64Jpeg(
        context: Context,
        uri: Uri,
        maxSidePx: Int = 1024,
        jpegQuality: Int = 85
    ): String? {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        val resized = resizeKeepingAspect(bitmap, maxSidePx)

        val baos = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, jpegQuality, baos)
        val outBytes = baos.toByteArray()

        return Base64.encodeToString(outBytes, Base64.NO_WRAP)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
        cursor.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return null
    }

    fun base64ToBytes(base64: String?): ByteArray? {
        if (base64.isNullOrBlank()) return null
        val clean = base64.trim().substringAfter(",",
            base64.trim()
        )
        return runCatching { Base64.decode(clean, Base64.DEFAULT) }.getOrNull()
    }

    private fun resizeKeepingAspect(bitmap: Bitmap, maxSidePx: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val maxSide = max(w, h)

        if (maxSide <= maxSidePx) return bitmap

        val scale = maxSidePx.toFloat() / maxSide.toFloat()
        val newW = (w * scale).toInt().coerceAtLeast(1)
        val newH = (h * scale).toInt().coerceAtLeast(1)

        return Bitmap.createScaledBitmap(bitmap, newW, newH, true)
    }
}
