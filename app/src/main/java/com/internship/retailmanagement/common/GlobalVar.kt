package com.internship.retailmanagement.common

import android.app.Application
import java.util.*

class GlobalVar : Application() {
    var userId: Long? = null
    var userName: String? = null
    var userEmail: String? = null
    var userNif: Long? = null
    var userAddress: String? = null
    var userCouncil: String? = null
    var userZipCode: String? = null
    var userBirthDate: Date? = null
    var userPhone: String? = null
    var userCategory: String? = null
    var userStatus: String? = null
    var storeId: Long? = null
}