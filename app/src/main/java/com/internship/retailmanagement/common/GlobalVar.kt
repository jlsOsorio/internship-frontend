package com.internship.retailmanagement.common

import android.app.Application
import java.util.*

class GlobalVar : Application() {
    var userId: Long? = null
    var userCategory: String? = null
    var userStatus: String? = null
    var storeId: Long? = null
    var storeAddress: String? = null
    var storeCouncil: String? = null
    var storeZipCode: String? = null
    var storeContact: String? = null
    var storeNumberCR: Int? = null
    var storeStatus: String? = null
    var productId: Long? = null
    var productName: String? = null
    var productStock: Int? = null
    var productIva: Int? = null
    var productGrossPrice: Double? = null
    var opFundId: Long? = null
    var opFundEntryQty: Double? = null
    var opFundExitQty: Double? = null
    var opFundCashRegister: Long? = null
    var opFundMoment: String? = null
    var typeMovement: String? = null
    var isLoggedIn: Boolean = false
    var userRole: String? = null
}