package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.text.TextPaint
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splines.*
import kotlinx.android.synthetic.main.activity_splines.view.*

 class SplinesView @JvmOverloads constructor(
     context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
 ) : View(context, attrs, defStyleAttr) {

    var paint = Paint()

    var canvas: Canvas? = null

    override fun onDraw(canva: Canvas) {
        if(canvas == null){
            canvas = canva
        }
       // super.onDraw(canvas)

    }

     class CoordinatePoints(var x: Float, var y: Float, var z: Float)
     lateinit var pointArray: Array<Int>

//     fun drawPointss(x:Float, y: Float){
//         paint.color = Color.BLUE
//         paint.style = Paint.Style.FILL
//         canvas.drawCircle(x, y, 10f, paint)
//     }

     var count = 0


     override fun onTouchEvent(event: MotionEvent?): Boolean {
         when(event?.action) {
             MotionEvent.ACTION_DOWN-> {
                 count++
//                 drawPointss(event.x, event.y)

                 paint.color = Color.BLUE
                 paint.style = Paint.Style.FILL

                 canvas?.drawCircle(event.x, event.y, 50f, paint)

                 invalidate()
             }
         }

//         pointArray = Array(count) { 0 }
         return true
     }
}

