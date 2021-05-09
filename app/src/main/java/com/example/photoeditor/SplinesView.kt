package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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
import kotlin.math.pow
import kotlin.math.sqrt

class SplinesView @JvmOverloads constructor(
     context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
 ) : View(context, attrs, defStyleAttr) {

     var paint = Paint()
     var path = Path()

     var arrPoint = arrayListOf<CoordinatePoints>()
     var arrSplines = arrayListOf<CoordinatePoints>()
     var arrHelper = arrayListOf<ArrayList<CoordinatePoints>>()
     var arrSegments = arrayListOf<ArrayList<CoordinatePoints>>()

     class CoordinatePoints(var x: Float, var y: Float)

     override fun onTouchEvent(event: MotionEvent?): Boolean {
         when(event?.action) {
             MotionEvent.ACTION_DOWN-> {
                 arrPoint.add(CoordinatePoints(event.x, event.y))
                 if(arrPoint.size > 1){
                     constructionSegments(arrPoint)
                 }

                 invalidate()
             }
         }

         return true
     }

     private fun constructionSegments(arrPoint: ArrayList<CoordinatePoints>){
         arrSegments.clear()
         for(i in arrPoint.indices){
             if (i == arrPoint.size - 1) break

             arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1]))
         }
     }

     override fun onDraw(canvas: Canvas) {
         super.onDraw(canvas)

         for(i in arrPoint.indices){

             if(i == arrPoint.size - 1){
                 drawingPoints(arrPoint[i].x, arrPoint[i].y, -1f, -1f, canvas)
             }
             else drawingPoints(arrPoint[i].x, arrPoint[i].y, arrPoint[i + 1].x, arrPoint[i + 1].y, canvas)
         }

         if(arrSplines.isNotEmpty()){

             paint.color = Color.BLUE
             paint.style = Paint.Style.STROKE
             paint.strokeWidth = 10f

             canvas.drawPath(path, paint)
         }
     }

     private fun drawingPoints(xStart:Float, yStart: Float, xEnd:Float, yEnd: Float, canvas: Canvas){
         if(xEnd != -1f && yEnd != -1f){
             paint.color = Color.BLACK
             paint.style = Paint.Style.STROKE
             paint.strokeWidth = 7f

             canvas.drawLine(xStart, yStart, xEnd, yEnd, paint)
         }

         paint.color = Color.YELLOW
         paint.style = Paint.Style.FILL
         canvas.drawCircle(xStart, yStart, 20f, paint)

         paint.color = Color.BLACK
         paint.style = Paint.Style.STROKE
         paint.strokeWidth = 3f
         canvas.drawCircle(xStart, yStart, 20f, paint)

         canvas.save()
     }

     private fun calculatingDistance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
         return kotlin.math.sqrt(
             (x2.toDouble() - x1.toDouble()).pow(2.0) + (y2.toDouble() - y1.toDouble()).pow(2.0)
         )
     }

     private fun calculatingEndHelperPoints(x1: Float, y1: Float, x2: Float, y2: Float, r:Float): CoordinatePoints {
         var m = (y2 - y1)/(x2 - x1)
         var n = -x1*m + y1

         var a = 1 + (m * m)
         var b = -x1 * 2 + (m * (n - y1)) * 2
         var c = (x1 * x1) + (n - y1)*(n - y1) - (r * r)

         var x =  ((-b + sqrt((b * b) - 4 * a * c)) / (2 * a))
         var y = x * m + n

         return CoordinatePoints(x, y)
     }

     fun convertingToSpline(){
         var t = 0.0

         var distanceFirst: Double
         var distanceSecond: Double
         var distanceHelper: Double

         arrSplines.add(arrSplines.size, arrPoint[0])

         for(i in arrSegments.indices) {
             if(i == arrSegments.size - 2) break
             while (t <= 1.0) {
                 t += 0.05

                 distanceFirst = calculatingDistance(arrSegments[i][0].x, arrSegments[i][0].y,arrSegments[i][1].x, arrSegments[i][1].y,)
                 distanceSecond = calculatingDistance(arrSegments[i + 1][0].x, arrSegments[i + 1][0].y,arrSegments[i + 1][1].x, arrSegments[i + 1][1].y)

                 arrHelper.add(
                     arrayListOf(
                         calculatingEndHelperPoints(arrSegments[i][0].x, arrSegments[i][0].y,arrSegments[i][1].x, arrSegments[i][1].y, (t * distanceFirst).toFloat()),
                         calculatingEndHelperPoints(arrSegments[i + 1][0].x, arrSegments[i + 1][0].y,arrSegments[i + 1][1].x, arrSegments[i + 1][1].y, (t * distanceSecond).toFloat())
                     )
                 )
             }
         }

         t = 0.0

         for (i in arrHelper.indices) {
             if(i == arrHelper.size - 2) break
             t += 0.05
             distanceHelper = t * calculatingDistance(
                 arrHelper[i][0].x,
                 arrHelper[i][0].y,
                 arrHelper[i][1].x,
                 arrHelper[i][1].y
             )

             arrSplines.add(arrSplines.size, calculatingEndHelperPoints(arrHelper[i][0].x, arrHelper[i][0].y, arrHelper[i][1].x, arrHelper[i][1].y, (t * distanceHelper).toFloat()))
         }

         arrSplines.add(arrSplines.size, arrPoint[arrPoint.size - 1])
         path.moveTo(arrSplines[0].x, arrSplines[0].y)

         for(i in arrSplines.indices){
             path.lineTo(arrSplines[i].x, arrSplines[i].y)
         }

         Toast.makeText(context, "kek", Toast.LENGTH_SHORT).show()

         invalidate()
     }
}

