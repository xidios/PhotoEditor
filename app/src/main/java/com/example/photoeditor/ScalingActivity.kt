package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.fragment_save.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScalingActivity : AppCompatActivity() {
    private val RESULT_TAG = "resultImage"
    private lateinit var NewPhoto: Bitmap
    private lateinit var PhotoOnSave: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        val receivedImage = intent.getByteArrayExtra("IMAGE")
        if (receivedImage != null) {
            NewPhoto = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
            PhotoOnSave = NewPhoto
            imageViewScaling.setImageBitmap(NewPhoto)
        }
        buttonScalingApply.setOnClickListener(){
            val resultIntent = Intent()
            try {
                resultIntent.putExtra(RESULT_TAG, compressBitmap(PhotoOnSave))
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()
            } catch (error: Exception) {
                Log.d("ScalingActivity", "Произошла ошибка при сохранении изображения"+error.message)
            }
        }


        seekBarScaling.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val per = 100 - i
                imageViewScaling.setImageDrawable(null)
                PhotoOnSave = Scale(per)
                imageViewScaling.setImageBitmap(PhotoOnSave)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {


            }
        })
    }


    private fun Scale(percentage: Int): Bitmap {
        val reWidth = (NewPhoto.width * percentage / 100)
        val reHeight = (NewPhoto.height * percentage / 100)

        val oldPixels = IntArray(NewPhoto.width * NewPhoto.height)
        NewPhoto.getPixels(oldPixels, 0, NewPhoto.width, 0, 0, NewPhoto.width, NewPhoto.height)

        var b = 0
        val newPixels = IntArray(reWidth * reHeight)

        val X: Int = (NewPhoto.width - reWidth) / 2
        val Y: Int = (NewPhoto.height - reHeight) / 2

        for (y in 0 until reHeight) {
            for (x in 0 until reWidth) {
                newPixels[b++] = oldPixels[NewPhoto.width * (Y + y) + (X + x)]
            }
        }

        return Bitmap.createBitmap(newPixels, reWidth, reHeight, Bitmap.Config.ARGB_8888)
    }
}