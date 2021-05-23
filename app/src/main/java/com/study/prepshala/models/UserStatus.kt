package com.study.prepshala.models

import java.util.*

class UserStatus {
    var name: String? = null
    var phone: String? = null
    var profileImage: String? = null
    var lastUpdated: Long = 0
    var updatedDate: Date? = null
    var statuses: ArrayList<Status>? = null

    constructor() {}
    constructor(name: String?, profileImage: String?, lastUpdated: Long, updatedDate: Date, statuses: ArrayList<Status>?, phone: String) {
        this.name = name
        this.profileImage = profileImage
        this.lastUpdated = lastUpdated
        this.statuses = statuses
        this.updatedDate = updatedDate
        this.phone = phone
    }

}