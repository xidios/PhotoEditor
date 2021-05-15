package com.example.photoeditor

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_retouching.*
import kotlin.math.sqrt

class RetouchingActivity : AppCompatActivity() {
    private val RESULT_TAG = "resultImage"
    private lateinit var originalPhoto: Bitmap
    private lateinit var workingPhoto: Bitmap

    private var receivedImage: ByteArray? = null

    private var widthPhoto: Float = 0.0f
    private var heightPhoto: Float = 0.0f

    private var stepRadius = 90
    private var stepStrength = 100

    private var prevPixel: Int = 0

    private var scale = 0.0f
    private var widthBitmap = 0.0f
    private var heightBitmap = 0.0f


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        widthPhoto = imageViewRetouching.width.toFloat()
        heightPhoto = imageViewRetouching.height.toFloat()

        receivedImage = intent.getByteArrayExtra("IMAGE")
        if (receivedImage != null) {
            originalPhoto = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage!!.size)
            workingPhoto = originalPhoto
            imageViewRetouching.setImageBitmap(originalPhoto)
        }

        textSeekBarCoef.text = "Strength: ${seekBarCoef.progress}%"
        textSeekBarRadius.text = "Radius: ${seekBarRadius.progress}"

        seekBarCoef.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                textSeekBarCoef.text = "Radius: $idx"
                textSeekBarCoef.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                textSeekBarCoef.isSelected = false
                stepStrength = seekBarCoef.progress
            }
        })

        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                textSeekBarCoef.text = "Radius: $idx"
                textSeekBarCoef.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                textSeekBarCoef.isSelected = false
                stepRadius = seekBarCoef.progress
            }
        })

        imageViewRetouching.setOnTouchListener {v, e ->
            if (e.action == MotionEvent.ACTION_MOVE || e.action == MotionEvent.ACTION_DOWN){
                mouseEditing()

                val motionTouchEventX = (e.x - (widthPhoto - scale * widthBitmap) / 2.0F) / scale
                val motionTouchEventY = (e.y - (heightPhoto - scale * heightBitmap) / 2.0F) / scale

                workingPhoto = retouchingPhoto(motionTouchEventX.toInt(), motionTouchEventY.toInt(), workingPhoto)

                imageViewRetouching.setImageBitmap(workingPhoto)
            }
            true
        }

        acceptChanging.setOnClickListener {
            imageViewRetouching.setImageDrawable(null)
            imageViewRetouching.setImageBitmap(workingPhoto)
        }

        cancelChanging.setOnClickListener {
            imageViewRetouching.setImageDrawable(null)
            imageViewRetouching.setImageBitmap(originalPhoto)
        }
    }

    private fun mouseEditing() {
        widthBitmap = workingPhoto.width.toFloat()
        heightBitmap = workingPhoto.height.toFloat()

        val scaleW = widthBitmap / widthPhoto
        val scaleH = heightBitmap / heightPhoto

        scale = if (scaleW > scaleH) {
            widthPhoto / widthBitmap
        } else {
            heightPhoto / heightBitmap
        }
    }

    private fun retouchingPhoto(centerX: Int, centerY: Int, imageBitmap: Bitmap): Bitmap {
        var matrixPixels = IntArray(imageBitmap.width * imageBitmap.height)

        val colorAverage = calculatingAverageColors(matrixPixels, centerY, centerX, imageBitmap.width, imageBitmap.height)

        matrixPixels = calculatingColorsWithCoeff(matrixPixels, centerY, centerX, imageBitmap.width, imageBitmap.height, colorAverage[0], colorAverage[1], colorAverage[2])

        return Bitmap.createBitmap(matrixPixels, imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
    }

    private fun calculatingAverageColors(matrix: IntArray, yC: Int, xC: Int, width: Int, height: Int): Array<Double> {
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

    private fun calculatingColorsWithCoeff(matrix: IntArray, yC: Int, xC: Int, width: Int, height: Int, redA: Double, greenA: Double, blueA: Double): IntArray {
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

                k = (1.0 - sqrt((((x - xC) * (x - xC)) + ((y - yC) * (y - yC))).toDouble()) / stepRadius) * (stepStrength.toDouble() / 100)

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
