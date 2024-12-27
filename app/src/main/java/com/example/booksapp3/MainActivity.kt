package com.example.booksapp3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ------------------------
        // 1) Mewarnai kata "Terlengkap" menjadi kuning
        // ------------------------
        val fullText = "Perpustakaan Original ebook Terlengkap"
        val highlightWord = "Terlengkap"

        // Temukan posisi kata "Terlengkap" di fullText
        val startIndex = fullText.indexOf(highlightWord)
        val endIndex = startIndex + highlightWord.length

        // Buat objek SpannableStringBuilder
        val spannable = SpannableStringBuilder(fullText)
        // Set warna kuning pada kata "Terlengkap"
        spannable.setSpan(
            ForegroundColorSpan(Color.YELLOW),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set warna putih untuk textView secara default
        binding.titleTv.setTextColor(Color.WHITE)
        // Set text Spannable ke textView
        binding.titleTv.text = spannable
        // ------------------------

        // handle click, Login
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // menghandle klik, continuewithoutlogin
        binding.continueBtn.setOnClickListener {
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }
    }
}
