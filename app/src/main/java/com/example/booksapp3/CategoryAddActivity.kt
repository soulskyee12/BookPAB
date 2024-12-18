package com.example.booksapp3

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityCategoryAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityCategoryAddBinding

    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    // Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Harap Tunggu...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Tombol kembali
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // Tombol submit
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        // Ambil data kategori
        category = binding.categoryEt.text.toString().trim()

        // Validasi data
        if (category.isEmpty()) {
            Toast.makeText(this, "Masukkan kategori...", Toast.LENGTH_SHORT).show()
        } else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        // Tampilkan progress dialog
        progressDialog.show()

        // Ambil timestamp
        val timestamp = System.currentTimeMillis()

        // Persiapkan data untuk disimpan di Firebase
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "" // Akan diisi setelah kita dapat key unik dari push()
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        // Rujukan ke Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Categories")

        // Gunakan push() untuk membuat ID unik setiap kali menambah data
        val id = ref.push().key
        hashMap["id"] = id!!

        // Simpan data ke Firebase di node dengan key unik
        ref.child(id)
            .setValue(hashMap)
            .addOnSuccessListener {
                // Berhasil tambah
                progressDialog.dismiss()
                Toast.makeText(this, "Kategori berhasil ditambahkan...", Toast.LENGTH_SHORT).show()
                // Kosongkan field setelah berhasil menambah
                binding.categoryEt.text.clear()
            }
            .addOnFailureListener { e ->
                // Gagal tambah
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal menambah kategori: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
