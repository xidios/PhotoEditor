package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_cube.view.*
import kotlin.math.cos
import kotlin.math.sin


class CubeActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }

    class DrawView(context: Context?) : View(context) {
        lateinit var canvas: Canvas
        var paint = Paint()
        var path: Path = Path()

        override fun onDraw(canva: Canvas) {
            canvas = canva
            canvas.drawColor(Color.BLUE)

            loop()
        }


        class Point2D(var x: Float, var y: Float)

        class Point3D(x: Float, y: Float, z: Float) {
            var x: Float = 0f
            var y: Float = 0f
            var z: Float = 0f
        }

        class Cube(var x: Float, var y: Float, var z: Float, var size: Float) {
            var vertex = arrayOf(
                Point3D(x - size, y - size, z - size),
                Point3D(x + size, y - size, z - size),
                Point3D(x + size, y + size, z - size),
                Point3D(x - size, y + size, z - size),
                Point3D(x - size, y - size, z + size),
                Point3D(x + size, y - size, z + size),
                Point3D(x + size, y + size, z + size),
                Point3D(x - size, y + size, z + size)
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

                    var x = (p.y - this.y) * sinX + (p.z - this.z) * cosX
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
        var cube = Cube(0f, 0f, 400f, 200f)

        var height = 300f
        var width = 300f


        private fun project(points3d: Array<Point3D>, height: Float, width: Float): Array<Point2D> {

            var points2d = Array(points3d.size, { Point2D(x, y) })

            var focalLength = 200

            for (i in points3d.indices) {

                var p = points3d[i]

                var x = p.x * (focalLength / p.z) + width * 0.5f
                var y = p.y * (focalLength / p.z) + height * 0.5f

                points2d[i] = Point2D(x, y)

            }

            return points2d
        }

        fun loop() {
            paint.color = 0xffffffff.toInt()
            paint.style = Paint.Style.FILL
            this.canvas.drawRect(0f, 0f, 200f, 200f, paint)

            paint.color = 0xffffffff.toInt()
            paint.style = Paint.Style.STROKE

            cube.rotateX(pointer.y * 0.0001f)
            cube.rotateY(-pointer.x * 0.0001f)


            var vertices = project(cube.vertex, width, height)

            for (i in cube.faces.indices) {

                var face = cube.faces[i]

                var p1 = cube.vertex[face[0]]
                var p2 = cube.vertex[face[1]]
                var p3 = cube.vertex[face[2]]

                var v1 = Point3D(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z)
                var v2 = Point3D(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z)

                var n = Point3D(
                    v1.y * v2.z - v1.z * v2.y,
                    v1.z * v2.x - v1.x * v2.z,
                    v1.x * v2.y - v1.y * v2.x
                )

                if (-p1.x * n.x + -p1.y * n.y + -p1.z * n.z <= 0) {

                    paint.color = 0xff000000.toInt()
                    paint.style = Paint.Style.STROKE

                    path.moveTo(vertices[face[0]].x, vertices[face[0]].y)
                    path.lineTo(vertices[face[1]].x, vertices[face[1]].y)
                    path.lineTo(vertices[face[2]].x, vertices[face[2]].y)
                    path.lineTo(vertices[face[3]].x, vertices[face[3]].y)

                    canvas.drawPath(path, paint)

                }
            }
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    Toast.makeText(context, "Я сработал, я красава", Toast.LENGTH_SHORT).show()
                }

                MotionEvent.ACTION_MOVE -> {
                    pointer.x = event.x - width * 0.5f;
                    pointer.y = event.y - height * 0.5f;
                    Toast.makeText(context, "Я сработал, я красава", Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
    }
}



