package com.dicoding.asclepius.view

import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultText = intent.getStringExtra("RESULT_TEXT")
        val imageUri = intent.getStringExtra("IMAGE_URI")

        val formattedResultText = formatResultText(resultText)

        binding.resultText.text = formattedResultText
        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }
    }

    private fun formatResultText(resultText: String?): SpannableStringBuilder {
        if (resultText == null) return SpannableStringBuilder("Hasil tidak ditemukan")

        val results = resultText.split("\n").map { it.split(":") }
        val nonCancerScore = results.find { it[0].trim() == "Non Cancer" }?.get(1)?.trim()?.toFloatOrNull() ?: 0f
        val cancerScore = results.find { it[0].trim() == "Cancer" }?.get(1)?.trim()?.toFloatOrNull() ?: 0f

        val prediction = if (nonCancerScore > cancerScore) "Non Cancer" else "Cancer"
        val predictionPercentage = maxOf(nonCancerScore, cancerScore) * 100
        val formattedPrediction = "Hasil Prediksi: %.2f%% %s".format(predictionPercentage, prediction)

        val boldStyle = StyleSpan(Typeface.BOLD)
        val largeTextSize = RelativeSizeSpan(1.5f) // Ukuran teks 1.5x lebih besar dari ukuran standar

        val builder = SpannableStringBuilder(formattedPrediction)
        val boldIndex = formattedPrediction.indexOf(':') + 2 // Memulai indeks setelah ": "
        builder.setSpan(boldStyle, boldIndex, builder.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        builder.setSpan(largeTextSize, 0, builder.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        builder.append("Non Cancer : ")
        builder.append("%.6f".format(nonCancerScore))
        builder.append("Cancer : ")
        builder.append("%.6f".format(cancerScore))

        return builder
    }
}
