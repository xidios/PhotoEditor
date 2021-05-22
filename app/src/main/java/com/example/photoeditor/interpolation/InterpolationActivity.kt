package com.example.photoeditor.interpolation

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
import com.example.photoeditor.model.Tools
import com.example.photoeditor.scaling.model.ScalingAlgorithm
import kotlinx.android.synthetic.main.activity_interpolation.*
import kotlinx.android.synthetic.main.activity_interpolation.undoButton
import kotlinx.android.synthetic.main.activity_scaling.*
import java.util.*

class InterpolationActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > Interpolation"

    private lateinit var newPhoto: Bitmap
    private lateinit var photoOnSave: Bitmap
    private val resultIntent = Intent()
    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interpolation)

        interpolationToolbar.setNavigationOnClickListener {
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                interpolationImage.setImageURI(currentUri)
                newPhoto = (interpolationImage.drawable as BitmapDrawable).bitmap
                photoOnSave = newPhoto
            }
        } catch(e: Exception) {
            Toast.makeText(this, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, e.toString())
            this.finish()
        }

        applyBilInterpolationButton.setOnClickListener() {
            try {
                photoOnSave = ScalingAlgorithm().bilinearInterpolation(newPhoto, 1.0)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
            }

            try {
                history.push(currentUri)
                currentUri = Tools.saveTempImage(this, photoOnSave)
                interpolationImage.setImageBitmap(photoOnSave)
            } catch (e: Exception) {
                Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения: $e")
                Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT).show()
            }

        }
        applyTrilInterpolationButton.setOnClickListener(){
            try {
                photoOnSave = ScalingAlgorithm().trilinearInterpolation(newPhoto, 1.0)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.algorithm_error_message, Toast.LENGTH_SHORT).show()
            }

            try {
                history.push(currentUri)
                currentUri = Tools.saveTempImage(this, photoOnSave)
                interpolationImage.setImageBitmap(photoOnSave)
            } catch (e: Exception) {
                Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения: $e")
                Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT).show()
            }
        }

        undoButton.setOnClickListener() {
            if (history.isEmpty()) {
                Toast.makeText(this, R.string.empty_history_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            interpolationImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }
    }

    override fun onDestroy() {
        Tools.clearHistory(this, history)
        super.onDestroy()
    }
}