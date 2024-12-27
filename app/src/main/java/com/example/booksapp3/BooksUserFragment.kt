package com.example.booksapp3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.booksapp3.databinding.FragmentBooksUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BooksUserFragment : Fragment {

    private lateinit var binding: FragmentBooksUserBinding

    companion object {
        private const val TAG = "BOOK_USER_TAG"

        // Menerima data dari activity untuk memuat buku categoryId, category, uid
        fun newInstance(
            categoryId: String,
            category: String,
            uid: String
        ): BooksUserFragment {
            val fragment = BooksUserFragment()
            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ambil argument yang diteruskan di newInstance method
        val args = arguments
        if (args != null) {
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context), container, false)

        // Load pdf sesuai dengan kategori
        Log.d(TAG, "onCreateView: Category: $category")
        when (category) {
            "All" -> {
                loadAllBooks()
            }
            "Populer" -> {
                loadMostViewedDownloadedBooks("viewsCount")
            }
            else -> {
                loadCategorizedBooks()
            }
        }

        // Search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu diimplementasikan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterPdfUser.filter.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: SEARCH EXCEPTION: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu diimplementasikan
            }
        })

        return binding.root
    }

    private fun loadAllBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelPdf::class.java)
                    model?.let { pdfArrayList.add(it) }
                }
                adapterPdfUser = AdapterPdfUser(requireContext(), pdfArrayList)
                binding.bookRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {
                // Tidak perlu diimplementasikan
            }
        })
    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        // Ambil 12 data terakhir (nilai terbesar) menurut viewsCount
        ref.orderByChild(orderBy)
            .limitToLast(12)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        model?.let { pdfArrayList.add(it) }
                    }
                    // Data akan diurutkan ascending (kecil -> besar)
                    // Balik (reverse) agar yang terbesar tampil paling atas
                    pdfArrayList.reverse()

                    adapterPdfUser = AdapterPdfUser(requireContext(), pdfArrayList)
                    binding.bookRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {
                    // Tidak perlu diimplementasikan
                }
            })
    }

    private fun loadCategorizedBooks() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        model?.let { pdfArrayList.add(it) }
                    }
                    adapterPdfUser = AdapterPdfUser(requireContext(), pdfArrayList)
                    binding.bookRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {
                    // Tidak perlu diimplementasikan
                }
            })
    }
}
