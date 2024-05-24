package com.ddm.closethatholegram.modelos

class Post {
    var userId: String? = null
    var imageUrl: String? = null
    var description: String? = null
    var location: String? = null
    var timestamp: Long = System.currentTimeMillis()

    constructor()

    constructor(
        userId: String?,
        imageUrl: String?,
        description: String?,
        location: String?,
        timestamp: Long
    ) {
        this.userId = userId
        this.imageUrl = imageUrl
        this.description = description
        this.location = location
        this.timestamp = timestamp
    }
}
