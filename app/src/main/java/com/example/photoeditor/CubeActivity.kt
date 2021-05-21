package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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

        rotateButton.setOnClickListener {
            cubeView.rotateZ()
        }

        resetButton.setOnClickListener {
            cubeView.requestLayout()
        }
    }
}

class DrawView (context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): View(context, attrs, defStyleAttr, defStyleRes) {
    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr,0)

    private lateinit var vertexes: Array<Array<Float>>

    class CoordinateNum(var x: Float, var y: Float, var z: Float)

    private var numberFaces = arrayOf(
        arrayOf(CoordinateNum(0f, 0f,-200f),
                CoordinateNum(0f, -100f,-200f), CoordinateNum(0f, 100f,-200f)),  //1

        arrayOf(CoordinateNum(0f, 0f,200f),
                CoordinateNum(-20f, -100f,200f), CoordinateNum(-20f, 100f,200f),
               CoordinateNum(20f, -100f,200f),  CoordinateNum(20f, 100f,200f)),) //2
//
//        arrayOf(CoordinateNum(0f, -205f,0f),
//                CoordinateNum(0f, -205f,-120f), CoordinateNum(0f, -205f,120f),
//                CoordinateNum(0f, -205f,-120f),   CoordinateNum(0f, -205f,120f),
//                CoordinateNum(100f, -205f,-120f),  CoordinateNum(100f, -205f,120f)), //3
//
//        arrayOf(CoordinateNum(0f, -200f,0f),
//                CoordinateNum(-20f, -100f,-100f), CoordinateNum(-20f, 100f,100f),
//                CoordinateNum(0f, 100f,100f), CoordinateNum(10f, -100f,-100f), CoordinateNum(20f, 100f,-100f)), //4
//
//        arrayOf(CoordinateNum(200f, 0f,0f),
//                CoordinateNum(200f, 100f,50f), CoordinateNum(200f, -100f,0f), CoordinateNum(200f, 100f,-50f)),
//
//        arrayOf(CoordinateNum(-200f, 0f,0f),
//                CoordinateNum(200f, 0f,-100f), CoordinateNum(-200f, 0f,100f), CoordinateNum(-200f, 0f,100f),
//                CoordinateNum(200f, 0f,100f), CoordinateNum(200f, 0f,100f)) //6
//    )

    private var faces = arrayOf(
        arrayOf(0, 1, 2, 3, Color.RED, 0), arrayOf(4, 5, 6, 7, Color.GREEN, 1),
        arrayOf(0, 1, 5, 4, Color.BLUE, 2), arrayOf(2, 3, 7, 6, Color.CYAN, 3),
        arrayOf(0, 3, 7, 4, Color.MAGENTA, 4), arrayOf(1, 2, 6, 5, Color.YELLOW, 5)
    )

    private var currentX = 0f
    private var currentY = 0f
    private var deepestZ = -200f
    private var paint = Paint()
    private val VERTEX_RADIUS = 15f
    private val LINE_WIDTH = 10f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        vertexes = arrayOf(
            arrayOf(-200f, -200f, -200f), arrayOf(-200f, 200f, -200f),
            arrayOf(200f, 200f, -200f), arrayOf(200f, -200f, -200f),
            arrayOf(-200f, -200f, 200f), arrayOf(-200f, 200f, 200f),
            arrayOf(200f, 200f, 200f), arrayOf(200f, -200f, 200f)
        )
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

        for (i in numberFaces.indices) {
            for(j in numberFaces[i].indices){
                val y = numberFaces[i][j].y
                val z = numberFaces[i][j].z

                numberFaces[i][j].y = (y * cos - z * sin).toFloat()
                numberFaces[i][j].z = (z * cos + y * sin).toFloat()

            }
        }
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

        for (i in numberFaces.indices) {
            for(j in numberFaces[i].indices){
                val x = numberFaces[i][j].x
                val z = numberFaces[i][j].z

                numberFaces[i][j].x = (x * cos + z * sin).toFloat()
                numberFaces[i][j].z = (z * cos - x * sin).toFloat()

            }
        }
    }

    fun rotateZ() {
        val angle = 15f
        val sin = sin(toRadians(angle))
        val cos = cos(toRadians(angle))

        for (vertex in vertexes) {
            val x = vertex[0]
            val y = vertex[1]

            vertex[0] = (x * cos - y * sin).toFloat()
            vertex[1] = (y * cos + x * sin).toFloat()
        }

        for (i in numberFaces.indices) {
            for(j in numberFaces[i].indices){
                val x = numberFaces[i][j].x
                val y = numberFaces[i][j].y

                numberFaces[i][j].x = (x * cos - y * sin).toFloat()
                numberFaces[i][j].y = (y * cos + x * sin).toFloat()
            }
        }

        invalidate()
    }

    private fun drawVertex(canvas: Canvas) {
        for (vertex in vertexes) {
            if (vertex[2] > deepestZ) {
                paint.color = Color.BLACK
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
                fillFace(canvas, face)
                for (i in 0..3) {
                    drawEdge(canvas, vertexes[face[i]], vertexes[face[(i + 1) % 4]])
                }
            }
        }
    }

    private fun fillFace(canvas: Canvas, face: Array<Int>) {
        val path = Path()
        path.moveTo(vertexes[face[0]][0], vertexes[face[0]][1])

        for (i in 1..3) {
            path.lineTo(vertexes[face[i]][0], vertexes[face[i]][1])
        }
        path.lineTo(vertexes[face[0]][0], vertexes[face[0]][1])

        paint.color = face[4]
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

        idxNumbers(face[5], canvas)
    }


    private fun idxNumbers(i: Int, canvas: Canvas){
        val path = Path()
        when (i) {
            0 -> {
                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)

                for(j in numberFaces[i].indices){
                    if(j == 0) continue
                    if(numberFaces[i][0].z > 0){
                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
                    }
                }

                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                canvas.drawPath(path, paint)

                path.reset()
            }
            1 -> {
                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)

                for(j in 0..2){
                    if(j == 0 || j == 1) continue
                    if(numberFaces[i][0].z > 0){
                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
                    }
                }

                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                canvas.drawPath(path, paint)

                path.reset()
                path.moveTo(numberFaces[i][3].x, numberFaces[i][3].y)
                for(j in 3..4){
                    if(numberFaces[i][0].z > 0){
                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
                    }
                }

                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                canvas.drawPath(path, paint)

                path.reset()
            }
