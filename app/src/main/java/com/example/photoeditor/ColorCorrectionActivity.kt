package com.example.photoeditor

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_correction.*

class ColorCorrectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_correction)

        val image = intent.getParcelableExtra<Parcelable>("ImageF")
        imageViewFilter.setImageURI(image as Uri)

        var photoBitmap: Bitmap = (imageViewFilter.drawable as BitmapDrawable).bitmap
        var width = photoBitmap.width
        var height = photoBitmap.height

        fWB.setOnClickListener {
            var photoBitmapTemp : Bitmap = blackAndWhiteFilter(width, height, photoBitmap)

            imageViewFilter.setImageDrawable(null)
            imageViewFilter.setImageBitmap(photoBitmapTemp)

        }

        fSepia.setOnClickListener {
            var photoBitmapTemp : Bitmap = sepiaFilter(width, height, photoBitmap)

            imageViewFilter.setImageDrawable(null)
            imageViewFilter.setImageBitmap(photoBitmapTemp)
        }

        fRed.setOnClickListener {
            var photoBitmapTemp : Bitmap = redFilter(width, height, photoBitmap)

            imageViewFilter.setImageDrawable(null)
            imageViewFilter.setImageBitmap(photoBitmapTemp)
        }

        fNegative.setOnClickListener {
            var photoBitmapTemp : Bitmap = negativeFilter(width, height, photoBitmap)

            imageViewFilter.setImageDrawable(null)
            imageViewFilter.setImageBitmap(photoBitmapTemp)
        }
    }

    private fun  blackAndWhiteFilter(width: Int, height:Int, photoBitmap: Bitmap): Bitmap {

        var srcPixelMatrix  = IntArray(width*height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        var pixelSeparator = 255/2 * 3
        var sourcePixels: Int
        var totalColor: Int

        var r: Int
        var g: Int
        var b: Int

        for(y in 0 until height){
            for(x in 0 until width){
                sourcePixels = srcPixelMatrix[width * y + x]

                r = sourcePixels shr 16 and 0xff
                g = sourcePixels shr 8 and 0xff
                b = sourcePixels and 0xff
                totalColor = r + g + b

                if (totalColor > pixelSeparator) {
                    srcPixelMatrix[width * y + x] = -0x1111111
                }

                else {
                    srcPixelMatrix[width * y + x] = -0x1000000
                }
            }
        }

        return Bitmap.createBitmap(srcPixelMatrix, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun  sepiaFilter(width: Int, height:Int, photoBitmap: Bitmap): Bitmap {

        var srcPixelMatrix = IntArray(width*height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        var pixelSeparator = 255
        var sourcePixels: Int
        var totalColor: Int

        var r: Int
        var g: Int
        var b: Int

        for(y in 0 until height){
            for(x in 0 until width){
                sourcePixels = srcPixelMatrix[width * y + x]

                r = sourcePixels shr 16 and 0xff
                g = sourcePixels shr 8 and 0xff
                b = sourcePixels and 0xff

                totalColor = (r + g + b)/3

                b = totalColor
                g = b
                r = g
                r += 40
                g += 20

                if (r > pixelSeparator) {
                    r = pixelSeparator
                }
                if (g > pixelSeparator) {
                    g = pixelSeparator
                }

                srcPixelMatrix[width * y + x] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(srcPixelMatrix, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun  redFilter(width: Int, height:Int, photoBitmap: Bitmap): Bitmap {

        var srcPixelMatrix = IntArray(width*height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        var sourcePixels: Int

        var r: Int
        var g: Int
        var b: Int

        for(y in 0 until height){
            for(x in 0 until width){
                sourcePixels = srcPixelMatrix[width * y + x]

                r = (sourcePixels shr 16 and 0xff) + 10

                if (r > 255)
                    r = 255

                g = sourcePixels shr 8 and 0xff
                b = sourcePixels and 0xff


                srcPixelMatrix[width * y + x] = -0x10000 or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(srcPixelMatrix, width, height, Bitmap.Config.ARGB_8888)
    }


    private fun  negativeFilter(width: Int, height:Int, photoBitmap: Bitmap): Bitmap {

        var srcPixelMatrix = IntArray(width*height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        var pixelSeparator = 255
        var sourcePixels: Int

        var r: Int
        var g: Int
        var b: Int

        for(y in 0 until height){
            for(x in 0 until width){
                sourcePixels = srcPixelMatrix[width * y + x]

                r = pixelSeparator - (sourcePixels shr 16 and 0xff)
                g = pixelSeparator - (sourcePixels shr 8 and 0xff)
                b = pixelSeparator - (sourcePixels and 0xff)

                srcPixelMatrix[width * y + x] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        }

        return Bitmap.createBitmap(srcPixelMatrix, width, height, Bitmap.Config.ARGB_8888)
    }
}


