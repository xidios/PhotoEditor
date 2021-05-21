package com.example.photoeditor.segmentation.model

import android.app.Activity
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_imgproc.*
import org.bytedeco.javacpp.opencv_objdetect
import java.io.File

object FaceDetection {

    private const val faceModel = "haarcascade_frontalface_default.xml"

    private lateinit var faceCascade: opencv_objdetect.CascadeClassifier

    fun loadModel(activity: Activity) {
        faceCascade = opencv_objdetect.CascadeClassifier(File(activity.filesDir, "das").apply {
            writeBytes(activity.assets.open(faceModel).readBytes())
        }.path)
    }

    fun detectFaces(image: Mat): RectVector {
        val rectangles = RectVector()
        val grayScaled = image.prepare()
        faceCascade.detectMultiScale(grayScaled, rectangles, 1.2, 10, 0, Size(30, 30), null)
        return rectangles
    }

    private fun Mat.toGrayScale(): Mat =
        if (channels() >= 3) Mat().apply { cvtColor(this@toGrayScale, this, COLOR_BGR2GRAY) }
        else this

    private fun Mat.prepare(): Mat {
        val mat = toGrayScale()
        equalizeHist(mat, mat)
        return mat
    }


}