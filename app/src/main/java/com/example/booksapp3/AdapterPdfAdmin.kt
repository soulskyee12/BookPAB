package com.example.booksapp3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.booksapp3.databinding.RowPdfAdminBinding

class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable {


    //context
    private var context: Context

    // ArrayList sebagai tempat pdf
    public var pdfArrayList: ArrayList<ModelPdf>
    private var filterList: ArrayList<ModelPdf>

    // view binding
    private lateinit var binding: RowPdfAdminBinding

    // filter object
    private var filter: FilterPdfAdmin? = null

    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.pdfArrayList = pdfArrayList
        this.context = context
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        // bind , inflate layout row_pdf_admin , menghubungkan pada tampilan xml
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfAdmin(binding.root)
    }


    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        /* Ambil Data
        * Set Data
        * Dan click Listener*/

        // get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        // konversi timestamp ke dd/MM/yyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        // set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        // melihat detail lengkap pdf

        // load category id
        MyApplication.loadCategory(categoryId, holder.categoryTv)

        // load pdf thumbnail
        MyApplication.loadPdfFromUrlSinglePage(
            pdfUrl,
            title,
            holder.pdfView,
            holder.progressBar,
            null
        )

        //load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)

        // listener moreBtn
        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        // listener openDetailActivity
        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId",pdfId)
            context.startActivity(intent)
        }
    }

    private fun moreOptionsDialog(model: ModelPdf, holder: HolderPdfAdmin) {
        // ambil id, url, title dari buku
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title

        // options untuk memperlihatkan dialog
        val options = arrayOf("Edit", "Delete")

        // alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) { dialog, position ->
                // handle item klik
                if (position == 0) {
                    // ketika edit diklik
                    val intent = Intent(context, PdfEditActivity::class.java)
                    intent.putExtra("bookId", bookId) // mengirimkan nilai bookId ke next activity
                    context.startActivity(intent) // Memulai activity
                } else if (position == 1) {
                    // ketika delete diklik

                    // konfirmasi dialog sebelum menghapus
                    MyApplication.deleteBook(context, bookId, bookUrl, bookTitle)
                }
            }
            .show() // Tampilkan dialog
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size // jumlah item
    }


    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterPdfAdmin(filterList, this)
        }
        return filter as FilterPdfAdmin
    }

    //Sebagai viewholder untuk row_pdf_admin
    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ui view di row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }
}