package com.example.photoeditor.splines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.R
import kotlinx.android.synthetic.main.activity_splines.*

class SplinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splines)

        applyAlgorithm.setOnClickListener { splinesView.convertingToSpline() }
        deletePoint.setOnClickListener { splinesView.deletePoints = 1 }

        splinesToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }
}


