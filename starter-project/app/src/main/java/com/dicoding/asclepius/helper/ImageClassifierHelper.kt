package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage
import java.io.IOException

class ImageClassifierHelper(private val context: Context) {
    private lateinit var model: CancerClassification

    private fun setupImageClassifier() {
        model = CancerClassification.newInstance(context)
    }

    fun classifyStaticImage(imageUri: Uri): List<String> {
        if (!::model.isInitialized) {
            setupImageClassifier()
        }

        val bitmap = getBitmapFromUri(imageUri, context)
        return classifyBitmap(bitmap)
    }

    private fun classifyBitmap(bitmap: Bitmap): List<String> {
        try {
            val image = TensorImage.fromBitmap(bitmap)

            val outputs = model.process(image)
            val probability = outputs.probabilityAsCategoryList

            return probability.map { "${it.label}: ${it.score}" }
        } catch (e: IOException) {
            e.printStackTrace()
            return listOf("error")
        }
    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IOException("failed")
    }
}
