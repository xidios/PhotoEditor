package com.example.photoeditor.algorithms

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.util.Log
import java.lang.Math.*

class UnsharpMaskAlgorithm {
    fun blur(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = createBitmap(width, height, bitmap.config)

        var pixelColors = arrayOf<IntArray>()
        for (x in 0 until width) {
            val row = IntArray(height)
            for (y in 0 until height) {
                row[y] = bitmap.getPixel(x, y)
            }
            pixelColors += row
        }

        for (x in 0 until width) {
            for (y in 0 until height) {
                try {
                    val newColor = getMeanColor(pixelColors, x, y, width, height)
                    result.setPixel(x, y, newColor)
                } catch (e: Exception) {
                    Log.d("UnsharpMaskActivity", "$x, $y, ${e.toString()}")
                }
            }
        }

        return result
    }

    private fun getMeanColor(pixelColors: Array<IntArray>, x: Int, y: Int, width: Int, height: Int): Int {
        var count = 0
        var alpha = 0
        var red = 0
        var green = 0
        var blue = 0

        for (i in max(0, x - 1)..min(x + 1, width - 1)) {
            for (j in max(0, y - 1)..min(y + 1, height - 1)) {
                val pixel = pixelColors[i][j]
                alpha += Color.alpha(pixel)
                red += Color.red(pixel)
                green += Color.green(pixel)
                blue += Color.blue(pixel)
                count++
            }
        }

        alpha /= count
        red /= count
        green /= count
        blue /= count

        return Color.argb(alpha, red, green, blue)
    }
}