package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
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
            imageViewInterpolation.setImageURI(receivedImage as Uri)
            NewPhoto = (imageViewInterpolation.getDrawable() as BitmapDrawable).bitmap
            PhotoOnSave = NewPhoto
        }

        buttonBilInterpolationApply.setOnClickListener() {
            try {
                    PhotoOnSave = bilinearInterpolation(NewPhoto, 1.0)

            } catch (e: Exception) {
                Toast.makeText(this, "Неудалось масштабировать изображение", Toast.LENGTH_SHORT)
                    .show()
            }
            imageViewInterpolation.setImageBitmap(PhotoOnSave)

        }
        buttonTrilInterpolationApply.setOnClickListener(){
            try {
                PhotoOnSave = trilinearInterpolation(NewPhoto, 1.0)

            } catch (e: Exception) {
                Toast.makeText(this, "Неудалось масштабировать изображение", Toast.LENGTH_SHORT)
                    .show()
            }
            imageViewInterpolation.setImageBitmap(PhotoOnSave)
        }
        buttonInterpolationSave.setOnClickListener() {
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

    private fun bilinearInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        if (bitmap.height * bitmap.width * k * k >= 26214400) {
            Toast.makeText(this, "Введите коэффициент поменьше", Toast.LENGTH_SHORT).show()
            return bitmap
        }

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val neww = (bitmap.width * k).toInt()
        val newh = (bitmap.height * k).toInt()
        val newImageArray = IntArray(neww * newh)

        var index: Int

        val xCoef = (bitmap.width - 1).toDouble() / neww
        val yCoef = (bitmap.height - 1).toDouble() / newh

        var newIndex = 0

        for (i in 0 until newh) {
            for (j in 0 until neww) {
                var x = (xCoef * j).toInt()
                var y = (yCoef * i).toInt()

                var xNew = xCoef * j - x
                var yNew = yCoef * i - y

                index = y * bitmap.width + x
                var p1 = pixels[index]
                var p2 = pixels[index + 1]
                var p3 = pixels[index + bitmap.width]
                var p4 = pixels[index + bitmap.width + 1]

                var blue =
                    (p1 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 and 0xff) * xNew * (1 - yNew) + (p3 and 0xff) * yNew * (1 - xNew) + (p4 and 0xff) * (xNew * yNew)
                var green =
                    (p1 shr 8 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 shr 8 and 0xff) * xNew * (1 - yNew) + (p3 shr 8 and 0xff) * yNew * (1 - xNew) + (p4 shr 8 and 0xff) * (xNew * yNew)
                var red =
                    (p1 shr 16 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 shr 16 and 0xff) * xNew * (1 - yNew) + (p3 shr 16 and 0xff) * yNew * (1 - xNew) + (p4 shr 16 and 0xff) * (xNew * yNew)

                newImageArray[newIndex++] = Color.rgb(red.toInt(), green.toInt(), blue.toInt())
            }
        }
        Toast.makeText(this, "Изображение масштабировано", Toast.LENGTH_SHORT).show()
        return Bitmap.createBitmap(newImageArray, neww, newh, Bitmap.Config.ARGB_8888)
    }

    private fun trilinearInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        if (k < 0.01) {
            Toast.makeText(this, "Минимальное значение 0.01", Toast.LENGTH_SHORT).show()
            return bitmap
        }
        var m = 1.0
        while (k < m / 2) {
            m /= 2
        }
        var m2 = m / 2

        var bitmapM = bilinearInterpolation(bitmap, m)
        var bitmap2M = bilinearInterpolation(bitmap, m2)


        var bitmapMArray = IntArray(bitmapM.width * bitmapM.height)
        var bitmap2MArray = IntArray(bitmap2M.width * bitmap2M.height)
        bitmapM.getPixels(bitmapMArray, 0, bitmapM.width, 0, 0, bitmapM.width, bitmapM.height)
        bitmap2M.getPixels(bitmap2MArray, 0, bitmap2M.width, 0, 0, bitmap2M.width, bitmap2M.height)

        var neww = (bitmap.width * k).toInt()
        var newh = (bitmap.height * k).toInt()
        var newArray = IntArray(neww * newh)
        var index = 0

        for (i in 0 until neww) {
            for (j in 0 until newh) {
                var mX = ((i / k).toInt() * m).toInt()
                var mY = ((j / k).toInt() * m).toInt()

                var m2X = ((i / k).toInt() * m2).toInt()
                var m2Y = ((j / k).toInt() * m2).toInt()

                var mIndex = (mX * bitmapM.height + mY)
                var m2Index = (m2X * bitmap2M.height + m2Y)

                var pixelM = 0
                var pixel2M = 0
                try {
                    pixelM = bitmapMArray[mIndex]
                    pixel2M = bitmap2MArray[m2Index]
                } catch (e: Exception) {
                    pixelM = bitmapMArray[mIndex - 1]
                    pixel2M = bitmap2MArray[m2Index - 1]
                }


                var rM = Color.red(pixelM)
                var gM = Color.green(pixelM)
                var bM = Color.blue(pixelM)


                var r2M = Color.red(pixel2M)
                var g2M = Color.green(pixel2M)
                var b2M = Color.blue(pixel2M)


                var r = (rM * (1 / m2 - k) + r2M * (k - (1 / m))) / (1 / m)
                var g = (gM * (1 / m2 - k) + g2M * (k - (1 / m))) / (1 / m)
                var b = (bM * (1 / m2 - k) + b2M * (k - (1 / m))) / (1 / m)


                newArray[index++] = Color.rgb(r.toInt(), g.toInt(), b.toInt())

            }
        }


        Toast.makeText(this, "Изображение масштабировано", Toast.LENGTH_SHORT).show()
        return Bitmap.createBitmap(newArray, neww, newh, Bitmap.Config.ARGB_8888)
    }
}