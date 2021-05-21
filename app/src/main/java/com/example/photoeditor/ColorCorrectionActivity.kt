package com.example.photoeditor

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_correction.*
import com.example.photoeditor.algorithms.FilteringImage

class ColorCorrectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_correction)

        val image = intent.getParcelableExtra<Parcelable>("Image")
        filtersImage.setImageURI(image as Uri)

        var photoBitmap: Bitmap = (filtersImage.drawable as BitmapDrawable).bitmap
        val originalPhoto: Bitmap = (filtersImage.drawable as BitmapDrawable).bitmap

        val width = photoBitmap.width
        val height = photoBitmap.height

        val output = FilteringImage()

        var arrActions = arrayListOf<Bitmap>(originalPhoto)

        blackAndWhiteFilter.setOnClickListener {
            photoBitmap = output.blackAndWhiteFilter(width, height, photoBitmap)
            arrActions.add(arrActions.size, photoBitmap)

            filtersImage.setImageBitmap(photoBitmap)
        }

        sepiaFilter.setOnClickListener {
            photoBitmap = output.sepiaFilter(width, height, photoBitmap)

            arrActions = checkingSize(arrActions)
            arrActions.add(arrActions.size, photoBitmap)

            filtersImage.setImageBitmap(photoBitmap)
        }

        redFilter.setOnClickListener {
            photoBitmap = output.redFilter(width, height, photoBitmap)

            arrActions = checkingSize(arrActions)
            arrActions.add(arrActions.size, photoBitmap)

            filtersImage.setImageBitmap(photoBitmap)
        }

        negativeFilter.setOnClickListener {
            photoBitmap = output.negativeFilter(width, height, photoBitmap)

            arrActions = checkingSize(arrActions)
            arrActions.add(arrActions.size, photoBitmap)

            filtersImage.setImageBitmap(photoBitmap)
        }

        cancelFilter.setOnClickListener {
            when (arrActions.size) {
                0 -> {
                    filtersImage.setImageBitmap(arrActions[0])
                }
                1 -> {
                    filtersImage.setImageBitmap(arrActions[0])
                }
                else -> {
                    filtersImage.setImageBitmap(arrActions[arrActions.size - 2])
                    arrActions.removeAt(arrActions.size - 1)
                }
            }
        }
    }

    private fun checkingSize(arr: ArrayList<Bitmap>): ArrayList<Bitmap>{
        if(arr.size == 15){
            arr.removeAt(1)
        }

        return arr
    }
}


