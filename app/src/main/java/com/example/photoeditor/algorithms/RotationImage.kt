package com.example.photoeditor.algorithms

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.*

class RotationImage {
    fun rotateImage(image: Bitmap, angle: Int): Bitmap {
        val angle = toRadians(angle)
        val width = image.width
        val height = image.height
        val imagePixels = IntArray(height * width)
        image.getPixels(imagePixels, 0, width, 0, 0, width, height)

        var corners = mutableListOf(0 to 0, width - 1 to 0, width - 1 to height - 1, 0 to height - 1)
        corners = getNewCorners(corners, angle)
        val shifts = getShifts(corners)
        shiftCorners(corners, shifts)

        val newSize = getNewSize(corners)
        val newWidth = newSize.first
        val newHeight = newSize.second

        val resultPixels = IntArray(newWidth * newHeight)
        for (i in resultPixels.indices) {
            resultPixels[i] = 0
        }

        for (i in imagePixels.indices) {
            val y = i / width
            val x = i % width
            val newCoords = getNewCoords(Pair(x, y), angle)
            val newIndex = (newCoords.first + shifts.first) + (newCoords.second + shifts.second) * newWidth
            resultPixels[newIndex] = imagePixels[i]
        }

        removeBlankSpaces(resultPixels, newWidth, newHeight, corners)

        return createBitmap(resultPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888)
    }

    private fun shiftCorners(corners: MutableList<Pair<Int, Int>>, shifts: Pair<Int, Int>) {
        for (i in corners.indices) {
            val x = corners[i].first + shifts.first
            val y = corners[i].second + shifts.second
            corners[i] = Pair(x, y)
        }
    }

    private fun removeBlankSpaces(resultPixels: IntArray, width: Int, height: Int, corners: MutableList<Pair<Int, Int>>) {
        for (i in resultPixels.indices) {
            val y = i / width
            val x = i % width
            val pixel = resultPixels[i]
            if (pixel == 0) {
                if (isInImage(x, y, corners)) {
                    resultPixels[i] = getMeanColor(resultPixels, x, y, width, height)
                }
            }
        }
    }

    private fun isInImage(x: Int, y: Int, corners: MutableList<Pair<Int, Int>>): Boolean {
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

    private fun getMeanColor(pixelColors: IntArray, x: Int, y: Int, width: Int, height: Int): Int {
        var count = 0
        var red = 0
        var green = 0
        var blue = 0

        for (i in max(0, x - 1)..min(x + 1, width - 1)) {
            for (j in max(0, y - 1)..min(y + 1, height - 1)) {
                val index = j * width + i
                val pixel = pixelColors[index]
                if (pixel != 0) {
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
            0
        }
    }

    private fun toRadians(angle: Int): Double {
        return -angle * PI / 180
    }

    private fun getNewCorners(corners: MutableList<Pair<Int, Int>>, angle: Double): MutableList<Pair<Int, Int>> {
        val newCorners: MutableList<Pair<Int, Int>> = mutableListOf()

        for (corner in corners) {
            newCorners.add(getNewCoords(corner, angle))
        }

        return newCorners
    }

    private fun getNewCoords(coords: Pair<Int, Int>, angle: Double): Pair<Int, Int> {
        val x = coords.first
        val y = coords.second
        val newX = (x * cos(angle) - y * sin(angle)).roundToInt()
        val newY = (x * sin(angle) + y * cos(angle)).roundToInt()
        return Pair(newX, newY)
    }

    private fun getShifts(corners: MutableList<Pair<Int, Int>>): Pair<Int, Int> {
        var horizontalShift = 0
        var verticalShift = 0
        for (corner in corners) {
            verticalShift = min(verticalShift, corner.second)
            horizontalShift = min(horizontalShift, corner.first)
        }
        return Pair(abs(horizontalShift), abs(verticalShift))
    }

    private fun getNewSize(corners: MutableList<Pair<Int, Int>>): Pair<Int, Int> {
        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE

        for (corner in corners) {
            minX = min(minX, corner.first)
            maxX = max(maxX, corner.first)
            minY = min(minY, corner.second)
            maxY = max(maxY, corner.second)
        }

        val width = maxX - minX
        val height = maxY - minY
        return Pair(width + 1, height + 1)
    }
}
