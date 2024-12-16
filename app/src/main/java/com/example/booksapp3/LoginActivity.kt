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
import com.example.booksapp3.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityLoginBinding

    //firebase autentifikasi
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //inisialisasi firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //inisialisasi progress dialog diatas, ini akan ditampilin waktu login user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // listener not have acc,
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        // listener mulai login
        binding.loginBtn.setOnClickListener {
            /* Pahamin urutan pengerjaan kodenya,,,
            * 1) Input Data
            * 2) Validasi Data
            * 3) Create Account - pake firebase auth
            * 4) Simpan Info user - pake firebase realtime db
            *   Jika itu user maka pindah ke user dashboard
            *   Jika itu admin - maka pindah ke admin dashboard*/
            validateData()
        }

    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        //1 -  Input Data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // 2 - Validasi Data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Emailnya salah woi...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this,"Masukikkin dul passwordnya...", Toast.LENGTH_SHORT).show()
        }
        else {
            loginUser()
        }
    }

    private fun loginUser() {
        //3 - Login Account - pake firebase auth

        // tampil progress
        progressDialog.setMessage("Loggin Skuy...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Login gagal karena ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        // 4 - check tipe user - firebase auth
        // jika user mka pindah ke user dashboard
        // jika admin maka pindah ke admin dashboard

        progressDialog.setMessage("Checking User...")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //ini untuk tau data yang diambil user dan admin
                    val userType = snapshot.child("userType").value
                    if (userType == "user"){
                        // user, menuju ke user dashboard
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                    else if (userType == "admin") {
                        // admin, menuju ke admin dashboard
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}