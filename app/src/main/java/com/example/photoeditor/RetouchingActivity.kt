package com.example.photoeditor

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View.MeasureSpec
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_retouching.*
import com.example.photoeditor.algorithms.RetouchingImage


@Suppress("DEPRECATION")
class RetouchingActivity : AppCompatActivity() {
    private val KEY = "Image"

    private lateinit var originalPhoto: Bitmap
    private lateinit var workingPhoto: Bitmap

    private var receivedImage: ByteArray? = null

    private var widthPhoto: Float = 0.0f
    private var heightPhoto: Float = 0.0f

    private var stepRadius = 80
    private var stepStrength = 60

    private var scale = 0.0f
    private var widthBitmap = 0.0f
    private var heightBitmap = 0.0f

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        widthPhoto = retouchImage.width.toFloat()
        heightPhoto = retouchImage.height.toFloat()

        //TODO(Исправить получение изображения в ретуши)
        val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)

        if (receivedImage != null) {
            retouchImage.setImageURI(receivedImage as Uri)

            retouchImage.isDrawingCacheEnabled = true
            retouchImage.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            retouchImage.layout(
                0, 0,
                retouchImage.measuredWidth, retouchImage.measuredHeight
            )
            retouchImage.buildDrawingCache(true)

            originalPhoto = Bitmap.createBitmap(retouchImage.drawingCache)
            retouchImage.isDrawingCacheEnabled = false

            workingPhoto = originalPhoto
        }

        radiusTextView.text = "Радиус размытия: ${radiusPicker.progress}px"
        strengthTextView.text = "Эффект: ${strengthPicker.progress}%"

        strengthPicker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                strengthTextView.text = "Strength: $idx%"
                strengthTextView.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strengthTextView.isSelected = false
                stepStrength = strengthPicker.progress
            }
        })

        radiusPicker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                radiusTextView.text = "Radius: ${idx}px"
                radiusTextView.isSelected = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radiusTextView.isSelected = false
                stepRadius = radiusPicker.progress
            }
        })

        retouchImage.setOnTouchListener { v, e ->
            if (e.action == MotionEvent.ACTION_MOVE || e.action == MotionEvent.ACTION_DOWN) {
                scale = mouseEditing(retouchImage.width.toFloat(), retouchImage.height.toFloat())

                val motionTouchEventX =
                    (e.x - (retouchImage.width.toFloat() - scale * widthBitmap) / 2.0F) / scale
                val motionTouchEventY =
                    (e.y - (retouchImage.height.toFloat() - scale * heightBitmap) / 2.0F) / scale

                val output = RetouchingImage()

                output.stepRadius = stepRadius
                output.stepStrength = stepStrength

                workingPhoto = output.retouchingPhoto(
                    motionTouchEventX.toInt(),
                    motionTouchEventY.toInt(),
                    workingPhoto
                )

                retouchImage.setImageBitmap(workingPhoto)
            }
            true
        }

        acceptChanging.setOnClickListener {
            retouchImage.setImageDrawable(null)
            retouchImage.setImageBitmap(workingPhoto)
        }

        cancelChanging.setOnClickListener {
            retouchImage.setImageDrawable(null)
            workingPhoto = originalPhoto
            retouchImage.setImageBitmap(workingPhoto)
        }
    }

    private fun mouseEditing(widthPhoto: Float, heightPhoto: Float): Float {
        widthBitmap = workingPhoto.width.toFloat()
        heightBitmap = workingPhoto.height.toFloat()

        val scaleW = widthBitmap / widthPhoto
        val scaleH = heightBitmap / heightPhoto

        scale = if (scaleW > scaleH) {
            widthPhoto / widthBitmap
        } else {
            heightPhoto / heightBitmap
        }

        return scale
    }

}



