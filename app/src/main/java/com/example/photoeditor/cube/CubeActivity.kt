package com.example.photoeditor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cube.*

class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube)

        cubeToolbar.setNavigationOnClickListener{
            this.finish()
        }

        rotateButton.setOnClickListener {
            cubeView.rotateZ()
        }

        resetButton.setOnClickListener {
            cubeView.requestLayout()
        }
    }
}



