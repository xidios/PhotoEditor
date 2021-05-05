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

class Point2D(var x: Float, var y: Float)

class Point3D(var x: Float,var y: Float,var z: Float)

class Cube(var x: Float, var y: Float, var z: Float, var size: Float) {
    var vertex = arrayOf(
        Point3D(x = x - size, y = y - size, z = z - size),
        Point3D(x = x + size, y = y - size, z = z - size),
        Point3D(x = x + size, y = y + size, z = z - size),
        Point3D(x = x - size, y = y + size, z = z - size),
        Point3D(x = x - size, y = y - size, z = z + size),
        Point3D(x = x + size, y = y - size, z = z + size),
        Point3D(x = x + size, y = y + size, z = z + size),
        Point3D(x = x - size, y = y + size, z = z + size)
    )

    var faces = arrayOf(
        arrayOf(0, 1, 2, 3), arrayOf(0, 4, 5, 1),
        arrayOf(1, 5, 6, 2), arrayOf(3, 2, 6, 7),
        arrayOf(0, 3, 7, 4), arrayOf(4, 7, 6, 5)
    )

    fun rotateX(radian: Float) {
        var cosX = cos(radian)
        var sinX = sin(radian)

        for (i in vertex.indices) {
            var p = this.vertex[i]

            var y = (p.y - this.y) * sinX + (p.z - this.z) * cosX
            var z = (p.y - this.y) * cosX - (p.z - this.z) * sinX

            p.y = y + this.y
            p.z = z + this.z
        }
    }

    fun rotateY(radian: Float) {
        var cosY = cos(radian)
        var sinY = sin(radian)

        for (i in vertex.indices) {
            var p = this.vertex[i]

            var x = (p.x - this.x) * sinY + (p.z - this.z) * cosY
            var z = (p.x - this.x) * cosY - (p.z - this.z) * sinY

            p.x = x + this.x
            p.z = z + this.z
        }
    }
}

var pointer = Point2D(0f, 0f)
var cube = Cube(0f, 0f, 600f, 500f * 0.5f)

class CubeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }

    class DrawView(context: Context?) : View(context) {

        var canvas = Canvas()
        var paint = Paint()

        override fun onDraw(canva: Canvas) {
            canva.drawColor(Color.DKGRAY)
            canvas = canva

            drawingCube(pointer, canvas)
        }

        private fun cubeProjection(vertex3D: Array<Point3D>, height: Float, width: Float): Array<Point2D> {

            var vertex2D = Array(vertex3D.size) { Point2D(x, y) }

            var focalLength = 400

            for (i in vertex3D.indices) {

                var vertex = vertex3D[i]

                var x = vertex.x * (focalLength / vertex.z) + width * 0.5f
                var y = vertex.y * (focalLength / vertex.z) + height * 0.5f

                vertex2D[i] = Point2D(x, y)

            }

            return vertex2D
        }

        private fun drawingCube(pointer:Point2D, canvas:Canvas) {
            var path = Path()

            var height = canvas.height.toFloat()
            var width = canvas.width.toFloat()

            cube.rotateX(pointer.y * 0.00005f)
            cube.rotateY(pointer.x * 0.00005f)

            canvas.save()
            var vertices = cubeProjection(cube.vertex, width, height)

            for (i in cube.faces.indices) {
                var face = cube.faces[i]

                var firstVertex = cube.vertex[face[0]]
                var secondVertex = cube.vertex[face[1]]
                var thirdVertex = cube.vertex[face[2]]

                var firstVertexIn3D = Point3D(secondVertex.x - firstVertex.x, secondVertex.y - firstVertex.y, secondVertex.z - firstVertex.z)
                var secondVertexIn3D = Point3D(thirdVertex.x - firstVertex.x, thirdVertex.y - firstVertex.y, thirdVertex.z - firstVertex.z)

                var multipleVertexIn3D = Point3D(
                    firstVertexIn3D.y * secondVertexIn3D.z - firstVertexIn3D.z * secondVertexIn3D.y,
                    firstVertexIn3D.z * secondVertexIn3D.x - firstVertexIn3D.x * secondVertexIn3D.z,
                    firstVertexIn3D.x * secondVertexIn3D.y - firstVertexIn3D.y * secondVertexIn3D.x
                )

                if (-firstVertex.x * multipleVertexIn3D.x + -firstVertex.y * multipleVertexIn3D.y + -firstVertex.z * multipleVertexIn3D.z <= 0) {
                    val myPath: Array<Point2D> = arrayOf(
                        vertices[face[0]], vertices[face[1]], vertices[face[2]], vertices[face[3]]
                    )
                    path.moveTo(myPath[0].x - 400, myPath[0].y + 200)

                    for (i in myPath.indices) {
                        path.lineTo(myPath[i].x - 400, myPath[i].y + 200)
                    }

                    paint.color = Color.BLACK
                    paint.style = Paint.Style.FILL
                    canvas.drawPath(path, paint)

                    paint.color = 0xfff9ed02.toInt()
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 10f
                    canvas.drawPath(path, paint)

                    paint.color = 0xfff9ed02.toInt()
                    paint.textSize = 200f

                    val randomInt = (1..9).random()

                    canvas.drawTextOnPath("$randomInt ", path, 230f, 340f, paint)

                }
            }
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    pointer.x = event.x - width * 0.5f
                    pointer.y = event.y - height * 0.5f

                    invalidate()
                }
            }

            return true
        }
    }

}



