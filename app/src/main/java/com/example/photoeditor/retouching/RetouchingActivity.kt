package com.example.photoeditor.retouching

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.R
import com.example.photoeditor.retouching.model.RetouchingAlgorithm
import com.example.photoeditor.model.Tools
import kotlinx.android.synthetic.main.activity_retouching.*
import kotlinx.android.synthetic.main.activity_retouching.undoButton
import kotlinx.android.synthetic.main.activity_rotation.*
import java.util.*

class RetouchingActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private val DEBUG_TAG = "PhotoEditor > Retouching"

    private lateinit var currentUri: Uri
    var history = ArrayDeque<Uri>()
    private val resultIntent = Intent()
    private lateinit var originalPhoto: Bitmap
    private var currentPhoto: Bitmap? = null
    private lateinit var radiusLabel: String
    private lateinit var strengthLabel: String
    private var radius = 1
    private var strength = 1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                currentUri = receivedImage as Uri
                retouchImage.setImageURI(currentUri)
            }

            retouchImage.isDrawingCacheEnabled = true
            retouchImage.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            retouchImage.layout(
                0, 0,
                retouchImage.measuredWidth, retouchImage.measuredHeight
            )
            retouchImage.buildDrawingCache(true)

            originalPhoto = Bitmap.createBitmap(retouchImage.drawingCache)
            retouchImage.isDrawingCacheEnabled = false
        } catch (e: Exception) {
            Toast.makeText(this, R.string.image_load_error_message, Toast.LENGTH_SHORT).show()
            Log.d(DEBUG_TAG, e.toString())
            this.finish()
        }

        val output = RetouchingAlgorithm()

        radiusLabel = resources.getString(R.string.radius_textView)
        strengthLabel = resources.getString(R.string.amount_textView)
        output.setData(this, originalPhoto)

        radiusPicker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                radiusTextView.text = "$radiusLabel ${idx}px"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                radius = radiusPicker.progress
            }
        })

        strengthPicker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, idx: Int, b: Boolean) {
                strengthTextView.text = "$strengthLabel ${idx}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                strength = strengthPicker.progress
            }
        })

        retouchImage.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_MOVE || e.action == MotionEvent.ACTION_DOWN) {
                if (currentPhoto == null) {
                    currentPhoto = originalPhoto
                }
                currentPhoto = output.runAlgorithm(
                    e.x.toInt(),
                    e.y.toInt(),
                    radius,
                    strength,
                    currentPhoto as Bitmap
                )
                retouchImage.setImageBitmap(currentPhoto)
            }
            true
        }

        applyButton.setOnClickListener {
            val bitmap = (retouchImage.drawable as BitmapDrawable).bitmap
            try {
                history.push(currentUri)
                currentUri = Tools.saveTempImage(this, bitmap) as Uri
                resultIntent.putExtra(RESULT_TAG, currentUri)
                setResult(RESULT_OK, resultIntent)
            } catch (error: Exception) {
                Log.d(DEBUG_TAG, "Произошла ошибка при сохранении изображения")
                Toast.makeText(this, R.string.image_save_error_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        undoButton.setOnClickListener {
            if (history.isEmpty()) {
                retouchImage.setImageBitmap(originalPhoto)
                currentPhoto = originalPhoto
                return@setOnClickListener
            }

            Tools.deleteFile(this, currentUri)
            currentUri = history.pop()
            retouchImage.setImageURI(currentUri)
            resultIntent.putExtra(RESULT_TAG, currentUri)
            setResult(RESULT_OK, resultIntent)
        }

        retouchToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun onDestroy() {
        Tools.clearHistory(this, history)
        super.onDestroy()
    }
}



