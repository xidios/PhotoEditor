package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cube.*
import kotlin.math.*


class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube)

        cubeToolbar.setNavigationOnClickListener{
            this.finish()
        }
    }
}

class DrawView (context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): View(context, attrs, defStyleAttr, defStyleRes) {
    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr,0)

    private var vertexes = arrayOf(
        arrayOf(-200f, -200f, -200f), arrayOf(-200f, 200f, -200f),
        arrayOf(200f, 200f, -200f), arrayOf(200f, -200f, -200f),
        arrayOf(-200f, -200f, 200f), arrayOf(-200f, 200f, 200f),
        arrayOf(200f, 200f, 200f), arrayOf(200f, -200f, 200f)
    )

    private var faces = arrayOf(
        arrayOf(0, 1, 2, 3), arrayOf(4, 5, 6, 7),
        arrayOf(0, 1, 5, 4), arrayOf(2, 3, 7, 6),
        arrayOf(0, 3, 7, 4), arrayOf(1, 2, 6, 5)
    )

    private var currentX = 0f
    private var currentY = 0f
    private var deepestZ = -200f
    private var paint = Paint()
    private val VERTEX_RADIUS = 15f
    private val LINE_WIDTH = 10f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        rotateY(45f)
        rotateX(160f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())

        drawFaces(canvas)
        drawVertex(canvas)
    }

    private fun rotateX(angle: Float) {
        val sin = sin(toRadians(angle))
        val cos = cos(toRadians(angle))
        var minZ = Float.MAX_VALUE

        for (vertex in vertexes) {
            val y = vertex[1]
            val z = vertex[2]

            vertex[1] = (y * cos - z * sin).toFloat()
            vertex[2] = (z * cos + y * sin).toFloat()

            minZ = min(minZ, vertex[2])
        }

        deepestZ = minZ
    }

    private fun rotateY(angle: Float) {
        val sin = sin(toRadians(angle))
        val cos = cos(toRadians(angle))
        var minZ = Float.MAX_VALUE

        for (vertex in vertexes) {
            val x = vertex[0]
            val z = vertex[2]

            vertex[0] = (x * cos + z * sin).toFloat()
            vertex[2] = (z * cos - x * sin).toFloat()

            minZ = min(minZ, vertex[2])
        }

        deepestZ = minZ
    }

    private fun rotateZ(angle: Float) {
        val sin = sin(toRadians(angle))
        val cos = cos(toRadians(angle))

        for (vertex in vertexes) {
            val x = vertex[0]
            val y = vertex[1]

            vertex[0] = (x * cos - y * sin).toFloat()
            vertex[1] = (y * cos + x * sin).toFloat()
        }
    }

    private fun drawVertex(canvas: Canvas) {
        for (vertex in vertexes) {
            if (vertex[2] > deepestZ) {
                paint.color = Color.BLACK
                if (vertex.contentEquals(vertexes[0])) {
                    paint.color = Color.RED
                }
                paint.style = Paint.Style.FILL
                canvas.drawCircle(vertex[0], vertex[1], VERTEX_RADIUS, paint)
            }
        }
    }

    private fun drawEdge(canvas: Canvas, vertex1: Array<Float>, vertex2: Array<Float>) {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = LINE_WIDTH
        canvas.drawLine(vertex1[0], vertex1[1], vertex2[0], vertex2[1], paint)
    }

    private fun drawFaces(canvas: Canvas) {
        for (face in faces) {
            if (isVisible(face)) {
                for (i in face.indices) {
                    drawEdge(canvas, vertexes[face[i]], vertexes[face[(i + 1) % 4]])
                }
            }
        }
    }

    private fun isVisible(face: Array<Int>): Boolean {
        for (vertexNumber in face) {
            val vertex = vertexes[vertexNumber]
            if (vertex[2] == deepestZ) {
                return false
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//            var coords = vertexes
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                currentX = event.x
                currentY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - currentX
                val deltaY = currentY - event.y
                if (abs(deltaX) > abs(deltaY)) {
                    rotateY(deltaX / 10)
                } else {
                    rotateX(deltaY / 10)
                }
                currentX = event.x
                currentY = event.y
            }
        }
        invalidate()
        return true
    }

    private fun toRadians(angle: Float): Double {
        return PI * angle / 180
    }
}



