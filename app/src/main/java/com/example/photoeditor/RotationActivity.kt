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
import java.util.*

class RotationActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private var corners: MutableList<Pair<Int, Int>>? = null
    private lateinit var currentUri: Uri
    private var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        rotationToolbar.setNavigationOnClickListener{
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                rotationImage.setImageURI(currentUri)
            }
        } catch (e: Exception) {
            Log.d("RotationActivity", e.toString())
            this.finish()
        }

        applyRotationButton.setOnClickListener {
            rotate()
        }

        cancelChanging.setOnClickListener {
            if (history.isEmpty()) {
                Toast.makeText(this, "История изменений пуста", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            deleteFile(currentUri)
            currentUri = history.pop()
            rotationImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun rotate() {
        val angle = rotationAnglePicker.text.toString().toIntOrNull()
        val rotate = RotationImage()

        if (angle == null) {
            Toast.makeText(this, "Введите корректный угол", Toast.LENGTH_SHORT).show()
            return
        }

        var bitmap = (rotationImage.drawable as BitmapDrawable).bitmap
        try {
            val received = rotate.prepare(bitmap, angle, corners)
            bitmap = received.first
            corners = received.second
            Log.d("RotationActivity", "Алгоритм успешно выполнен")
        } catch(error: Exception) {
            Log.d("RotationActivity", "Произошла ошибка при работе алгоритма ${error.toString()}")
            Toast.makeText(this, "Произошла ошибка при работе алгоритма", Toast.LENGTH_SHORT).show()
        }

        try {
            history.push(currentUri)
            currentUri = saveTempImage(this, bitmap) as Uri
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
            rotationImage.setImageBitmap(bitmap)
        } catch (error: Exception) {
            Log.d("RotationActivity", "Произошла ошибка при сохранении изображения")
            Toast.makeText(this, "Произошла ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        for (image in history) {
            deleteFile(image)
//            Log.d("RotationActivity", image.toString())
        }
        super.onDestroy()
    }

    private fun deleteFile(image: Uri) {

    }
}