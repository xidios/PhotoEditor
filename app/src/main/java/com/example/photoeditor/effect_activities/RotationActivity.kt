package com.example.photoeditor.effect_activities

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.R
import com.example.photoeditor.model.RotationAlgorithm
import com.example.photoeditor.model.Tools
import kotlinx.android.synthetic.main.activity_rotation.*
import java.util.*

class RotationActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > Rotation"

    private var corners: MutableList<Pair<Int, Int>>? = null
    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        rotationToolbar.setNavigationOnClickListener {
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                rotationImage.setImageURI(currentUri)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, e.toString())
            this.finish()
        }

        applyRotationButton.setOnClickListener {
            val angle = rotationAnglePicker.text.toString().toIntOrNull()
            if (angle == null) {
                Toast.makeText(this, R.string.incorrect_angle_toast, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rotate(angle)
        }

        undoButton.setOnClickListener {
            if (history.isEmpty()) {
                Toast.makeText(this, R.string.empty_history_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            rotationImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun rotate(angle: Int) {
        var bitmap = (rotationImage.drawable as BitmapDrawable).bitmap

        try {
            val received = RotationAlgorithm.runAlgorithm(bitmap, angle, corners)
            bitmap = received.first
            corners = received.second
            Log.d(DEBUG_TAG, "Алгоритм выполнен")
        } catch (error: Exception) {
            Log.d(DEBUG_TAG, "Произошла ошибка при работе алгоритма: $error")
            Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
        }

        try {
            history.push(currentUri)
            currentUri = Tools.saveTempImage(this, bitmap) as Uri
            rotationImage.setImageBitmap(bitmap)

            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        } catch (error: Exception) {
            Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения")
            Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy() {
        Tools.clearHistory(this, history)
        super.onDestroy()
    }
}