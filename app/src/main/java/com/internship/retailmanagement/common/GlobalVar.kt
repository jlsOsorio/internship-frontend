package com.internship.retailmanagement.common

import android.app.Application

class GlobalVar : Application() {
    var userId: Long? = null
    var storeId: Long? = null
}