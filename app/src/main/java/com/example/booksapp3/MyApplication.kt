package com.example.booksapp3

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {

        fun formatTimeStamp(timeStamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timeStamp
            // format dd/MM/yyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        // function
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
            val TAG = "PDF_SIZE_TAG"

            // dengan url kita bisa mendapatkan file yang berasal dari firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetaData ->
                    Log.d(TAG, "loadPdfSize: ambil metadata")
                    val bytes = storageMetaData.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    // convert bytes ke MB
                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb >= 1) {
                        sizeTv.text = "${String.format("%.2f", mb)} MB"
                    } else if (kb >= 1) {
                        sizeTv.text = "${String.format("%.2f", kb)} KB"
                    } else {
                        sizeTv.text = "${String.format("%.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "loadPdfSize: Gagal untuk mendapatkan meta data karena ${e.message}")
                }
        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ) {

            val TAG = "PDF_THUMBNAIL_TAG"

            // menggunakan url kita bisa mendapatkan file dan metada dari fire.db
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    // ubah ke pdfView
                    pdfView.fromBytes((bytes))
                        //.pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError { t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onPageError { page, t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onLoad { nbPages ->
                            Log.d(TAG, "loadPdfFromUrlSinglePage: Pages: $nbPages")
                            // pdf loading, bisa masukin halaman sama thumbnail
                            progressBar.visibility = View.INVISIBLE

                            // kalau parameter pagesTV gak null maka bisa atur berapa jumlah halaman
                            if (pagesTv != null) {
                                pagesTv.text = "$nbPages"
                            }
                        }
                        .load()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "loadPdfSize: Gagal untuk mendapatkan meta data karena ${e.message}")
                }
        }

        fun loadCategory(categoryId: String, categoryTv: TextView) {
            // memuat kateogri dari kategori id, firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        // get category
                        val category = "${snapshot.child("category").value}"

                        // set category
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun deleteBook(context: Context, bookId: String, bookUrl: String, bookTitle: String) {
            // detail dari parameter
            /*
            * 1 - context - digunakan ketika membutuhkan toast, progressdialog, dll..
            * 2 - bookId - utk menghapus buku dri db
            * 3 - bookUrl - menghapus buku dari storage
            * 4 - bookTitle - menampilkan dialog ...*/

            val TAG = "DELETE_BOOK_TAG"

            Log.d(TAG, "deleteBook: menghapus...")

            // progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Mohon Tunggu")
            progressDialog.setMessage("Deleting $bookTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG, "deleteBook: Menghapus dari storage")
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
            storageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteBook: Terhapus dari storage")
                    Log.d(TAG, "deleteBook: Mengahpus dari database...")

                    val ref = FirebaseDatabase.getInstance().getReference("Books")
                    ref.child(bookId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "Berhasil Terhapus", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "deleteBook: Menghapus dari db...")

                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Log.d(
                                TAG,
                                "deleteBook: Gagal menghapus buku dari db karena ${e.message}"
                            )
                            Toast.makeText(
                                context,
                                "Gagal menghapus  karena ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Log.d(
                        TAG,
                        "deleteBook: Gagal menghapus buku dari storage karena karena ${e.message}"
                    )
                    Toast.makeText(
                        context,
                        "Gagal menghapus  karena ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun incrementBookViewCount(bookId: String) {
            // ambil views dari buku sekarang
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.child(bookId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // ambil views
                        var viewsCount = "${snapshot.child("viewsCount").value}"

                        if (viewsCount == "" || viewsCount == "null") {
                            viewsCount = "0";
                        }

                        // increment views
                        val newViesCount = viewsCount.toLong() + 1

                        // Increment views
                        val hashMap = HashMap<String, Any>()
                        hashMap["viewsCount"] = newViesCount

                        // set ke database
                        val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                        dbRef.child(bookId)
                            .updateChildren(hashMap)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        public fun hapusDariFavorite(context: Context, bookId: String) {
            val TAG = "REMOVE_FAV_TAG"
            Log.d(TAG, "removeFromFavorite: Removing from favorites")

            val firebaseAuth = FirebaseAuth.getInstance()

            // db ref
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(TAG, "removeFromFavorite: Removed from fav")
                    Toast.makeText(
                        context,
                        "Dihapus dari favorit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "removeFromFavorite: Failed to remove from fav due to ${e.message}")
                    Toast.makeText(
                        context,
                        "Gagal untuk menghapus karena ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


    }


}