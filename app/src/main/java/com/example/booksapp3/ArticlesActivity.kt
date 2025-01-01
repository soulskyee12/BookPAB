package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityArticlesBinding
import com.example.booksapp3.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticlesActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityArticlesBinding

    private companion object {
        const val TAG = "ARTICLE_LIST"
    }

    // ArrayList as a container for books, initialized immediately
    private var articlesArrayList: ArrayList<ModelArticle> = ArrayList()

    // Adapter
    private lateinit var adapterArticles: AdapterArticles

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = resources.getColor(R.color.item_utama)
        super.onCreate(savedInstanceState)
        binding = ActivityArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup adapter with empty list
        adapterArticles = AdapterArticles(this, articlesArrayList)
        binding.articlesRv.adapter = adapterArticles

        // Load PDFs/books
        loadPdfList()

        // Search functionality
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapterArticles.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // listener back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadPdfList() {
        // Reference Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Articles")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                articlesArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelArticle::class.java)
                    if (model != null) {
                        articlesArrayList.add(model)
                    }
                }
                adapterArticles.notifyDataSetChanged()  // Notify adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }
}