package com.example.booksapp3

class ModelComment {


    var id = ""
    var bookId = ""
    var timestamp = ""
    var comment = ""
    var uid = ""


    //constructor buat db firebase
    constructor()

    //param constructor
    constructor(id: String, bookId: String, timestamp: String, comment: String, uid: String) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
    }

}