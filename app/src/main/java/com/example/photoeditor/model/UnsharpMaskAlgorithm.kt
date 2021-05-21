package com.example.photoeditor.model

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import java.lang.Math.*

class UnsharpMaskAlgorithm {

    fun getResult(input: Bitmap, radius: Int, amount: Double): Bitmap {
        var output = gaussianBlur(input, radius)
        output = subtraction(input, output)
        output = multiplication(output, amount)
        output = addition(input, output)
        return output
    }

    private fun gaussianBlur(input: Bitmap, sigma: Int): Bitmap {
        val size = 3 * sigma
        val width = input.width
        val height = input.height

        val coefficients = getCoefficients(size, sigma)
        val output = createBitmap(width, height, input.config)

        var pixelColors = arrayOf<IntArray>()
        for (x in 0 until width) {
            val row = IntArray(height)
            for (y in 0 until height) {
                row[y] = input.getPixel(x, y)
            }
            pixelColors += row
        }

        val temp = pixelColors.clone()

        for (y in 0 until height) {
            for (x in 0 until width) {
                temp[x][y] = getGaussianMeanColor(pixelColors, coefficients, x, y, width, height, size, "horizontal")
            }
        }

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = getGaussianMeanColor(temp, coefficients, x, y, width, height, size, "vertical")
                output.setPixel(x, y, color)
            }
        }

        return output
    }

    private fun getGaussianMeanColor(colors: Array<IntArray>, coefficients: DoubleArray, x: Int, y: Int,
                                     width: Int, height: Int, size: Int, mode: String): Int {
        var alpha = 0.0
        var red = 0.0
        var green = 0.0
        var blue = 0.0
        var sum = 0.0

        for (i in coefficients.indices) {
            if (mode == "horizontal") {
                val j = x + i - size
                if (j in 0 until width) {
                    val pixel = colors[j][y]
                    alpha += Color.alpha(pixel) * coefficients[i]
                    red += Color.red(pixel) * coefficients[i]
                    green += Color.green(pixel) * coefficients[i]
                    blue += Color.blue(pixel) * coefficients[i]
                    sum += coefficients[i]
                }
            } else {
                val j = y + i - size
                if (j in 0 until height) {
                    val pixel = colors[x][j]
                    alpha += Color.alpha(pixel) * coefficients[i]
                    red += Color.red(pixel) * coefficients[i]
                    green += Color.green(pixel) * coefficients[i]
                    blue += Color.blue(pixel) * coefficients[i]
                    sum += coefficients[i]
                }
            }
        }

        alpha /= sum
        red /= sum
        green /= sum
        blue /= sum

        return Color.argb(alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())
    }

    private fun getCoefficients(size: Int, sigma: Int): DoubleArray {
        val result = DoubleArray(2 * size + 1)
        result[size] = 0.0
        for (i in 1..size) {
            val j = i + size
            result[j] = exp((-i*i/(2*sigma*sigma)).toDouble())
            result[size - i] = result[j]
        }
        return result
    }

    private fun subtraction(input: Bitmap, smoothed: Bitmap): Bitmap {
        val width = input.width
        val height = input.height
        val output = createBitmap(width, height, input.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = input.getPixel(x, y)
                val smoothedPixel = smoothed.getPixel(x, y)

                val red = max(0, Color.red(pixel) - Color.red(smoothedPixel))
                val green = max(0, Color.green(pixel) - Color.green(smoothedPixel))
                val blue = max(0, Color.blue(pixel) - Color.blue(smoothedPixel))
                val resultPixel = Color.argb(255, red, green, blue)

                output.setPixel(x, y, resultPixel)
            }
        }

        return output
    }

    private fun multiplication(input: Bitmap, amount: Double): Bitmap {
        val width = input.width
        val height = input.height
        val output = createBitmap(width, height, input.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = input.getPixel(x, y)
                val red = Color.red(pixel) * amount
                val green = Color.green(pixel) * amount
                val blue = Color.blue(pixel) * amount
                val resultPixel = Color.argb(255, red.toInt(), green.toInt(), blue.toInt())
                output.setPixel(x, y, resultPixel)
            }
        }

        return output
    }

    private fun addition(input: Bitmap, edited: Bitmap): Bitmap {
        val width = input.width
        val height = input.height
        val output = createBitmap(width, height, input.config)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = input.getPixel(x, y)
                val editedPixel = edited.getPixel(x, y)
                val red = min(255, Color.red(pixel) + Color.red(editedPixel))
                val green = min(255, Color.green(pixel) + Color.green(editedPixel))
                val blue = min(255, Color.blue(pixel) + Color.blue(editedPixel))
                val resultPixel = Color.argb(255, red, green, blue)
                output.setPixel(x, y, resultPixel)
            }
        }

        return output
    }
}