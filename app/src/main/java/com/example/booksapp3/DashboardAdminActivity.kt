package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityDashboardAdminBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    // arraylist sebagai tempat kategori berada
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    // adapternya
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.item_utama)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //inisialisasi firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()

        // search
        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               // dipanggil ketika user mengetik
                try{
                    adapterCategory.filter.filter(s)
                }
                catch (e: Exception) {

                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })


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
        binding.addArticleFab.setOnClickListener{
            startActivity(Intent(this, ArticleAddActivity::class.java))
        }

        binding.profileBtn.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.ArticleView.setOnClickListener{
            startActivity(Intent(this, ArticlesActivity::class.java))
        }
    }

    private fun loadCategories() {
        // init arrayList
        categoryArrayList = ArrayList()

        // ambil semua "kategori" dari database di dir Firebase DB > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //membersihkan list sebelum menambahkan data
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    // ambil data sebagai model
                    val model = ds.getValue(ModelCategory::class.java)

                    // tambah ke arrayList
                    categoryArrayList.add(model!!)
                }
                // setup adapter
                adapterCategory = AdapterCategory(this@DashboardAdminActivity, categoryArrayList)
                // set adapter to recyclerview
                binding.categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkUser() {
        // Ambil user saat ini
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // Jika belum login, kembali ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Ambil UID user saat ini
            val uid = firebaseUser.uid

            // Referensi ke database untuk mengambil nama user
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Cek jika data ada
                        if (snapshot.exists()) {
                            // Ambil nama dan email dari snapshot
                            val name = snapshot.child("name").value.toString()
                            val email = snapshot.child("email").value.toString()

                            // Set nama dan email ke TextView
                            binding.toolbartitleTv.text = "Welcome, $name"
                            binding.subTitleTv.text = email
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error jika ada masalah
                        android.util.Log.e("Firebase", "Failed to fetch user data", error.toException())
                    }
                })
        }
    }

}