package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_retouching.*


class RetouchingActivity : AppCompatActivity() {
    private val RESULT_TAG = "resultImage"
    private lateinit var NewPhoto: Bitmap
    private lateinit var PhotoOnSave: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        val receivedImage = intent.getByteArrayExtra("IMAGE")
        if (receivedImage != null) {
            NewPhoto = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
            PhotoOnSave = NewPhoto
            imageViewRetouching.setImageBitmap(NewPhoto)

        }
        imageViewRetouching.setOnTouchListener { v, e ->

            if (e.action == MotionEvent.ACTION_MOVE || e.action == MotionEvent.ACTION_DOWN) {
                val x: Float = e.x
                val y: Float = e.y
                // The coordinates of the target point
                // The coordinates of the target point
                val dst = FloatArray(2)
                // Get the matrix of ImageView
                // Get the matrix of ImageView
                val imageMatrix: Matrix = imageViewRetouching.getImageMatrix()
                // Create an inverse matrix
                // Create an inverse matrix
                val inverseMatrix = Matrix()
                // Inverse, the inverse matrix is ​​assigned
                // Inverse, the inverse matrix is ​​assigned
                imageMatrix.invert(inverseMatrix)
                // Get the value of the target point dst through the inverse matrix mapping
                // Get the value of the target point dst through the inverse matrix mapping
                inverseMatrix.mapPoints(dst, floatArrayOf(x, y))
                Toast.makeText(this, "$dst[0] $dst[1]", Toast.LENGTH_SHORT).show()
                //NewPhoto = retouching(dst[0], dst[1], NewPhoto)
                imageViewRetouching.setImageBitmap(NewPhoto)
            }

            true

        }

    }


}
