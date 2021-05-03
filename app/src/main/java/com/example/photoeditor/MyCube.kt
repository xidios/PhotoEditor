package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.math.cos
import kotlin.math.sin


//class VertexCube(var x: Float, var y: Float, var z: Float)
//
//var paint = Paint()
//var vertex0 = VertexCube(300f, 300f, 300f)
//var vertex1 = VertexCube(300f, 300f, 600f)
//var vertex2 = VertexCube(300f, 600f, 300f)
//var vertex3 = VertexCube(300f, 600f, 600f)
//var vertex4 = VertexCube(600f, 300f, 300f)
//var vertex5 = VertexCube(600f, 300f, 600f)
//var vertex6 = VertexCube(600f, 600f, 300f)
//var vertex7 = VertexCube(600f, 600f, 600f)
//
//var vertexesCube = arrayOf(
//    vertex0, vertex1,
//    vertex2, vertex3,
//    vertex4, vertex5,
//    vertex6, vertex7
//)
//
//var edgeCube0 = arrayOf(0, 1)
//var edgeCube1 = arrayOf(1, 3)
//var edgeCube2 = arrayOf(3, 2)
//var edgeCube3 = arrayOf(2, 0)
//var edgeCube4 = arrayOf(4, 5)
//var edgeCube5 = arrayOf(5, 7)
//var edgeCube6 = arrayOf(7, 6)
//var edgeCube7 = arrayOf(6, 4)
//var edgeCube8 = arrayOf(0, 4)
//var edgeCube9 = arrayOf(1, 5)
//var edgeCube10 = arrayOf(2, 6)
//var edgeCube11 = arrayOf(3, 7)
//
//var edgesCube = arrayOf(
//    edgeCube0, edgeCube1, edgeCube2,
//    edgeCube3, edgeCube4, edgeCube5,
//    edgeCube6, edgeCube7, edgeCube8,
//    edgeCube9, edgeCube10, edgeCube11
//)
//
//val path0 = Path()
//val path1 = Path()
//val path2 = Path()
//
//
//class MyCube @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    var posX: Float = vertex0.x
//    var posY: Float = vertex0.y
//
//    lateinit var canvas: Canvas
//
////    override fun onTouchEvent(event: MotionEvent?): Boolean {
////        when (event?.action) {
////            MotionEvent.ACTION_DOWN -> {
////                rotateCubeX((event.x - posX) * 0.5f)
////                rotateCubeY((event.y - posY) * 0.5f)
////
////                posX = event.x
////                posY = event.y
////
////                draw(canvas)
////                Toast.makeText(context, "Я сработал, я красава", Toast.LENGTH_SHORT).show()
////            }
////
////            MotionEvent.ACTION_MOVE ->{
////                rotateCubeX((event.x - posX) * 0.5f)
////                rotateCubeY((event.y - posY) * 0.5f)
////
////                posX = event.x
////                posY = event.y
////
////                draw(canvas)
////                Toast.makeText(context, "Я сработал, я красава", Toast.LENGTH_SHORT).show()
////            }
////        }
////
////        return true
////    }
//
//    fun Ondraw(canva: Canvas) {
//        this.canvas = canva
//
//        for (i in edgesCube.indices) {
//            var n0 = edgesCube[i][0]
//            var n1 = edgesCube[i][1]
//
//            var node0 = vertexesCube[n0]
//            var node1 = vertexesCube[n1]
//
//            paint.color = 0xff000000.toInt()
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = 7f
//
//            this.canvas.drawLine(node0.x, node0.y, node1.x, node1.y, paint)
//
//
//            if(i == 3 || i == 8 || i == 10 || i == 7) {
//                paint.color = 0xff000000.toInt()
//                paint.style = Paint.Style.STROKE
//               paint.strokeWidth = 7f
//
//                canvas.drawLine(node0.x, node0.y, node1.x, node1.y, paint)
//
//                if (i == 3) {
//                    path0.moveTo(node0.x, node0.y)
//
//                }
//                if(i != 3){
//                    path0.lineTo(node0.x, node0.y)
//
//                    paint.color = 0xff400000.toInt()
//                    paint.style = Paint.Style.FILL
//                    canvas.drawPath(path0, paint)
//                }
//            }
//
//            if(i in 4..7) {
//                path1 == null
//
//                paint.color = 0xff000000.toInt()
//                paint.style = Paint.Style.STROKE
//                paint.strokeWidth = 7f
//
//                canvas.drawLine(node0.x, node0.y, node1.x, node1.y, paint)
//
//                if (i == 4) {
//                    path1.moveTo(node0.x, node0.y)
//                }
//
//               if(i != 4) {
//                   path1.lineTo(node0.x, node0.y)
//
//                   paint.color = 0xff5300ab.toInt()
//                   paint.style = Paint.Style.FILL
//                   canvas.drawPath(path1, paint)
//               }
//            }
//        }
//
//        for (i in vertexesCube.indices) {
//            var node = vertexesCube[i]
//
//            paint.color = 0xff000000.toInt()
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = 10f
//
//            this.canvas.drawCircle(node.x, node.y, 10f, paint)
//
//            paint.color = 0xffffef31.toInt()
//            paint.style = Paint.Style.FILL
//
//            this.canvas.drawCircle(node.x, node.y, 10f, paint)
//        }
//
//        this.canvas.save()
//
//
//    }
//
//    private fun rotateCubeX(rotateX: Float) {
//        var sinX = sin(rotateX)
//        var cosX = cos(rotateX)
//
//        for (i in vertexesCube.indices) {
//            var node = vertexesCube[i]
//
//            var y = node.y
//            var z = node.z
//
//            node.y = y * cosX - z * sinX
//            node.z = z * cosX + y * sinX
//        }
//
//        canvas.drawColor(0xff000000.toInt())
//        canvas.save()
//    }
//
//    private fun rotateCubeY(rotateY: Float) {
//        var sinY = sin(rotateY)
//        var cosY = cos(rotateY)
//
//        for (i in vertexesCube.indices) {
//            var node = vertexesCube[i]
//
//            var x = node.x
//            var z = node.z
//
//            node.x = x * cosY - z * sinY
//            node.z = z * cosY + x * sinY
//        }
//
//        canvas.drawColor(0xff000000.toInt())
//        canvas.save()
//    }
//
//    fun rotateCubeZ(rotateZ: Float) {
//        var sinZ = sin(rotateZ)
//        var cosZ = cos(rotateZ)
//
//        for (i in vertexesCube.indices) {
//            var node = vertexesCube[i]
//
//            var x = node.x
//            var y = node.y
//
//            node.x = x * cosZ - y * sinZ
//            node.y = y * cosZ + x * sinZ
//        }
//
//        canvas.save()
//    }
//}











