package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.model.Tools
import com.example.photoeditor.scaling.model.ScalingAlgorithm
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.activity_scaling.undoButton
import java.util.*

class ScalingActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > Scaling"
    private lateinit var newPhoto: Bitmap
    private lateinit var photoOnSave: Bitmap
    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        scalingToolbar.setNavigationOnClickListener {
            this.finish()
        }

        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
        if (receivedImage != null) {
            currentUri = receivedImage as Uri
            scalingImage.setImageURI(currentUri)
            newPhoto = (scalingImage.drawable as BitmapDrawable).bitmap
            photoOnSave = newPhoto
            Log.d(DEBUG_TAG, "${photoOnSave.height}  ${photoOnSave.width}")
        }

        applyScalingButton.setOnClickListener() {
            var k: Double = scalingAnglePicker.text.toString().toDouble()
            try {
                if (k < 1) {
                    photoOnSave = ScalingAlgorithm().trilinearInterpolation(newPhoto, k)
                } else if (k > 1) {
                    photoOnSave = ScalingAlgorithm().bilinearInterpolation(newPhoto, k)
                } else {
                    photoOnSave = newPhoto
                }
            } catch (e: Exception) {
                Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
            }

            try {
                history.push(currentUri)
                currentUri = Tools.saveTempImage(this, photoOnSave)
                scalingImage.setImageBitmap(photoOnSave)
                resultIntent.putExtra(RESULT_TAG, currentUri)
                setResult(RESULT_OK, resultIntent)
            } catch (e: Exception) {
                Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения")
                Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT).show()
            }
            Log.d(DEBUG_TAG, "${photoOnSave.height}  ${photoOnSave.width}")
        }
        undoButton.setOnClickListener() {
            if (history.isEmpty()) {
                Toast.makeText(this, R.string.empty_history_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            scalingImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }

        Log.d(DEBUG_TAG, "${photoOnSave.height}  ${photoOnSave.width}")
    }

    override fun onDestroy() {
        Tools.clearHistory(this, history)
        super.onDestroy()
    }
}