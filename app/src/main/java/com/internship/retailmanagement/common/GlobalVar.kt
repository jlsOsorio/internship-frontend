package com.internship.retailmanagement.common

import android.app.Application
import java.util.*

class GlobalVar : Application() {
    var userId: Long? = null
    var userCategory: String? = null
    var storeId: Long? = null
    var productId: Long? = null
    var isLoggedIn: Boolean = false
    var userRole: String? = null
}