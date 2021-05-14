package com.example.photoeditor

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.algorithms.RotationImage
import kotlinx.android.synthetic.main.activity_rotation.*

class RotationActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private var corners: MutableList<Pair<Int, Int>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        rotationToolbar.setNavigationOnClickListener{
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                rotationImage.setImageURI(receivedImage as Uri)
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
            var bitmap = (rotationImage.drawable as BitmapDrawable).bitmap
            if (corners == null) {
                val width = bitmap.width
                val height = bitmap.height
                corners = mutableListOf(0 to 0, width - 1 to 0, width - 1 to height - 1, 0 to height - 1)
            }
            try {
                val received = rotate.rotateImage(bitmap, angle, corners as MutableList<Pair<Int, Int>>)
                bitmap = received.first
                corners = received.second
                Log.d("RotationActivity", "Алгоритм успешно выполнен")
            } catch(error: Exception) {
                Log.d("RotationActivity", "Произошла ошибка при работе алгоритма ${error.toString()}")
                Toast.makeText(this, "Произошла ошибка при работе алгоритма", Toast.LENGTH_SHORT).show()
            }
            rotationImage.setImageDrawable(null)
            rotationImage.setImageBitmap(bitmap)

            val resultIntent = Intent()
            try {
                resultIntent.putExtra(RESULT_TAG, saveTempImage(this, bitmap))
                setResult(RESULT_OK, resultIntent)
            } catch (error: Exception) {
                Log.d("RotationActivity", "Произошла ошибка при сохранении изображения")
                Toast.makeText(this, "Произошла ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
            }
        }
    }
}