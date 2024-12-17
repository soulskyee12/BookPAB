package com.example.booksapp3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityPdfEditBinding

class PdfEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityPdfEditBinding

    //book id from intent started from AdapterPdfAdmin
    private var bookId =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pdf_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}