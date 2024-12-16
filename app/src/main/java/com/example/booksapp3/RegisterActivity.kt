package com.example.booksapp3

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    //pakek view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase autentifikasi
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inisialisasi firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //inisialisasi progress dialog diatas, ini akan ditampilin waktu membuat akun atau daftarin user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //tombol back biar bisa kembali ke page sbelumnya
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //operasi klik utk memulai register
        binding.registerBtn.setOnClickListener {
            /* Pahamin urutan pengerjaan kodenya,,,
            * 1) Input Data
            * 2) Validasi Data
            * 3) Create Account - pake firebase auth
            * 4) Simpan Info user - pake firebase realtime db*/
            validateData()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""


    private fun validateData() {
        // 1 - Input Data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        // 2 - Validasi Data
        if (name.isEmpty()) {
            Toast.makeText(this, "Masukkan Nama anda...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // kalau misal salah input email tidak sesuai dengang yang seharusnya
            Toast.makeText(this, "Emailnya salah woi...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            // password blank
            Toast.makeText(this, "Masukkin dul passwordnya...", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
            // password blank
            Toast.makeText(this, "Masukkin dul passwordnya...", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(this, "Password gak sama diliat lagi...", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        // 3 - Buat AKun - autentifikasi firebase

        // show progress
        progressDialog.setMessage("Membuat Akun...")
        progressDialog.show()

        //buat akun di auth firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //akun  telah dibuat, lalu mengupdate user di database
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal membuat akun karena ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    private fun updateUserInfo() {
        // 4 - Menyimpan info user - Pakek Realtime Database
        progressDialog.setMessage("Menyimpan informasi user...")

        // timestap
        val timeStamp = System.currentTimeMillis()

        // ambil id user, karena user telah terdaftar maka kita bisa get datanya skrng
        val uid = firebaseAuth.uid

        // mengatur data untuk ditambah di databes
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" // nanti ditambahin, skrng dikosongin
        hashMap["userType"] = "user" // tipe user nanti bisa digonta ganti
        hashMap["timestamp"] = timeStamp

        // atur data di db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Akun telah dibuat", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal membuat akun karena ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}