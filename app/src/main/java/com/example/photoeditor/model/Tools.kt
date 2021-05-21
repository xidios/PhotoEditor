package com.example.photoeditor.model

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.os.SystemClock
import android.provider.MediaStore
import androidx.core.net.toFile
import java.io.File
import java.io.FileOutputStream
import java.util.*

class Tools {
    companion object {
        fun saveTempImage(context: Context, image: Bitmap): Parcelable {
            val fileDirectory = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "PhotoEditor"
            )
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }

            val fileName = "img_${SystemClock.uptimeMillis()}.jpg"
            val file = File(fileDirectory, fileName)
            image.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return Uri.fromFile(file)

            /*val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, UUID.randomUUID().toString() + ".png", "drawing")
            return Uri.parse(path)*/
        }

        fun clearHistory(context: Context, history: ArrayDeque<Uri>) {
            for (image in history) {
                deleteFile(context, image)
            }
        }

        fun deleteFile(context: Context, image: Uri) {
            val file = image.toFile()
            if (file.exists()) {
                if (file.delete()) {
                    context.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(image.toFile())
                        )
                    )
                }
            }
        }
    }
}