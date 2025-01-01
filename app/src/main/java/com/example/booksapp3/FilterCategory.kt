package com.example.booksapp3

import android.view.Display.Mode
import android.widget.Filter

class FilterCategory(
    private var filterList: ArrayList<ModelCategory>,
    private var adapterCategory: AdapterCategory
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        // Cek apakah constraint kosong atau tidak
        if (constraint != null && constraint.isNotEmpty()) {
            // Ubah constraint menjadi lowercase
            val query = constraint.toString().lowercase()

            val filteredModels: ArrayList<ModelCategory> = ArrayList()
            for (model in filterList) {
                // Bandingkan dengan lowercase juga
                if (model.category.lowercase().contains(query)) {
                    filteredModels.add(model)
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            // Jika kosong, tampilkan semua data
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>
        adapterCategory.notifyDataSetChanged()
    }
}
