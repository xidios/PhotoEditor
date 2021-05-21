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
import com.example.photoeditor.model.Tools
import com.example.photoeditor.model.UnsharpMaskAlgorithm
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.activity_unsharp_mask.*
import kotlinx.android.synthetic.main.activity_unsharp_mask.cancelChanging
import java.util.*

class UnsharpMaskActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > UnsharpMask"

    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_mask)

        unsharpToolbar.setNavigationOnClickListener {
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                unsharpImage.setImageURI(currentUri)
            }
        } catch (e: Exception) {
            Toast.makeText(this, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, e.toString())
            this.finish()
        }

        applyUnsharpMaskButton.setOnClickListener {
            unsharp()
        }

        cancelChanging.setOnClickListener {
            if (history.isEmpty()) {
                Toast.makeText(this, R.string.empty_history_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            unsharpImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun unsharp() {
        val radius = radiusPicker.progress + 1
        val amount = amountPicker.progress / 100.0
        var bitmap = (unsharpImage.drawable as BitmapDrawable).bitmap

        try {
            bitmap = UnsharpMaskAlgorithm.runAlgorithm(bitmap, radius, amount)
            Log.d(DEBUG_TAG, "Алгоритм выполнен")
            Toast.makeText(this, R.string.algorithm_success_message, Toast.LENGTH_SHORT).show()
        } catch (error: Exception) {
            Log.d(DEBUG_TAG, "Произошла ошибка при работе алгоритма: $error")
            Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
        }

        try {
            history.push(currentUri)
            currentUri = Tools.saveTempImage(this, bitmap) as Uri
            unsharpImage.setImageBitmap(bitmap)

            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        } catch (error: Exception) {
            Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения")
            Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        Tools.clearHistory(this, history)
        super.onDestroy()
    }
}