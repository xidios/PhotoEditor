package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.algorithms.UnsharpMaskAlgorithm
import kotlinx.android.synthetic.main.activity_unsharp_mask.*


class UnsharpMaskActivity : AppCompatActivity() {
    private val KEY = "Image"
    private val RESULT_TAG = "resultImage"
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_mask)

        unsharpToolbar.setNavigationOnClickListener{
            this.finish()
        }

        try {
            val receivedImage = intent.getParcelableExtra<Parcelable>(KEY)
            if (receivedImage != null) {
                unsharpImage.setImageURI(receivedImage as Uri)
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

        val radius = smoothRadiusPicker.progress + 1
        val amount = amountPicker.progress / 100.0

        Log.d("UnsharpMaskActivity", "$radius, $amount")

        var bitmap = (unsharpImage.getDrawable() as BitmapDrawable).bitmap
        try {
            bitmap = unsharp.getResult(bitmap, radius, amount)
            Log.d("UnsharpMaskActivity", "Алгоритм выполнен")
            Toast.makeText(this, "Алгоритм выполнен", Toast.LENGTH_SHORT).show()
        } catch(error: Exception) {
            Log.d("UnsharpMaskActivity", "Произошла ошибка при выполнении алгоритма: ${error.toString()}")
            Toast.makeText(this, "Произошла ошибка при выполнении алгоритма", Toast.LENGTH_SHORT).show()
        }

        unsharpImage.setImageDrawable(null)
        unsharpImage.setImageBitmap(bitmap)

        val resultIntent = Intent()
        try {
            resultIntent.putExtra(RESULT_TAG, saveTempImage(this, bitmap))
            setResult(RESULT_OK, resultIntent)
        } catch (error: Exception) {
            Log.d("UnsharpMaskActivity", "Произошла ошибка при сохранении изображения")
            Toast.makeText(this, "Произошла ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
        }
    }
}