package com.example.booksapp3

import android.widget.Filter

class FilterPdfUser : Filter {

    // arraylist untuk search
    var filterList: ArrayList<ModelPdf>

    // adapter dimana filter akan diimplementasi
    var adapterPdfUser: AdapterPdfUser

    // constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) : super() {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        // value untuk search
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        // value search gak boleh null atau kosong
        if (constraint != null && constraint.isNotEmpty()) {

            // ubah upper case dan lower case agar tidak case sensitivity
            constraint = constraint.toString().uppercase()
            val filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices) {
                // validasi jika cocok
                if (filterList[i].title.uppercase().contains(constraint)) {
                    // value  cocok dengan apa yg dicari, add to list
                    filteredModels.add(filterList[i])
                }
            }
            // return filtered list dan size
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            // jika null atau kosong, maka kembali list dan size semula
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        // apply perubahan pd filter
        adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>

        // melihat perubahan
        adapterPdfUser.notifyDataSetChanged()
    }
}