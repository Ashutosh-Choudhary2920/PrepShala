package com.study.prepshala.models

class Messages {
    var messageId: String? = null
    var message: String? = ""
    var senderId: String? = null
    var imageUrl: String? = null
    var timestamp: Long = 0
    var feeling = -1
    var messageType: String = "text"

    constructor() {}
    constructor(message: String?, senderId: String?, timestamp: Long) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }

}