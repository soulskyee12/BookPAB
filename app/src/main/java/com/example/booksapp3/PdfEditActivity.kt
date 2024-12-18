package com.example.booksapp3

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityPdfEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityPdfEditBinding

    private companion object{
        private const val TAG = "PDF_EDIT_TAG"
    }

    //book id from intent started from AdapterPdfAdmin
    private var bookId = ""

    // progress dialog
    private lateinit var progressDialog: ProgressDialog

    // array sebagi tempat category titles
    private lateinit var categoryTitleArrayList: ArrayList<String>

    // arrayList sebagai tempat category id
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil id buku ke info buku
        bookId = intent.getStringExtra("bookId")!!

        // setup utk progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadInfo()

        // listener goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // listener pick category
        binding.categoryTv.setOnClickListener{
            categoryDialog()
        }

        // listener click, begin update
        binding.submitBtn.setOnClickListener{
                validateData()
        }

    }

    private fun loadInfo() {
        Log.d(TAG, "loadInfo: Loading book Info")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // ambil info buku
                    selectedCategoryId = snapshot.child("CategoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()

                    //set to views
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)

                    // load book category info pake categoryId
                    Log.d(TAG, "onDataChange: Loading book category info")
                    val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                // ambil data category
                                val category = snapshot.child("category").value
                                // set ke textview
                                binding.categoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private var title = ""
    private var  description = ""
    private fun validateData() {
        // ambil data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        // validasi data
        if (title.isEmpty()){
            Toast.makeText(this,"Masukkan Judul",Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this,"Masukkan Deskripsi",Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty()){
            Toast.makeText(this,"Pilih Kategori",Toast.LENGTH_SHORT).show()
        }
        else{
            updatePdf()
        }
    }

    private fun updatePdf() {
        Log.d(TAG, "updatePdf: Mulai Update pdf info...")

        // show progress
        progressDialog.setMessage("Updating book info")
        progressDialog.show()

        // setup data untuk update ke db
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        
        // mulai update
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener { 
                progressDialog.dismiss()
                Log.d(TAG, "updatePdf: Updated Successfully...")
                Toast.makeText(this,"Updated successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "updatePdf: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this,"Gagal Karena ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryDialog() {

        // untuk string array
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]

            // alert dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Category")
                .setItems(categoriesArray){dialog, position ->
                    // listener save id dan title
                    selectedCategoryId = categoryIdArrayList[position]
                    selectedCategoryTitle = categoryTitleArrayList[position]

                    //set ke textview
                    binding.categoryTv.text = selectedCategoryTitle
                }
                .show()
        }
    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories: loading categories...")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // hapus list sebelum mulai menambahkan data
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()
                for (ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDataChange: Category ID $id")
                    Log.d(TAG, "onDataChange: Category  $category")

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}