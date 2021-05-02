package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.fragment_save.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScalingActivity : AppCompatActivity() {

    private lateinit var NewPhoto: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        var k: Int

        val image = intent.getParcelableExtra<Parcelable>("Image")
        imageViewScaling.setImageURI(image as Uri)
        var photoBitmap: Bitmap = (imageViewScaling.drawable as BitmapDrawable).bitmap
        NewPhoto = photoBitmap


        seekBarScaling.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val temp = 100 - i
                imageViewScaling.setImageDrawable(null)
                imageViewScaling.setImageBitmap(Scale(temp))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {


            }
        })

//        buttonScaling.setOnClickListener() {
//            k = seekBarScaling.progress
//            val toast = Toast.makeText(
//                applicationContext,
//                k.toString(),
//                Toast.LENGTH_SHORT
//            )
//            if (k > 0) {
//                toast.show()
//                imageViewScaling.setImageDrawable(null)
//                imageViewScaling.setImageBitmap(zoom(k))
//            } else {
//                imageViewScaling.setImageDrawable(null)
//                imageViewScaling.setImageBitmap(NewPhoto)
//                Toast.makeText(
//                    applicationContext,
//                    "SHOUTERLGRE BE MORE THSN 0",
//                    Toast.LENGTH_SHORT
//                )
//            }
//        }
    }


    private fun Scale(percentage: Int): Bitmap {
        val newWidth = (NewPhoto.width * percentage / 100)
        val newHeight = (NewPhoto.height * percentage / 100)

        val oldPixels = IntArray(NewPhoto.width * NewPhoto.height)
        NewPhoto.getPixels(oldPixels, 0, NewPhoto.width, 0, 0, NewPhoto.width, NewPhoto.height)

        var offset = 0
        val newPixels = IntArray(newWidth * newHeight)

        val startX: Int = (NewPhoto.width - newWidth) / 2
        val startY: Int = (NewPhoto.height - newHeight) / 2

        for (y in 0 until newHeight) {
            for (x in 0 until newWidth) {
                newPixels[offset++] = oldPixels[NewPhoto.width * (startY + y) + (startX + x)]
            }
        }

        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.ARGB_8888)
    }
}