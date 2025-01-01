package com.example.booksapp3

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityArticleDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticleDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityArticleDetailBinding


    private companion object {
        const val TAG = "ARTICLE_DETAIL_TAG"
    }

    // book id, ambil dari intent
    private var articleId = ""

    // ambil dari firebase
    private var articleTitle = ""
    private var articleUrl = ""

//    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = resources.getColor(R.color.item_utama)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil id book dari intent, bookId akan digunakan untuk load book info
        articleId = intent.getStringExtra("articleId")!!

        //init prgresssbar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("tunggu sebentar ....")
        progressDialog.setCanceledOnTouchOutside(false)

        loadArticleDetails()

        // click listener backBtn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadArticleDetails() {
        // Books > bookId > Details
        val ref = FirebaseDatabase.getInstance().getReference("Articles")
        ref.child(articleId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val article = "${snapshot.child("article").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val articleTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"

                    // format date
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())


                    // Set data ke view
                    binding.articleTitleTv.text = articleTitle
                    binding.articleTv.text = article
                    binding.dateTv.text = date


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}