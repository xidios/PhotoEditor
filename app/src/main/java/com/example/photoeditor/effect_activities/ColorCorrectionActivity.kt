package com.example.photoeditor.effect_activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.R
import com.example.photoeditor.model.FilteringAlgorithm
import com.example.photoeditor.model.Tools
import kotlinx.android.synthetic.main.activity_color_correction.*
import java.util.*

class ColorCorrectionActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > Filters"

    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_correction)

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                filtersImage.setImageURI(currentUri)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, e.toString())
            this.finish()
        }

        colorCorrectionToolbar.setNavigationOnClickListener {
            this.finish()
        }

        blackAndWhiteFilter.setOnClickListener { applyFilter("blackAndWhiteFilter") }
        sepiaFilter.setOnClickListener { applyFilter("sepiaFilter") }
        redFilter.setOnClickListener { applyFilter("redFilter") }
        negativeFilter.setOnClickListener { applyFilter("negativeFilter") }

        undoButton.setOnClickListener {
            if (history.isEmpty()) {
                Toast.makeText(this, R.string.empty_history_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            filtersImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun applyFilter(tag: String) {
        var photoBitmap: Bitmap = (filtersImage.drawable as BitmapDrawable).bitmap

        try {
            photoBitmap = FilteringAlgorithm.runAlgorithm(photoBitmap, tag)
            Log.d(DEBUG_TAG, "Алгоритм выполнен")
        } catch (error: Exception) {
            Log.d(DEBUG_TAG, "Произошла ошибка при работе алгоритма: $error")
            Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
        }

        try {
            history.push(currentUri)
            currentUri = Tools.saveTempImage(this, photoBitmap) as Uri
            filtersImage.setImageBitmap(photoBitmap)

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


