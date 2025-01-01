package com.example.booksapp3

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityArticleAddBinding
import com.example.booksapp3.databinding.ActivityPdfAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ArticleAddActivity : AppCompatActivity() {

    //untuk setup view binding.
    private lateinit var binding: ActivityArticleAddBinding

    // firebase auth sperti biasa
    private lateinit var firebaseAuth: FirebaseAuth

    // pop up progress dialog
    private lateinit var progressDialog: ProgressDialog

    //TAG
    private val TAG = "ARTICLE_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
//        loadPdfCategories()

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //klik listener, kembali
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }


        //klik listener, upload pdf
        binding.submitBtn.setOnClickListener {
            /*Petunjuk,
            * 1- validasi data
            * 2 - upload pdf ke data firebase
            * 3 - ambil data url dari pdf yang diupload
            * 4 - upload info pdf ke firebase*/

            validateData()
        }
    }
    private var title = ""
    private var article = ""


    private fun validateData() {
        // 1 - validasi datra
        Log.d(TAG, "validateData: validasi data")

        // 2 - dapetin datanya
        title = binding.titleEt.text.toString().trim()
        article = binding.descriptionEt.text.toString().trim()


        //validasi data
        if (title.isEmpty()) {
            Toast.makeText(this,"Masukkan Judul...", Toast.LENGTH_SHORT).show()
        }
        else if (article.isEmpty()){
            Toast.makeText(this,"Masukkan artikel", Toast.LENGTH_SHORT).show()
        }
        else{
            // data tervalidasi
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        // 2 - upload pdf ke storage firebase
        Log.d(TAG, "uploadPdfToStorage: upload ke Storage...")

        //progress dialog
        progressDialog.setMessage("Uploading Article...")
        progressDialog.show()

        //timestamp
        val timeStamp = System.currentTimeMillis()


//        //path pdf di firebae storage
//        val filePathAndName = "Books/$timeStamp"

//        // storage reference
//        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
//        storageReference.putFile(pdfUri!!)
//            .addOnSuccessListener {taskSnapshot ->
//                Log.d(TAG, "uploadPdfToStorage: PDF telah terupload now getting url...")
//
//                //  3 - ambil data url dari pdf yang diupload
//                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
//                while (!uriTask.isSuccessful);
//                val uploadedPdfUrl = "${uriTask.result}"
//
//                uploadPdfInfoToDb(uploadedPdfUrl, timeStamp)
//            }
//            .addOnFailureListener{e ->
//                Log.d(TAG, "uploadPdfToStorage: Gagal Mengupload karena ${e.message}")
//                progressDialog.dismiss()
//                Toast.makeText(this,"Gagal Mengupload karena ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timeStamp: Long) {
//        //  4 - upload info pdf ke firebase
//        Log.d(TAG, "uploadPdfInfoToDb: Uploading ke database")
//        progressDialog.setMessage("Mengupload info pdf...")
//
        // uid dari user sekarang
        val uid = firebaseAuth.uid
//
        // setup untk mengupload data
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timeStamp"
        hashMap["title"] = "$title"
        hashMap["article"] = "$article"
        hashMap["timestamp"] = timeStamp
        hashMap["viewsCount"] = 0


        // db Reference DB > Books > BookId > (Book Info)
        val ref =  FirebaseDatabase.getInstance().getReference("Articles")
        ref.child("$timeStamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: Uploaded ke db")
                progressDialog.dismiss()
                Toast.makeText(this,"Uploaded...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfInfoToDb: gagal mengupload karena ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Gagal mengupload karena ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

//    private fun loadPdfCategories() {
//        Log.d(TAG, "loadPdfCategories: Loading kategori pdf...")
//        //init arrayList
//        categoryArrayList = ArrayList()
//
//        // mengambil data pada kategori DF > Categories
//        val ref = FirebaseDatabase.getInstance().getReference("Categories")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // membersihkan list sebelum menambah data
//                categoryArrayList.clear()
//                for (ds in snapshot.children){
//                    // ambil data
//                    val model = ds.getValue(ModelCategory::class.java)
//                    // menambahkan data ke arrayList
//                    categoryArrayList.add(model!!)
//                    Log.d(TAG,"onDataChange: ${model.category}")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//    }

//    private var selectedCategoryId = ""
//    private var selectedCategoryTitle = ""
//
//    private fun categoryPickDialog() {
//        Log.d(TAG, "categoryPickDialog: Menampilkan dialog pemilihan kategori")
//
//        // Pastikan categoriesArray dibuat dengan benar
//        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
//        for (i in categoryArrayList.indices) {
//            categoriesArray[i] = categoryArrayList[i].category
//            Log.d(TAG, "Category in Array: ${categoriesArray[i]}")
//        }
//
//        // Menampilkan AlertDialog
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Pick Category")
//            .setItems(categoriesArray) { _, which ->
//                selectedCategoryTitle = categoryArrayList[which].category
//                selectedCategoryId = categoryArrayList[which].id
//                binding.categoryTv.text = selectedCategoryTitle
//                Log.d(TAG, "Selected Category: $selectedCategoryTitle, ID: $selectedCategoryId")
//            }
//            .show()
//    }

//    private fun pdfPickIntent(){
//        Log.d(TAG, "pdfPickIntent: Sedang memilih pdf")
//
//        val intent = Intent()
//        intent.type = "application/pdf"
//        intent.action = Intent.ACTION_GET_CONTENT
//        pdfActivityResultLauncher.launch(intent)
//    }
//
//    val pdfActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult(),
//        ActivityResultCallback<ActivityResult> {result ->
//            if (result.resultCode == RESULT_OK) {
//                Log.d(TAG,"PDF Dipilih ")
//                pdfUri = result.data!!.data
//            }
//            else {
//                Log.d(TAG, "PDF Pick Cancelled")
//                Toast.makeText(this,"cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    )
}