package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin


class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }

    class DrawView(context: Context?) : View(context) {
        var vertex = arrayOf(
            arrayOf(-400f, -400f, -400f),arrayOf(-400f, -400f,  400f),
            arrayOf(-400f,  400f, -400f),arrayOf(-400f,  400f,  400f),
            arrayOf(400f, -400f, -400f),arrayOf(400f, -400f,  400f),
            arrayOf(400f,  400f, -400f),arrayOf(400f,  400f,  400f))

        var faces = arrayOf(
            arrayOf(0, 1), arrayOf(1, 3),
            arrayOf(3, 2), arrayOf(2, 0),
            arrayOf(4, 5), arrayOf(5, 7),
            arrayOf(7, 6), arrayOf(6, 4),
            arrayOf(0, 4), arrayOf(1, 5),
            arrayOf(2, 6), arrayOf(3, 7))

        var radianX = 60f
        var radianY = 60f

        private fun rotateX(radian: Float){
            var sinX = sin(radian)
            var cosX = cos(radian)

            for (i in vertex.indices) {
                var v = vertex[i]

                var z: Float = v[2]
                var y: Float = v[1]

                v[1] = y * cosX - z * sinX
                v[2] = y * cosX + z * sinX
                vertex[i] = v
            }
        }

        private fun rotateY(radian: Float){
            var sinY = sin(radian)
            var cosY = cos(radian)

            for (i in vertex.indices) {
                var v = vertex[i]

                var z: Float = v[2]
                var x: Float = v[0]

                v[0] = x * cosY + z * sinY
                v[2] = -x * cosY + z * sinY
                vertex[i] = v
            }
        }

        var paint = Paint()
        var path = Path()

        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(Color.DKGRAY)
            canvas.translate((width / 2).toFloat(), (height / 2).toFloat())

            drawFaces(canvas)
            drawVertex(canvas)
        }

        private fun drawFaces(canvas: Canvas){
            for(i in faces.indices){
                var n0 = faces[i][0]
                var n1 = faces[i][1]

                var v0 = vertex[n0]
                var v1 = vertex[n1]

                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 10f

                canvas.drawLine(v0[0], v0[1],v1[0], v1[1], paint)
            }
        }

        private fun drawVertex(canvas: Canvas){
            for(i in vertex.indices){
                var v = vertex[i]

                paint.color = Color.YELLOW
                paint.style = Paint.Style.FILL

                canvas.drawCircle(v[0], v[1], 10f, paint)
            }
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    radianX = event.x
                    radianY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    rotateX((event.x - radianX)/200)
                    rotateY((event.y - radianY)/200)

                    radianX = event.x
                    radianY = event.y

                }
            }
            invalidate()
            return true
        }
    }
}





