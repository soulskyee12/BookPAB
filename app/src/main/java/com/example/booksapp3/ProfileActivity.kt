package com.example.booksapp3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.booksapp3.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityProfileBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //arraylist buku
    private lateinit var booksArrayList: ArrayList<ModelPdf>
    private lateinit var  adapterPdfFavorite: AdapterPdfFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.item_utama)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        loadFavoriteBooks()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

        binding.keAI.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }



    }

    private fun loadUserInfo() {
        //ambil refernce dari db buat user
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    //ambil datanya
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    //format tanggal
                    var formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    //masukin datanya
                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = userType

                    //masukin gambar profile pake glide
                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_orange)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    private fun loadFavoriteBooks(){
        //init arraylist
        booksArrayList = ArrayList();

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear arraylist
                    booksArrayList.clear()
                    for (ds in snapshot.children){
                        //get id
                        val bookId = "${ds.child("bookId").value}"

                        //set model
                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        //add model
                        booksArrayList.add((modelPdf))
                    }
                    //number fav
                    binding.favoriteBookCountTv.text = "${booksArrayList.size}"

                    adapterPdfFavorite = AdapterPdfFavorite(this@ProfileActivity, booksArrayList)
                    binding.favoriteRv.adapter = adapterPdfFavorite
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}