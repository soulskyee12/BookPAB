package com.example.booksapp3

class ModelArticle {

    // variabel
    var id: String = ""
    var title: String = ""
    var article: String = ""
    var timestamp: Long = 0
    var uid: String = ""
    var viewsCount: Long = 0

    // empty konstruktor, needed for firebase
    constructor()


    // konstruktor yang ada parameternya
    constructor(
        id: String,
        title: String,
        article: String,
        timestamp: Long,
        uid: String,
        viewsCount: Long

    ) {

        this.id = id
        this.viewsCount = viewsCount
        this.uid = uid
        this.title = title
        this.article = article
        this.timestamp = timestamp



    }
}