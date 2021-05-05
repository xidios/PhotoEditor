package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.algorithms.RotationImage
import kotlinx.android.synthetic.main.activity_rotation.*

class RotationActivity : AppCompatActivity() {
    private val RESULT_TAG = "resultImage"
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        rotationToolbar.setNavigationOnClickListener{
            this.finish()
        }

        try {
            val receivedImage = intent.getByteArrayExtra("IMAGE")
            if (receivedImage != null) {
                image = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
                rotationImage.setImageBitmap(image)
            }
        } catch (e: Exception) {
            Log.d("RotationActivity", e.toString())
        }

        applyRotationButton.setOnClickListener {
            rotate()
        }
    }

    private fun rotate() {
        val angle = rotationAnglePicker.text.toString().toIntOrNull()
        val rotate = RotationImage()

        if (angle == null) {
            Toast.makeText(this, "Введите корректный угол", Toast.LENGTH_SHORT).show()
        } else {
            var bitmap = image as Bitmap
            try {
                bitmap = rotate.rotateImage(bitmap, angle)
            } catch(error: Exception) {
                Log.d("RotationActivity", "Произошла ошибка при работе алгоритма")
                Toast.makeText(this, "Произошла ошибка при работе алгоритма", Toast.LENGTH_SHORT).show()
            }
            rotationImage.setImageDrawable(null)
            rotationImage.setImageBitmap(bitmap)

            val resultIntent = Intent()
            try {
                resultIntent.putExtra(RESULT_TAG, compressBitmap(bitmap))
                setResult(RESULT_OK, resultIntent)
            } catch (error: Exception) {
                Log.d("RotationActivity", "Произошла ошибка при сжатии изображения")
                Toast.makeText(this, "Произошла ошибка при сжатии изображения", Toast.LENGTH_SHORT).show()
            }
        }
    }
}