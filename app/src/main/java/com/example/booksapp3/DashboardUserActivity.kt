package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inisialisasi firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    private fun checkUser() {
        // ambil data user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            //kalau belum login maka user akan tetap brd di dashboard tnpa login
            binding.subTitleTv.text = "Tidak Bisa Masuk"
        }
        else {
            // jika sudah login maka ke user info/db
            val email= firebaseUser.email
            // ambil data dari email lalu di tampilin melalui text di subTitleTv dengang method .text
            binding.subTitleTv.text = email
        }
    }
}