//            2 -> {
//                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)
//
//                for(j in 0..2){
//                    if(j == 0 || j == 1) continue
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//
//                path.reset()
//                path.moveTo(numberFaces[i][3].x, numberFaces[i][3].y)
//                for(j in 3..4){
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//
//                path.reset()
//                path.moveTo(numberFaces[i][5].x, numberFaces[i][5].y)
//                for(j in 5..6){
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//            }
//            3 -> {
//                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)
//
//                for(j in 0..2){
//                    if(j == 0 || j == 1) continue
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//
//                path.reset()
//
//                path.moveTo(numberFaces[i][3].x, numberFaces[i][3].y)
//                for(j in 3..4){
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//            }
//            4 -> {
//                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)
//                if (numberFaces[i][0].z > 0f) {
//                    for (j in 0..3) {
//                        if (j == 0 || j == 1) continue
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }

//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//
//            }
//            5 -> {
//                path.moveTo(numberFaces[i][1].x, numberFaces[i][1].y)
//
//                for(j in 0..3){
//                    if(j == 0 || j == 1) continue
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//
//                path.reset()
//                for(j in 4..5){
//                    if(numberFaces[i][0].z > 0){
//                        path.lineTo(numberFaces[i][j].x, numberFaces[i][j].y)
//                    }
//                }
//
//                paint.color = Color.BLACK
//                paint.style = Paint.Style.STROKE
//                canvas.drawPath(path, paint)
//            }
        }
    }


    private fun isVisible(face: Array<Int>): Boolean {
        for (i in 0..3) {
            val vertex = vertexes[face[i]]

            if (vertex[2] == deepestZ) {
                return false
            }
        }

        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
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



