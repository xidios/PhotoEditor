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
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlin.math.floor

class ScalingActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private lateinit var NewPhoto: Bitmap
    private lateinit var PhotoOnSave: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        scalingToolbar.setNavigationOnClickListener {
            this.finish()
        }

        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
        if (receivedImage != null) {
            scalingImage.setImageURI(receivedImage as Uri)
            NewPhoto = (scalingImage.drawable as BitmapDrawable).bitmap
            PhotoOnSave = NewPhoto
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
        }
        buttonScalingApply.setOnClickListener() {
            PhotoOnSave = bilinearInterpolation(NewPhoto)
            scalingImage.setImageBitmap(PhotoOnSave)
            Log.d("ScalingActivity", "${PhotoOnSave.height}  ${PhotoOnSave.width}")
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_TAG, Tools.saveTempImage(this, PhotoOnSave))
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()

//            } catch (error: Exception) {
//                Log.d(
//                    "ScalingActivity",
//                    "Произошла ошибка при сохранении изображения" + error.message
//                )
//            }
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
        var k: Double = scalePicker.text.toString().toDouble()
        if (k < 0.01) {
            Toast.makeText(this, "Минимальное значение 0.01", Toast.LENGTH_SHORT).show()
            return PhotoOnSave
        }
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

    fun bilinearInterpolation(bitmap: Bitmap): Bitmap {
        var k: Double = scalePicker.text.toString().toDouble()

        var oldw: Int = bitmap.width
        var oldh: Int = bitmap.height
        var oldArray = IntArray(oldw * oldh)
        bitmap.getPixels(oldArray, 0, oldw, 0, 0, oldw, oldh)

        var neww: Int = (oldw * k).toInt()
        var newh: Int = (oldh * k).toInt()

        val x_koef = (oldw - 1).toDouble() / neww
        val y_koef = (oldh - 1).toDouble() / newh

        var newArray = IntArray(neww * newh)

        for (j in 0 until newh) {
            var tmp: Double = (j / (newh - 1) * (oldh - 1)).toDouble()
            var h: Int = floor(tmp).toInt()
            if (h < 0) {
                h = 0
            } else {
                if (h >= oldh - 1) {
                    h = oldh - 2
                }
            }
            var u = tmp - h

            var y = (y_koef * j).toInt()
            for (i in 0 until neww) {
                var x = (x_koef * i).toInt()
                var index = y * bitmap.width + x


                var tmp: Double = (i / (neww - 1) * (oldw - 1)).toDouble()
                var w = floor(tmp).toInt()
                if (w < 0)
                    w = 0
                else {
                    if (w >= oldw - 1) {
                        w = oldw - 2

                    }
                }
                var t = tmp - w
                var d1 = (1 - t) * (1 - u)
                var d2 = t * (1 - u)
                var d3 = t * u
                var d4 = (1 - t) * u


                var p1 = oldArray[index]
                var p2 = oldArray[index + 1]
                var p3 = oldArray[index + oldw - 1]
                var p4 = oldArray[index + oldw]

                var blue =
                    p1 * d1 + p2 * d2 + p3 * d3 + p4 * d4
                var green =
                    (p1 shr 8) * d1 + (p2 shr 8) * d2 + (p3 shr 8) * d3 + (p4 shr 8) * d4
                var red =
                    (p1 shr 16) * d1 + (p2 shr 16) * d2 + (p3 shr 1) * d3 + (p4 shr 16) * d4
                newArray[j * newh + i] =
                    (red.toInt() shl 16) or (green.toInt() shl 8) or blue.toInt()
            }
        }
        return Bitmap.createBitmap(newArray, neww, newh, Bitmap.Config.ARGB_8888)
    }

}