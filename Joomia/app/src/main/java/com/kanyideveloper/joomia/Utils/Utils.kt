package com.kanyideveloper.joomia.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Utils {
    companion object {
        private fun compressImage(context: Context, uri: Uri): Bitmap? {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options()
                options.inSampleSize = 2 // Adjust this value as needed for compression
                return BitmapFactory.decodeStream(inputStream, null, options)
            } catch (e: Exception) {
                Timber.e("Error compressing image: $e")
            } finally {
                inputStream?.close()
            }
            return null
        }

        fun convertImageToBase64(context: Context, uri: Uri): String {
            val bitmap = compressImage(context, uri)
            return if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Adjust compression quality as needed
                val byteArray = outputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            } else {
                ""
            }
        }
    }
}
