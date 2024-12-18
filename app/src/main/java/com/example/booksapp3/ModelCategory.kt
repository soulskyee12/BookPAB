package com.example.booksapp3



class ModelCategory {
    // ambil variable dari database
    var id: String = ""
    var category: String = ""
    var timestamp: Long = 0
    var uid: String =""

    //constructor untuk firebase
    constructor()

    //param untuk constructor
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
}