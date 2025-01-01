package com.example.booksapp3

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.booksapp3.databinding.ActivityPdfViewBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityPdfViewBinding


    // TAG
    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    // id buku
    var bookId = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = resources.getColor(R.color.item_utama)
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ambil buku dari intent, untuk diload di db
        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        // listener back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Ambil Pdf url dari databes")
        // ambil url buku pake id buku
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // ambil buku url
                    val pdfUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                    // load pdf pake url dari storage
                    loadBookFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

//    private fun loadBookFromUrl(pdfUrl: String) {
//        Log.d(TAG, "loadBookFromUrl: Ambil pdf dari storage fb pake url")
//
//        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
//        val localFile = File.createTempFile("temp", "pdf")
//
////        reference.getBytes(Constants.MAX_BYTES_PDF)
//        reference.getFile(localFile)
//            .addOnSuccessListener {
//                Log.d(TAG, "loadBookFromUrl: pdf diambil dari Url")
//
//                // load pdf
////                binding.pdfView.fromBytes(bytes)
//                binding.pdfView.fromFile(localFile)
//                    .defaultPage(0)
////                    .swipeHorizontal(false)
////                    .spacing(10)
//                    .swipeHorizontal(true)
//                    .pageSnap(true)
//                    .autoSpacing(true)
//                    .pageFling(true)
//                    .onPageChange { page, pageCount ->
//                        // set halaman sekarang dan totoal di toolbar subt.
//                        val currentPage = page + 1 // page mulai dari 0 jadi +1 from 1
//                        binding.toolbarSubTitleTv.text = "$currentPage/$pageCount"
//                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
//                    }
//                    .onError { t ->
//                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
//                    }
//                    .onPageError { page, t ->
//                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
//                    }
//                    .load()
//                binding.progressBar.visibility = View.GONE
//            }
//            .addOnFailureListener { e ->
//                Log.d(TAG, "loadBookFromUrl: Gagal mengambil pdf karena ${e.message}")
//                binding.progressBar.visibility = View.GONE
//            }
//    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Checking if PDF exists in cache")

        // Define the file path with a unique name based on the bookId
        val tempFile = File(cacheDir, "temp_book_${bookId}.pdf")

        if (tempFile.exists()) {
            Log.d(TAG, "loadBookFromUrl: PDF exists in cache, loading from file")
            // Load the PDF directly from the existing file
            binding.pdfView.fromFile(tempFile)
                .defaultPage(0)
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true)
                .scrollHandle(DefaultScrollHandle(this))
                .onPageChange { page, pageCount ->
                    val currentPage = page + 1
                    binding.toolbarSubTitleTv.text = "$currentPage/$pageCount"
                    Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                }
                .onError { t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }
                .onPageError { page, t ->
                    Log.d(TAG, "loadBookFromUrl: ${t.message}")
                }
                .load()
            binding.progressBar.visibility = View.GONE
        } else {
            Log.d(TAG, "loadBookFromUrl: PDF not found in cache, downloading")
            // Download the file and save it to the cache directory
            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            reference.getFile(tempFile)
                .addOnSuccessListener {
                    Log.d(TAG, "loadBookFromUrl: PDF downloaded successfully")

                    // Load the PDF after download
                    binding.pdfView.fromFile(tempFile)
                        .defaultPage(0)
                        .swipeHorizontal(true)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .scrollHandle(DefaultScrollHandle(this))
                        .onPageChange { page, pageCount ->
                            val currentPage = page + 1
                            binding.toolbarSubTitleTv.text = "$currentPage/$pageCount"
                            Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                        }
                        .onError { t ->
                            Log.d(TAG, "loadBookFromUrl: ${t.message}")
                        }
                        .onPageError { page, t ->
                            Log.d(TAG, "loadBookFromUrl: ${t.message}")
                        }
                        .load()
                    binding.progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "loadBookFromUrl: Failed to download PDF: ${e.message}")
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

}