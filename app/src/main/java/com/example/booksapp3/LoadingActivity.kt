package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoadingActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
            checkUser()
        }, 1000)
        // delayMillis untuk berapa lama delay masuk aplikasi
    }

    private fun checkUser() {
        // mendapatkan informasi apkah user sudah login atau tidak
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // user tidak login, ke main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        //ini untuk tau data yang diambil user dan admin
                        val userType = snapshot.child("userType").value
                        if (userType == "user") {
                            // user, menuju ke user dashboard
                            startActivity(
                                Intent(
                                    this@LoadingActivity,
                                    DashboardUserActivity::class.java
                                )
                            )
                            finish()
                        } else if (userType == "admin") {
                            // admin, menuju ke admin dashboard
                            startActivity(
                                Intent(
                                    this@LoadingActivity,
                                    DashboardAdminActivity::class.java
                                )
                            )
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }


}
