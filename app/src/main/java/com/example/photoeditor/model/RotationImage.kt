package com.example.photoeditor.model

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import kotlin.math.*

class RotationImage {
    private var angle: Double = 0.0
    private lateinit var inputCorners: MutableList<Pair<Int, Int>>
    private lateinit var outputCorners: MutableList<Pair<Int, Int>>
    private lateinit var shifts: Pair<Int, Int>
    private var width = 0
    private var height = 0

    fun prepare(bitmap: Bitmap, intAngle: Int, receivedCorners: MutableList<Pair<Int, Int>>?): Pair<Bitmap, MutableList<Pair<Int, Int>>> {
        var newCorners: MutableList<Pair<Int, Int>>? = receivedCorners
        if (receivedCorners == null) {
            val width = bitmap.width
            val height = bitmap.height
            newCorners = mutableListOf(0 to 0, width - 1 to 0, width - 1 to height - 1, 0 to height - 1)
        }

        return rotateImage(bitmap, intAngle, newCorners as MutableList<Pair<Int, Int>>)
    }

    private fun rotateImage(image: Bitmap, intAngle: Int, receivedCorners: MutableList<Pair<Int, Int>>): Pair<Bitmap, MutableList<Pair<Int, Int>>> {
        angle = toRadians(intAngle)
        inputCorners = receivedCorners

        val oldWidth = image.width
        val oldHeight = image.height
        val imagePixels = IntArray(oldHeight * oldWidth)
        image.getPixels(imagePixels, 0, oldWidth, 0, 0, oldWidth, oldHeight)

        setNewCorners()
        setShifts()
        shiftCorners()
        setNewSize()

        val resultPixels = rotate(imagePixels, oldWidth)
        removeBlankSpaces(resultPixels)
        return Pair(createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888), outputCorners)
    }

    private fun setNewCorners() {
        val newCorners: MutableList<Pair<Int, Int>> = mutableListOf()

        for (corner in inputCorners) {
            newCorners.add(getNewCoords(corner, angle))
        }

        outputCorners = newCorners
    }

    private fun getNewCoords(coords: Pair<Int, Int>, angle: Double): Pair<Int, Int> {
        val x = coords.first
        val y = coords.second
        val newX = floor(x * cos(angle) - y * sin(angle)).toInt()
        val newY = floor(x * sin(angle) + y * cos(angle)).toInt()
        return Pair(newX, newY)
    }

    private fun setShifts() {
        var horizontalShift = Int.MAX_VALUE
        var verticalShift = Int.MAX_VALUE

        for (corner in outputCorners) {
            verticalShift = min(verticalShift, corner.second)
            horizontalShift = min(horizontalShift, corner.first)
        }

        shifts = Pair(-horizontalShift, -verticalShift)
    }

    private fun shiftCorners() {
        for (i in outputCorners.indices) {
            val x = outputCorners[i].first + shifts.first
            val y = outputCorners[i].second + shifts.second
            outputCorners[i] = Pair(x, y)
        }
    }

    private fun setNewSize() {
        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE

        for (corner in outputCorners) {
            minX = min(minX, corner.first)
            maxX = max(maxX, corner.first)
            minY = min(minY, corner.second)
            maxY = max(maxY, corner.second)
        }

        val width = maxX - minX
        val height = maxY - minY

        this.width = width + 1
        this.height = height + 1
    }

    private fun rotate(imagePixels: IntArray, oldWidth: Int): IntArray {
        val resultPixels = IntArray(width * height)
        for (i in resultPixels.indices) {
            resultPixels[i] = -1
        }

        for (i in imagePixels.indices) {
            val y = i / oldWidth
            val x = i % oldWidth
            if (isInImageRectangle(x, y, inputCorners)) {
                val newCoords = getNewCoords(Pair(x, y), angle)
                val newIndex = (newCoords.first + shifts.first) + (newCoords.second + shifts.second) * width
                resultPixels[newIndex] = imagePixels[i]
            }
        }

        return resultPixels
    }

    private fun isInImageRectangle(x: Int, y: Int, corners: MutableList<Pair<Int, Int>>): Boolean {
        val p1 = dotsProduct(corners[0], corners[1], x, y)
        val p2 = dotsProduct(corners[1], corners[2], x, y)
        val p3 = dotsProduct(corners[2], corners[3], x, y)
        val p4 = dotsProduct(corners[3], corners[0], x, y)
        return (p1 >= 0 && p2 >= 0 && p3 >= 0 && p4 >= 0) || (p1 <= 0 && p2 <= 0 && p3 <= 0 && p4 <= 0)
    }

    private fun dotsProduct(a: Pair<Int, Int>, b: Pair<Int, Int>, x: Int, y: Int): Int {
        val x1 = a.first
        val y1 = a.second
        val x2 = b.first
        val y2 = b.second
        return (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1)
    }

    private fun removeBlankSpaces(resultPixels: IntArray) {
        for (i in resultPixels.indices) {
            val y = i / width
            val x = i % width
            val pixel = resultPixels[i]
            if (pixel == -1) {
                if (isInImageRectangle(x, y, outputCorners)) {
                    resultPixels[i] = getMeanColor(resultPixels, x, y, width, height)
                }
            }
        }
    }

    private fun getMeanColor(pixelColors: IntArray, x: Int, y: Int, width: Int, height: Int): Int {
        var count = 0
        var red = 0
        var green = 0
        var blue = 0

        for (i in max(0, x - 1)..min(x + 1, width - 1)) {
            for (j in max(0, y - 1)..min(y + 1, height - 1)) {
                val index = j * width + i
                val pixel = pixelColors[index]
                if (pixel != -1) {
                    red += Color.red(pixel)
                    green += Color.green(pixel)
                    blue += Color.blue(pixel)
                    count++
                }
            }
        }

        return if (count != 0) {
            red /= count
            green /= count
            blue /= count
            Color.argb(255, red, green, blue)
        } else {
            -1
        }
    }

    private fun toRadians(angle: Int): Double {
        return -angle * PI / 180
    }
}
