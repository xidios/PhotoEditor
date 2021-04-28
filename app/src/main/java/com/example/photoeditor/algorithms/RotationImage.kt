package com.example.photoeditor.algorithms

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.math.*
import org.jetbrains.anko.toast

class RotationImage() {
    fun rotateImage(image: Bitmap, angle: Int): Bitmap {
        val angle = toRadians(angle)
        val width = image.width
        val height = image.height
        val imagePixels = IntArray(height * width)
        image.getPixels(imagePixels, 0, width, 0, 0, width, height)

        val corners = mutableListOf(0 to 0, width - 1 to 0, 0 to height - 1, width - 1 to height - 1)
        val newCorners = getNewCorners(corners, angle)

        val shifts = getShifts(newCorners)
        val newSize = getNewSize(newCorners)
        val newWidth = newSize.first
        val newHeight = newSize.second

        val resultPixels = IntArray(newWidth * newHeight)

        for (i in imagePixels.indices) {
            val y = i / width
            val x = i % width
            val newCoords = getNewCoords(Pair(x, y), angle)
            val newIndex = (newCoords.first + shifts.first) + (newCoords.second + shifts.second) * newWidth
            resultPixels[newIndex] = imagePixels[i]
        }

        Log.d("debug", "$newWidth, $newHeight")

        val result = createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        result.setPixels(resultPixels, 0, newWidth, 0, 0, newWidth, newHeight)

        return result
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
