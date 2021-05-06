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
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
            imageViewScaling.setImageBitmap(NewPhoto)
        }
        buttonScalingApply.setOnClickListener() {
            PhotoOnSave = resizeBitmap(NewPhoto)
            imageViewScaling.setImageBitmap(PhotoOnSave)
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
            val resultIntent = Intent()
            try {
                resultIntent.putExtra(RESULT_TAG, compressBitmap(PhotoOnSave))
                setResult(RESULT_OK, resultIntent)
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()

            } catch (error: Exception) {
                Log.d(
                    "ScalingActivity",
                    "Произошла ошибка при сохранении изображения" + error.message
                )
            }
        }

        Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")

//        seekBarScaling.setMax(NewPhoto.height+1)
//        seekBarScaling.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//
//            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
//                val per = NewPhoto.height - i + 1
//                imageViewScaling.setImageDrawable(null)
//                PhotoOnSave = resizeBitmap(NewPhoto,per)
//                imageViewScaling.setImageBitmap(PhotoOnSave)
//                Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//
//
//            }
//        })
    }

    fun resizeBitmap(source: Bitmap): Bitmap {
        var k: Double = EditTextScaling.text.toString().toDouble()
        var maxLength = source.height * k
        try {
            if (source.height >= source.width) {

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result =
                    Bitmap.createScaledBitmap(source, targetWidth, maxLength.toInt(), false)
                return result
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result =
                    Bitmap.createScaledBitmap(source, maxLength.toInt(), targetHeight, false)
                return result
            }
        } catch (e: Exception) {
            return source
        }
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