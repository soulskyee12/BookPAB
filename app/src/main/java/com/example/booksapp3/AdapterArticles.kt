package com.example.booksapp3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.booksapp3.databinding.RowArticlesBinding



class AdapterArticles : RecyclerView.Adapter<AdapterArticles.HolderArtiles>, Filterable {

    //context
    private var context: Context

    // ArrayList sebagai tempat pdf
    public var articleArrayList: ArrayList<ModelArticle>
    private var filterList: ArrayList<ModelArticle>

    // view binding
    private lateinit var binding: RowArticlesBinding

    // filter object
    private var filter: FilterArticles? = null

    //constructor
    constructor(context: Context, articleArrayList: ArrayList<ModelArticle>) : super() {
        this.articleArrayList = articleArrayList
        this.context = context
        this.filterList = articleArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderArtiles {
        // bind , inflate layout row_pdf_admin , menghubungkan pada tampilan xml
        binding = RowArticlesBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderArtiles(binding.root)
    }


    override fun onBindViewHolder(holder: HolderArtiles, position: Int) {
        /* Ambil Data
        * Set Data
        * Dan click Listener*/

        // get data
        val model = articleArrayList[position]
        val articleId = model.id
        val title = model.title
        val article = model.article
        val timestamp = model.timestamp

        // konversi timestamp ke dd/MM/yyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        // set data
        holder.titleTv.text = title
        holder.descriptionTv.text = article
        holder.dateTv.text = formattedDate

        // melihat detail lengkap
        // load pdf thumbnail
//        MyApplication.loadPdfFromUrlSinglePage(
//
//            title,
//            holder.pdfView,
//            holder.progressBar,
//            null
//        )
        // listener openDetailActivity
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra("articleId",articleId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articleArrayList.size // jumlah item
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterArticles(filterList, this)
        }
        return filter as FilterArticles
    }

    //Sebagai viewholder untuk row_article
    inner class HolderArtiles(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ui view di row_pdf_admin.xml
//        val pdfView = binding.pdfView
//        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val dateTv = binding.dateTv

    }

}