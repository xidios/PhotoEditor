package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.net.Uri
import android.widget.Toast
import com.example.photoeditor.algorithms.RotationImage
import kotlinx.android.synthetic.main.activity_rotation.*
import org.jetbrains.anko.toast

class RotationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        rotationToolbar.setNavigationOnClickListener{
            finish()
        }
        rotationImage.setImageURI(intent.getParcelableExtra<Parcelable>("Image") as Uri)

        applyRotationButton.setOnClickListener {rotate()}
    }

    private fun rotate() {
        val angle = rotationAnglePicker.text.toString().toIntOrNull()
        val rotate = RotationImage()

        if (angle == null) {
            Toast.makeText(this, "Введите корректный угол", Toast.LENGTH_SHORT).show()
        } else {
            var bitmap: Bitmap = (rotationImage.drawable as BitmapDrawable).bitmap
            try {
                bitmap = rotate.rotateImage(bitmap, angle)
            }
            catch(error: Exception) {
                Toast.makeText(this, "Ты лох", Toast.LENGTH_SHORT).show()
            }
            rotationImage.setImageBitmap(bitmap)
        }
    }

    override fun finish() {
        toast("I'm closed")
        super.finish()
    }
}