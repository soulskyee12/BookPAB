package com.example.booksapp3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booksapp3.databinding.ActivityPdfAddBinding
import com.example.booksapp3.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PdfListAdminActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityPdfListAdminBinding

    private companion object {
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    // Category ID and title
    private var categoryId = ""
    private var category = ""

    // ArrayList as a container for books, initialized immediately
    private var pdfArrayList: ArrayList<ModelPdf> = ArrayList()

    // Adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Validate and retrieve intent extras
        categoryId = intent.getStringExtra("categoryId") ?: throw IllegalArgumentException("Category ID must be provided")
        category = intent.getStringExtra("category") ?: throw IllegalArgumentException("Category must be provided")

        // Setup adapter with empty list
        adapterPdfAdmin = AdapterPdfAdmin(this, pdfArrayList)
        binding.booksRv.adapter = adapterPdfAdmin

        // Load PDFs/books
        loadPdfList()

        // Search functionality
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
               try{
                   adapterPdfAdmin.filter!!.filter(s.toString().toLowerCase())
               }
               catch (e: Exception){
                   Log.d(TAG, "onTextChanged: ${e.message}")
               }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun loadPdfList() {
        // Reference Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        if (model != null) {
                            pdfArrayList.add(model)
                        }
                    }
                    adapterPdfAdmin.notifyDataSetChanged()  // Notify adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database error: ${error.message}")
                }
            })
    }
}
