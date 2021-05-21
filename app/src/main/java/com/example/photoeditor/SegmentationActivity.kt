package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_segmentation.*
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter

class SegmentationActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private lateinit var image: Bitmap

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        FaceDetection.loadModel(this)
        val resultIntent = Intent()

        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
        if (receivedImage != null) {
            segmentationImage.setImageURI(receivedImage as Uri)
            image = (segmentationImage.drawable as BitmapDrawable).bitmap
//            image = getCapturedImage(receivedImage as Uri)
        }

        segmentationToolbar.setNavigationOnClickListener {
            this.finish()
        }

        applySegmentationButton.setOnClickListener() {
            image = detectFace(image)
            segmentationImage.setImageBitmap(image)
        }

        saveButton.setOnClickListener() {
            try {
                resultIntent.putExtra(RESULT_TAG, saveTempImage(this, image))
                setResult(RESULT_OK, resultIntent)
            } catch (error: Exception) {
                Log.d("SegmentationActivity", "Произошла ошибка при сохранении изображения")
                Toast.makeText(
                    this,
                    "Произошла ошибка при сохранении изображения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.N)
    private fun getCapturedImage(uri: Uri): Bitmap {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        return when (ExifInterface(contentResolver.openInputStream(uri)).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                Matrix().apply { postRotate(90F) },
                true
            )
            ExifInterface.ORIENTATION_ROTATE_180 -> Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                Matrix().apply { postRotate(180F) },
                true
            )
            ExifInterface.ORIENTATION_ROTATE_270 -> Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                Matrix().apply { postRotate(270F) },
                true
            )
            else -> bitmap
        }
    }*/

    private fun detectFace(image: Bitmap): Bitmap {
        val strokeWidth = 15f
        val frame = AndroidFrameConverter().convert(image)
        val mat = OpenCVFrameConverter.ToMat().convert(frame)
        val mutableBitmap: Bitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.setStyle(Paint.Style.STROKE)
        paint.setStrokeWidth(strokeWidth)

        val numberOfFaces = FaceDetection.detectFaces(mat)

        (0 until numberOfFaces.size()).forEach { i ->
            val rect = numberOfFaces[i]
            canvas.drawRect(
                Rect(rect.x(), rect.y(), rect.x() + rect.width(), rect.y() + rect.height()),
                paint
            )
        }

        Toast.makeText(
            this,
            "Лиц распознано: ${numberOfFaces.size().toString()}",
            Toast.LENGTH_LONG
        ).show()

        return mutableBitmap
    }
}