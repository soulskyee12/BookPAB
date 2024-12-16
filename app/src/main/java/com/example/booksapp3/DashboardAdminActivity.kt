package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityDashboardAdminBinding

import com.google.firebase.auth.FirebaseAuth


class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inisialisasi firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        //  inisialisasi, start add category page
        binding.addCategoryBtn.setOnClickListener{
            startActivity(Intent(this, CategoryAddActivity::class.java))
        }

        binding.addPdfFab.setOnClickListener{
            startActivity(Intent(this, PdfAddActivity::class.java))
        }
    }

    private fun checkUser() {
        // ambil data user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            //kalau belum login maka ke main screen
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else {
            // jika sudah login maka ke user info/db
            val email= firebaseUser.email
            // ambil data dari email lalu di tampilin melalui text di subTitleTv dengang method .text
            binding.subTitleTv.text = email
        }
    }
}