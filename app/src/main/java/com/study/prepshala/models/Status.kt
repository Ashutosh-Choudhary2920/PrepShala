package com.study.prepshala.models

import java.util.*

class Status {
    var imageUrl: String? = null
    var timeStamp: Long = 0
    var updatedDate: Date? = null

    constructor() {}
    constructor(imageUrl: String?, timeStamp: Long, updatedDate: Date?) {
        this.imageUrl = imageUrl
        this.timeStamp = timeStamp
        this.updatedDate = updatedDate
    }
}