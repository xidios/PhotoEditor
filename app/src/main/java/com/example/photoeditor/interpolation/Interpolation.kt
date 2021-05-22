package com.example.photoeditor.interpolation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import com.example.photoeditor.R
import com.example.photoeditor.model.Tools.Companion.saveTempImage
import com.example.photoeditor.scaling.model.ScalingAlgorithm
import kotlinx.android.synthetic.main.activity_interpolation.*

class Interpolation : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private lateinit var NewPhoto: Bitmap
    private lateinit var PhotoOnSave: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interpolation)

        interpolationToolbar.setNavigationOnClickListener {
            this.finish()
        }


        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
        if (receivedImage != null) {
            interpolationImage.setImageURI(receivedImage as Uri)
            NewPhoto = (interpolationImage.getDrawable() as BitmapDrawable).bitmap
            PhotoOnSave = NewPhoto
        }

        applyBilInterpolationButton.setOnClickListener() {
            try {
                    PhotoOnSave = ScalingAlgorithm().bilinearInterpolation(NewPhoto, 1.0)

            } catch (e: Exception) {
                Toast.makeText(this, "Неудалось масштабировать изображение", Toast.LENGTH_SHORT)
                    .show()
            }
            interpolationImage.setImageBitmap(PhotoOnSave)

        }
        applyTrilInterpolationButton.setOnClickListener(){
            try {
                PhotoOnSave = ScalingAlgorithm().trilinearInterpolation(NewPhoto, 1.0)

            } catch (e: Exception) {
                Toast.makeText(this, "Неудалось масштабировать изображение", Toast.LENGTH_SHORT)
                    .show()
            }
            interpolationImage.setImageBitmap(PhotoOnSave)
        }
        interpolationSaveButton.setOnClickListener() {
            try {
                val resultIntent = Intent()
                resultIntent.putExtra(RESULT_TAG, saveTempImage(this, PhotoOnSave))
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }


}