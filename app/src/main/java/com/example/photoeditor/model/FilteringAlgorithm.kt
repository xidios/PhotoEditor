package com.example.photoeditor.model

import android.graphics.Bitmap

class FilteringAlgorithm {
    companion object {
        fun runAlgorithm(bitmap: Bitmap, tag: String): Bitmap {
            val output = FilteringAlgorithm()
            val width = bitmap.width
            val height = bitmap.height

            return when (tag) {
                "blackAndWhiteFilter" -> output.blackAndWhiteFilter(width, height, bitmap)
                "sepiaFilter" -> output.sepiaFilter(width, height, bitmap)
                "redFilter" -> output.redFilter(width, height, bitmap)
                "negativeFilter" -> output.negativeFilter(width, height, bitmap)
                else -> bitmap
            }
        }
    }

    fun blackAndWhiteFilter(width: Int, height: Int, photoBitmap: Bitmap): Bitmap {
        val srcPixelMatrix = IntArray(width * height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        val pixelSeparator = 255 / 2 * 3
        var sourcePixels: Int
        var totalColor: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                sourcePixels = srcPixelMatrix[width * y + x]

                r = sourcePixels shr 16 and 0xff
                g = sourcePixels shr 8 and 0xff
                b = sourcePixels and 0xff
                totalColor = r + g + b

                if (totalColor > pixelSeparator) {
                    srcPixelMatrix[width * y + x] = -0x1111111
                } else {
                    srcPixelMatrix[width * y + x] = -0x1000000
                }
            }
        }

        return Bitmap.createBitmap(srcPixelMatrix, width, height, Bitmap.Config.ARGB_8888)
    }

    fun sepiaFilter(width: Int, height: Int, photoBitmap: Bitmap): Bitmap {
        val srcPixelMatrix = IntArray(width * height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        val pixelSeparator = 255
        var sourcePixels: Int
        var totalColor: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                sourcePixels = srcPixelMatrix[width * y + x]

                r = sourcePixels shr 16 and 0xff
                g = sourcePixels shr 8 and 0xff
                b = sourcePixels and 0xff

                totalColor = (r + g + b) / 3

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

    fun redFilter(width: Int, height: Int, photoBitmap: Bitmap): Bitmap {
        val srcPixelMatrix = IntArray(width * height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        var sourcePixels: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
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

    fun negativeFilter(width: Int, height: Int, photoBitmap: Bitmap): Bitmap {
        val srcPixelMatrix = IntArray(width * height)
        photoBitmap.getPixels(srcPixelMatrix, 0, width, 0, 0, width, height)

        val pixelSeparator = 255
        var sourcePixels: Int

        var r: Int
        var g: Int
        var b: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
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