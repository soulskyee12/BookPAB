package com.example.booksapp3

import android.widget.Filter

// digunakan untuk filter  data dari recycler dan search pdf dari pdf list di recyclr

class FilterArticles : Filter {

    // array list buat nge search
    var filterList: ArrayList<ModelArticle>
    // adapter dimana dibutuhkan filter untuk diimplementasi
    var adapterArticles: AdapterArticles

    //constructor
    constructor(filterList: ArrayList<ModelArticle>, adapterArticles: AdapterArticles) {
        this.filterList = filterList
        this.adapterArticles = adapterArticles
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint // value untuk search
        val results = FilterResults()

        // value yang akan dicari tidak boleh null dan kosong
        if(constraint != null && constraint.isNotEmpty()) {
            // ubah uppercase, lowr case tuk mnghindari key sens
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelArticle>()
            for (i in filterList.indices) {
                // validasi jika cocok
                if (filterList[i].title.lowercase().contains(constraint)) {
                    // cari value yang mirip sama value di list, tambahkan ke filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels

        }
        else {
            // search value is either null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        // menerapkan perubahan filter
        adapterArticles.articleArrayList = results.values as ArrayList<ModelArticle>

        // notify changes
        adapterArticles.notifyDataSetChanged()
    }
}