package com.example.photoeditor

import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_segmentation.*
import kotlinx.android.synthetic.main.activity_unsharp_mask.*
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter

class SegmentationActivity : AppCompatActivity() {


    private val KEY = "Image"
    private lateinit var uri: Uri
    private val RESULT_TAG = "resultImage"
    private lateinit var image: Bitmap

    //TODO(Исправить получение изображения в сегментации)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        FaceDetection.loadModel(this)
        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
        uri = receivedImage as Uri
        if (receivedImage != null) {

            image = getCapturedImage(uri)
            SegmentationImage.setImageBitmap(image)

        }
        SegmentationToolbar.setNavigationOnClickListener {
            this.finish()
        }
        SegmentationButton.setOnClickListener() {
            image = detectFace(image)
        }
        SaveSegmentationButton.setOnClickListener() {
            val resultIntent = Intent()
            try {
                resultIntent.putExtra(RESULT_TAG, saveTempImage(this, image))
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_LONG).show()
                this.finish()
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

    @RequiresApi(Build.VERSION_CODES.N)
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
    }

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
                Rect(
                    rect.x(),
                    rect.y(),
                    rect.x() + rect.width(),
                    rect.y() + rect.height()
                ), paint
            )
        }
        Toast.makeText(
            this,
            "Лиц распознано: ${numberOfFaces.size().toString()}",
            Toast.LENGTH_LONG
        ).show()

        SegmentationImage.setImageBitmap(mutableBitmap)
        return mutableBitmap
    }
}