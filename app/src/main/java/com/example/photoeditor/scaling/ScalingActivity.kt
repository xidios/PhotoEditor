package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.example.photoeditor.scaling.model.ScalingAlgorithm
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.activity_unsharp_mask.*
import kotlinx.android.synthetic.main.fragment_save.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.ArrayDeque
import kotlin.math.floor

class ScalingActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private lateinit var NewPhoto: Bitmap
    private lateinit var PhotoOnSave: Bitmap
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
            NewPhoto = (scalingImage.getDrawable() as BitmapDrawable).bitmap
            PhotoOnSave = NewPhoto
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
        }

        applyScalingButton.setOnClickListener() {
            var k: Double = scalingAnglePicker.text.toString().toDouble()
            try {
                if (k < 1) {
                    PhotoOnSave = ScalingAlgorithm().trilinearInterpolation(NewPhoto, k)
                } else if (k > 1) {
                    PhotoOnSave = ScalingAlgorithm().bilinearInterpolation(NewPhoto, k)
                } else {
                    PhotoOnSave = NewPhoto
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Неудалось масштабировать изображение", Toast.LENGTH_SHORT)
                    .show()
            }
            scalingImage.setImageBitmap(PhotoOnSave)
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")

        }
        scalingSaveButton.setOnClickListener() {
            try {
                currentUri = history.pop()
                scalingImage.setImageURI(currentUri)
                resultIntent.putExtra(RESULT_TAG, currentUri)
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")

    }




}