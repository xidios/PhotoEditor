package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.pow

class SplinesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val codeDrawing = 666
     var deletePoints = 0

     var paint = Paint()
     var path = Path()

     var arrPoint = arrayListOf<CoordinatePoints>()
     var arrSplines = arrayListOf<CoordinatePoints>()
     var arrHelper = arrayListOf<CoordinatePoints>()
     var arrSegments = arrayListOf<ArrayList<CoordinatePoints>>()

     class CoordinatePoints(var x: Float, var y: Float)

     override fun onDraw(canvas: Canvas) {
         super.onDraw(canvas)
         fillingInCanvasOfPoints(0, canvas)

         if(arrSplines.isNotEmpty()){
             canvas.drawColor(Color.WHITE)

             paint.color = Color.BLUE
             paint.style = Paint.Style.STROKE
             paint.strokeWidth = 10f

             canvas.drawPath(path, paint)

             fillingInCanvasOfPoints(codeDrawing,canvas)

             canvas.save()
         }
     }

    private fun constructionSegments(){
        path.reset()
        arrSplines.clear()
        arrSegments.clear()
        arrHelper.clear()

        var i = 0
        var help = arrPoint.size

        while( help != 0){
            when {
                arrPoint.size > 3 -> {
                    help -= 4
                    when {
                        help >= 0 -> {
                            if(i == 0) arrHelper.add(arrPoint[i])
                            arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1], arrPoint[i + 2], arrPoint[i + 3]))
                            arrHelper.add(arrPoint[i + 3])
                            help++
                            i += 3
                        }
                        help == -1 -> {
                            arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1], arrPoint[i + 2]))
                            arrHelper.add(arrPoint[i + 2])
                            break
                        }
                        help == -2 -> {
                            arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1]))
                            arrHelper.add(arrPoint[i + 1])
                            break
                        }
                        help == -3 -> {
                            arrSegments.add(arrayListOf(arrPoint[i]))
                            arrHelper.add(arrPoint[i])
                            break
                        }
                    }
                }
                arrPoint.size == 3 -> {
                    arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1], arrPoint[i + 2]))
                    arrHelper.add(arrPoint[i])
                    arrHelper.add(arrPoint[i + 2])
                    break
                }
                arrPoint.size == 2 -> {
                    arrSegments.add(arrayListOf(arrPoint[i], arrPoint[i + 1]))
                    arrHelper.add(arrPoint[i])
                    arrHelper.add(arrPoint[i + 1])
                    break
                }
            }
        }
    }

    private fun fillingInCanvasOfPoints(code:Int, canvas:Canvas){
        for(i in arrPoint.indices){
            if(i == arrPoint.size - 1){
                drawingPoints(arrPoint[i].x, arrPoint[i].y, -1f, -1f, code,canvas)
            }
            else drawingPoints(arrPoint[i].x, arrPoint[i].y, arrPoint[i + 1].x, arrPoint[i + 1].y, code, canvas)
        }

    }

    private fun drawingPoints(xStart:Float, yStart: Float, xEnd:Float, yEnd: Float,code:Int ,canvas: Canvas){
        if(xEnd != -1f && yEnd != -1f && code != codeDrawing){
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 7f

            canvas.drawLine(xStart, yStart, xEnd, yEnd, paint)
        }

        if(code != codeDrawing){
            paint.color = Color.YELLOW
            paint.style = Paint.Style.FILL
            canvas.drawCircle(xStart, yStart, 30f, paint)

            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawCircle(xStart, yStart, 30f, paint)

        }
        else{
            for(i in arrHelper.indices){
                paint.color = Color.RED
                paint.alpha = 100
                paint.style = Paint.Style.FILL
                canvas.drawCircle(arrHelper[i].x, arrHelper[i].y, 30f, paint)

                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                canvas.drawCircle(arrHelper[i].x, arrHelper[i].y, 30f, paint)
            }
        }


        canvas.save()
    }

    fun convertingToSpline(){
         constructionSegments()

         var xS: Double
         var yS: Double

        arrSplines.add(arrSplines.size, arrPoint[0])

         for(i in arrSegments.indices) {
             var t = 0.0

             when (arrSegments[i].size) {
                 4 -> {
                     while (t <= 1.0) {
                         t += 0.05

                         xS =
                             (1.0 - t).pow(3.0) * arrSegments[i][0].x + 3.0 * t * (1.0 - t).pow(2.0) * arrSegments[i][1].x +
                                     3.0 * t.pow(2.0) * (1.0 - t) * arrSegments[i][2].x + t.pow(3.0) * arrSegments[i][3].x

                         yS =
                             (1.0 - t).pow(3.0) * arrSegments[i][0].y + 3.0 * t * (1.0 - t).pow(2.0) * arrSegments[i][1].y +
                                     3.0 * t.pow(2.0) * (1.0 - t) * arrSegments[i][2].y + t.pow(3.0) * arrSegments[i][3].y


                         arrSplines.add(
                             arrSplines.size, SplinesView.CoordinatePoints(xS.toFloat(), yS.toFloat())
                         )
                     }
                 }
                 3 -> {
                     if(i != 0){
                         arrSegments[i].add(arrSegments[i].size,
                             SplinesView.CoordinatePoints(
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].x + arrSegments[i][arrSegments[i].size - 1].x) / 2,
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].y + arrSegments[i][arrSegments[i].size - 1].y) / 2
                             )
                         )
                     }
                     while (t <= 1.0) {
                         t += 0.05

                         xS =(1.0 - t).pow(2.0) * arrSegments[i][0].x + 2.0 * t * (1.0 - t) * arrSegments[i][1].x + t.pow(2.0)  * arrSegments[i][2].x

                         yS =(1.0 - t).pow(2.0) * arrSegments[i][0].y + 2.0 * t * (1.0 - t) * arrSegments[i][1].y + t.pow(2.0)  * arrSegments[i][2].y

                         arrSplines.add(arrSplines.size,
                             SplinesView.CoordinatePoints(xS.toFloat(), yS.toFloat())
                         )
                     }
                 }
                 2 -> {
                     if(i != 0){
                         arrSegments[i].add(arrSegments[i].size,
                             SplinesView.CoordinatePoints(
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].x + arrSegments[i][arrSegments[i].size - 1].x) / 2,
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].y + arrSegments[i][arrSegments[i].size - 1].y) / 2
                             )
                         )
                     }
                     while (t <= 1.0) {
                         t += 0.05

                         xS =(1.0 - t) * arrSegments[i][0].x + t * arrSegments[i][1].x

                         yS =(1.0 - t) * arrSegments[i][0].y + t * arrSegments[i][1].y

                         arrSplines.add(arrSplines.size,
                             SplinesView.CoordinatePoints(xS.toFloat(), yS.toFloat())
                         )
                     }
                 }
                 1 -> {
                     if(i != 0){
                         arrSegments[i].add(arrSegments[i].size,
                             SplinesView.CoordinatePoints(
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].x + arrSegments[i][arrSegments[i].size - 1].x) / 2,
                                 (arrSegments[i - 1][arrSegments[i - 1].size - 1].y + arrSegments[i][arrSegments[i].size - 1].y) / 2
                             )
                         )
                     }
                     arrSplines.add(arrSplines.size,
                         SplinesView.CoordinatePoints(arrSegments[i][0].x, arrSegments[i][0].y)
                     )
                 }
             }
         }

        arrSplines.add(arrSplines.size, arrPoint[arrPoint.size - 1])

        path.moveTo(arrSplines[0].x, arrSplines[0].y)

        for(i in arrSplines.indices){
             path.lineTo(arrSplines[i].x, arrSplines[i].y)
        }

         invalidate()
     }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(arrSplines.isEmpty()){
                    arrPoint.add(CoordinatePoints(event.x, event.y))

                    invalidate()
                }

                if(deletePoints == 1){
                    for(i in arrPoint.indices){
                        if((event.x < arrPoint[i].x + 60f && event.x >= arrPoint[i].x && event.y < arrPoint[i].y + 60f && event.y >= arrPoint[i].y) ||
                            (event.x >= arrPoint[i].x - 60f && event.x < arrPoint[i].x && event.y >= arrPoint[i].y - 60f && event.y < arrPoint[i].y)){

                                Toast.makeText(context, "del", Toast.LENGTH_SHORT).show()
                            arrPoint.removeAt(i)

                            deletePoints = 0
                            convertingToSpline()
                            invalidate()

                            break
                        }
                    }
                }
            }

            MotionEvent.ACTION_MOVE ->{
                if(arrSplines.isNotEmpty()){
                    for(i in arrPoint.indices){
                        if((event.x < arrPoint[i].x + 60f && event.x >= arrPoint[i].x  && event.y < arrPoint[i].y + 60f && event.y >= arrPoint[i].y) ||
                            (event.x >= arrPoint[i].x - 60f && event.x < arrPoint[i].x && event.y >= arrPoint[i].y - 60f && event.y < arrPoint[i].y)){

                            arrPoint[i].x = event.x
                            arrPoint[i].y = event.y

                            convertingToSpline()

                            invalidate()

                            break
                        }
                    }
                }
            }
        }

        return true
    }
}

