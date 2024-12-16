package com.example.booksapp3

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityCategoryAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {

    // sebagai view binding
    private lateinit var binding: ActivityCategoryAddBinding

    // firebase authentifikasi
    private lateinit var firebaseAuth: FirebaseAuth

    // progress dialog.
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init //firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Harap Tunggu...")
        progressDialog.setCanceledOnTouchOutside(false)

        //pindah klik, kembali
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // pindah klik, upload kategori
        binding.submitBtn.setOnClickListener{
            validateData()
        }
    }

    private var category = ""

    private fun validateData() {
        // validate Data
        category = binding.categoryEt.text.toString().trim()

        // validate Data
        if (category.isEmpty()) {
            Toast.makeText(this,"Enter Category...", Toast.LENGTH_SHORT).show()
        }
        else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        // tampilkan progress...
        progressDialog.show()

        // ambil timestap db
        val timestamp = System.currentTimeMillis()

        // mempersiapkan data di firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] ="$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        // tambahkan ke firebase db: Database Root > Categories > categoryId > category info
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //berhasil ditambah
                progressDialog.dismiss()
                Toast.makeText(this,"Berhasil Ditambah...", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e ->
                //Gagal Ditambah
                progressDialog.dismiss()
                Toast.makeText(this,"Gagal untuk menambah kategori", Toast.LENGTH_SHORT).show()
            }
    }
}