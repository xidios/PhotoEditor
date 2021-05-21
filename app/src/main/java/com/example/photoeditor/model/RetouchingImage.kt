package com.example.photoeditor.algorithms

import android.graphics.Bitmap
import kotlin.math.sqrt

class RetouchingImage {
    private var prevPixel: Int = 0

    var stepRadius = 80
    var stepStrength = 60

    fun retouchingPhoto(centerX: Int, centerY: Int, imageBitmap: Bitmap): Bitmap {
        var matrixPixels = IntArray(imageBitmap.width * imageBitmap.height)
        imageBitmap.getPixels(
            matrixPixels,
            0,
            imageBitmap.width,
            0,
            0,
            imageBitmap.width,
            imageBitmap.height
        )

        val colorAverage = calculatingAverageColors(
            matrixPixels,
            centerY,
            centerX,
            imageBitmap.width,
            imageBitmap.height
        )

        matrixPixels = calculatingColorsWithCoeff(
            matrixPixels,
            centerY,
            centerX,
            imageBitmap.width,
            imageBitmap.height,
            colorAverage[0],
            colorAverage[1],
            colorAverage[2]
        )

        return Bitmap.createBitmap(
            matrixPixels, imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888
        )
    }

    private fun calculatingAverageColors(
        matrix: IntArray,
        yC: Int,
        xC: Int,
        width: Int,
        height: Int
    ): Array<Double> {
        var red = 0.0
        var green = 0.0
        var blue = 0.0

        var counter = 0

        for (y in (yC - stepRadius) until (yC + stepRadius)) {
            if (y < 0 || y >= height) {
                continue
            }

            for (x in (xC - stepRadius) until (xC + stepRadius)) {
                if (x < 0 || x >= width) {
                    continue
                }

                if (sqrt((((x - xC) * (x - xC)) + ((y - yC) * (y - yC))).toDouble()).toInt() > stepRadius) {
                    continue
                }

                prevPixel = matrix[width * y + x]

                red += (prevPixel shr 16 and 0xff)
                green += (prevPixel shr 8 and 0xff)
                blue += (prevPixel and 0xff)

                counter++
            }
        }

        return arrayOf(red / counter, green / counter, blue / counter)
    }

    private fun calculatingColorsWithCoeff(
        matrix: IntArray,
        yC: Int,
        xC: Int,
        width: Int,
        height: Int,
        redA: Double,
        greenA: Double,
        blueA: Double
    ): IntArray {
        var k: Double

        var red: Int
        var green: Int
        var blue: Int

        for (y in (yC - stepRadius) until (yC + stepRadius)) {

            if (y < 0 || y >= height) {
                continue
            }

            for (x in (xC - stepRadius) until (xC + stepRadius)) {

                if (x < 0 || x >= width) {
                    continue
                }

                if (sqrt((((x - xC) * (x - xC)) + ((y - yC) * (y - yC))).toDouble()).toInt() >= stepRadius) {
                    continue
                }

                k =
                    (1.0 - sqrt((((x - xC) * (x - xC)) + ((y - yC) * (y - yC))).toDouble()) / stepRadius) * (stepStrength.toDouble() / 100)

                prevPixel = matrix[width * y + x]

                red = (prevPixel shr 16 and 0xff)
                green = (prevPixel shr 8 and 0xff)
                blue = (prevPixel and 0xff)

                red = averagingColorChannel(red, redA, k)
                green = averagingColorChannel(green, greenA, k)
                blue = averagingColorChannel(blue, blueA, k)

                matrix[width * y + x] = (-0x1000000) or (red shl 16) or (green shl 8) or blue
            }
        }

        return matrix
    }

    private fun averagingColorChannel(color: Int, colorAverage: Double, k: Double): Int {
        var colorResult = color

        if (colorAverage > colorResult) {
            colorResult += ((colorAverage - colorResult) * k).toInt()
        } else if (colorAverage < colorResult) {
            colorResult -= ((colorResult - colorAverage) * k).toInt()
        }

        return colorResult
    }

}