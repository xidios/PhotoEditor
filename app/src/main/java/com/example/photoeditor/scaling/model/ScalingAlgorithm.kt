package com.example.photoeditor.scaling.model

import android.graphics.Bitmap
import android.graphics.Color

class ScalingAlgorithm {

    private fun resizeBitmap(source: Bitmap, k: Double): Bitmap {
        if (k < 0.01) {
            //Toast.makeText(this, "Минимальное значение 0.01", Toast.LENGTH_SHORT).show()
            return source
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
                if (source.width <= maxLength) {
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result =
                    Bitmap.createScaledBitmap(source, maxLength.toInt(), targetHeight, false)
                //Toast.makeText(this, "Изображение масштабировано", Toast.LENGTH_SHORT).show()
                return result
            }
        } catch (e: Exception) {
            return source
        }
    }

    public fun bilinearInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        if (bitmap.height * bitmap.width * k * k >= 26214400) {
            //Toast.makeText(this, "Введите коэффициент поменьше", Toast.LENGTH_SHORT).show()
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
                val x = (xCoef * j).toInt()
                val y = (yCoef * i).toInt()

                val xNew = xCoef * j - x
                val yNew = yCoef * i - y

                index = y * bitmap.width + x
                val p1 = pixels[index]
                val p2 = pixels[index + 1]
                val p3 = pixels[index + bitmap.width]
                val p4 = pixels[index + bitmap.width + 1]

                val blue =
                    (p1 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 and 0xff) * xNew * (1 - yNew) + (p3 and 0xff) * yNew * (1 - xNew) + (p4 and 0xff) * (xNew * yNew)
                val green =
                    (p1 shr 8 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 shr 8 and 0xff) * xNew * (1 - yNew) + (p3 shr 8 and 0xff) * yNew * (1 - xNew) + (p4 shr 8 and 0xff) * (xNew * yNew)
                val red =
                    (p1 shr 16 and 0xff) * (1 - xNew) * (1 - yNew) + (p2 shr 16 and 0xff) * xNew * (1 - yNew) + (p3 shr 16 and 0xff) * yNew * (1 - xNew) + (p4 shr 16 and 0xff) * (xNew * yNew)

                newImageArray[newIndex++] = Color.rgb(red.toInt(), green.toInt(), blue.toInt())
            }
        }
        //Toast.makeText(this, "Изображение масштабировано", Toast.LENGTH_SHORT).show()
        return Bitmap.createBitmap(newImageArray, neww, newh, Bitmap.Config.ARGB_8888)
    }

    public fun trilinearInterpolation(bitmap: Bitmap, k: Double): Bitmap {
        if (k < 0.01) {
            //Toast.makeText(this, "Минимальное значение 0.01", Toast.LENGTH_SHORT).show()
            return bitmap
        }
        var m = 1.0
        while (k < m / 2) {
            m /= 2
        }
        val m2 = m / 2

        val bitmapM = bilinearInterpolation(bitmap, m)
        val bitmap2M = bilinearInterpolation(bitmap, m2)


        val bitmapMArray = IntArray(bitmapM.width * bitmapM.height)
        val bitmap2MArray = IntArray(bitmap2M.width * bitmap2M.height)
        bitmapM.getPixels(bitmapMArray, 0, bitmapM.width, 0, 0, bitmapM.width, bitmapM.height)
        bitmap2M.getPixels(bitmap2MArray, 0, bitmap2M.width, 0, 0, bitmap2M.width, bitmap2M.height)

        val neww = (bitmap.width * k).toInt()
        val newh = (bitmap.height * k).toInt()
        val newArray = IntArray(neww * newh)
        var index = 0

        for (i in 0 until neww) {
            for (j in 0 until newh) {
                val mX = ((i / k).toInt() * m).toInt()
                val mY = ((j / k).toInt() * m).toInt()

                val m2X = ((i / k).toInt() * m2).toInt()
                val m2Y = ((j / k).toInt() * m2).toInt()

                val mIndex = (mX * bitmapM.height + mY)
                val m2Index = (m2X * bitmap2M.height + m2Y)

                var pixelM = 0
                var pixel2M = 0
                try {
                    pixelM = bitmapMArray[mIndex]
                    pixel2M = bitmap2MArray[m2Index]
                } catch (e: Exception) {
                    pixelM = bitmapMArray[mIndex - 1]
                    pixel2M = bitmap2MArray[m2Index - 1]
                }


                val rM = Color.red(pixelM)
                val gM = Color.green(pixelM)
                val bM = Color.blue(pixelM)


                val r2M = Color.red(pixel2M)
                val g2M = Color.green(pixel2M)
                val b2M = Color.blue(pixel2M)


                val r = (rM * (1 / m2 - k) + r2M * (k - (1 / m))) / (1 / m)
                val g = (gM * (1 / m2 - k) + g2M * (k - (1 / m))) / (1 / m)
                val b = (bM * (1 / m2 - k) + b2M * (k - (1 / m))) / (1 / m)


                newArray[index++] = Color.rgb(r.toInt(), g.toInt(), b.toInt())

            }
        }


        //Toast.makeText(this, "Изображение масштабировано", Toast.LENGTH_SHORT).show()
        return Bitmap.createBitmap(newArray, neww, newh, Bitmap.Config.ARGB_8888)
    }
}