package com.example.booksapp3

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityPdfDetailBinding
import com.example.booksapp3.databinding.DialogCommentAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityPdfDetailBinding

    private companion object {
        const val TAG = "BOOKS_DETAIL_TAG"
    }

    // book id, ambil dari intent
    private var bookId = ""

    // ambil dari firebase
    private var bookTitle = ""
    private var bookUrl = ""


    // boolean untuk indicator
    private var isInMyFavorite = false

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var commentArrayList: ArrayList<ModelComment>
    private lateinit var adapterComment: AdapterComment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.item_utama)
        binding = ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil id book dari intent, bookId akan digunakan untuk load book info
        bookId = intent.getStringExtra("bookId")!!

        //init prgresssbar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("tunggu sebentar ....")
        progressDialog.setCanceledOnTouchOutside(false)

        // init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            // user logged in lalu check apa bukunya favorite atau tidak
            checkIsFavorite()
        }

        // increment nilai book views, ketika page mulai
        MyApplication.incrementBookViewCount(bookId)

        loadBookDetails()
        showComments()

        // click listener backBtn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // listerner klik, open pdf view
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }

        // listener favorite button
        binding.favoriteBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                // user gak login, gak bisa klik favorite
                Toast.makeText(this, "Kamu tidak login loh...", Toast.LENGTH_SHORT).show()
            } else {
                // user login, bisa klik favorite
                if (isInMyFavorite) {
                    MyApplication.hapusDariFavorite(this, bookId)
                } else {
                    // gak di fav. maka tambah
                    addToFavorite()
                }
            }
        }

        //binding
        binding.addCommentBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "anda belom login", Toast.LENGTH_SHORT).show()
            } else {
                addCommentDialog()
            }
        }

    }

    private fun showComments() {
        //init arraylist
        commentArrayList = ArrayList()

        //load comment
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentArrayList.clear()
                    for(ds in snapshot.children){
                        val model = ds.getValue(ModelComment::class.java)

                        //add to list
                        commentArrayList.add(model!!)
                    }
                    //adapter
                    adapterComment = AdapterComment(this@PdfDetailActivity, commentArrayList)
                    binding.commentRv.adapter = adapterComment
                }



                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var comment = ""

    private fun addCommentDialog() {

        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))

        //alert dialog
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentAddBinding.root)

        //show and create alert
        val alertDialog = builder.create()
        alertDialog.show()

        //dismis dialog
        commentAddBinding.backBtn.setOnClickListener { alertDialog.dismiss() }

        //handle add comment
        commentAddBinding.submitBtn.setOnClickListener {
            //get data
            comment = commentAddBinding.commentEt.text.toString().trim()

            if (comment.isEmpty()) {
                Toast.makeText(this, "masukkan komen...", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                addComment()
            }


        }

    }

    private fun addComment() {
        //show progressbar
        progressDialog.setMessage("Menambah Komentar")
        progressDialog.show()

        //timestamp
        val timestamp = "${System.currentTimeMillis()}"

        //setup data
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["bookId"] = "$bookId"
        hashMap["timestamp"] = "$timestamp"
        hashMap["comment"] = "$comment"
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to db
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).child("Comments").child(timestamp)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Sedang Menambahkan ...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "gagal menambahkan comment karena ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


    private fun loadBookDetails() {
        // Books > bookId > Details
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // ambil data
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    bookTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    bookUrl = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    // format date
                    val date = MyApplication.formatTimeStamp(timestamp.toLong())

                    // load pdf category
                    MyApplication.loadCategory(categoryId, binding.categoryTv)

                    // load pdf thumbnail
                    MyApplication.loadPdfFromUrlSinglePage(
                        "$bookUrl",
                        "$bookTitle",
                        binding.pdfView,
                        binding.progressBar,
                        binding.pagesTv
                    )

                    // load pdf size
                    MyApplication.loadPdfSize("$bookUrl", "$bookTitle", binding.sizeTv)

                    // set data
                    binding.titleTv.text = bookTitle
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = viewsCount
                    binding.dateTv.text = date

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun checkIsFavorite() {
        Log.d(TAG, "checkIsFavorite: Check apakah bukunya favorite atau tidak")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if (isInMyFavorite) {
                        // kalo ada di favorite maka
                        Log.d(TAG, "onDataChange: Tersedia di Favorite")
                        // ganti drawable icon fav
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_white,
                            0,
                            0
                        )
                        binding.favoriteBtn.text = "Remove Favorites"
                    } else {
                        // kalo gaada di favorite maka
                        Log.d(TAG, "onDataChange: Tidak Tersedia di Favorite")
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            R.drawable.ic_favorite_border_white,
                            0,
                            0
                        )
                        binding.favoriteBtn.text = "Add Favorite"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun addToFavorite() {
        Log.d(TAG, "addToFavorite: Tambah ke Favorit")
        val timestamp = System.currentTimeMillis()

        // setup data utk ditambah di db
        val hashMap = HashMap<String, Any>()
        hashMap["bookId"] = bookId
        hashMap["timestamp"] = timestamp

        // save to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "addToFavorite: Added to Favorites")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "addToFavorite: Failder to add to favorite due to ${e.message}")
                Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

//    private fun removeFromFavorite(context: Context, bookId: String) {
//        val TAG = "REMOVE_FAV_TAG"
//        Log.d(TAG, "removeFromFavorite: Removing from favorites")
//
//        val firebaseAuth = FirebaseAuth.getInstance()
//
//        // db ref
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
//            .removeValue()
//            .addOnSuccessListener {
//                Log.d(TAG, "removeFromFavorite: Removed from fav")
//                Toast.makeText(
//                    context,
//                    "Dihapus dari favorit",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .addOnFailureListener { e ->
//                Log.d(TAG, "removeFromFavorite: Failed to remove from fav due to ${e.message}")
//                Toast.makeText(
//                    context,
//                    "Gagal untuk menghapus karena ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
}