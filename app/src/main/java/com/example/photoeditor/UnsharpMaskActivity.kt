package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.photoeditor.algorithms.UnsharpMaskAlgorithm
import kotlinx.android.synthetic.main.activity_rotation.*
import kotlinx.android.synthetic.main.activity_unsharp_mask.*

class UnsharpMaskActivity : AppCompatActivity() {
    private val RESULT_TAG = "resultImage"
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_mask)

        unsharpToolbar.setNavigationOnClickListener{
            this.finish()
        }

        try {
            val receivedImage = intent.getByteArrayExtra("IMAGE")
            if (receivedImage != null) {
                image = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.size)
                unsharpImage.setImageBitmap(image)
            }
        } catch (e: Exception) {
            Log.d("UnsharpMaskActivity", e.toString())
        }

        applyUnsharpMaskButton.setOnClickListener {
            unsharp()
        }
    }

    private fun unsharp() {
        val unsharp = UnsharpMaskAlgorithm()

        /*var radius = smoothRadiusPicker.progress
        val scaling = 0.5*/

        var bitmap = image as Bitmap
        try {
            bitmap = unsharp.blur(bitmap)
        } catch(error: Exception) {
            Log.d("UnsharpMaskActivity", "Произошла ошибка при размытии")
            Toast.makeText(this, "Произошла ошибка при размытии", Toast.LENGTH_SHORT).show()
        }

        Log.d("UnsharpMaskActivity", "Размытие выполнено")

        unsharpImage.setImageDrawable(null)
        unsharpImage.setImageBitmap(bitmap)

        val resultIntent = Intent()
        try {
            resultIntent.putExtra(RESULT_TAG, compressBitmap(bitmap))
            setResult(RESULT_OK, resultIntent)
        } catch (error: Exception) {
            Log.d("UnsharpMaskActivity", "Произошла ошибка при сжатии изображения")
            Toast.makeText(this, "Произошла ошибка при сжатии изображения", Toast.LENGTH_SHORT).show()
        }
    }
}