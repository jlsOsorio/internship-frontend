package com.internship.retailmanagement.common

import android.app.Application
import android.content.SharedPreferences
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import com.internship.retailmanagement.dataclasses.invoices.OrderProdItem
import java.io.FileOutputStream

class GlobalVar : Application() {
    var userId: Long? = null
    var emailLoggedIn: String? = null
    var storeUserLogged: Long? = null
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
    var invoiceNumber: Long? = null
    var invProdsList: MutableList<InvProdItem> = arrayListOf()
    var invCashRegister: Long? = null
    var prodsList: MutableList<OrderProdItem> = arrayListOf()
    var prodsNames: MutableList<String> = arrayListOf()
    var mapProds: MutableMap<String, Int> = mutableMapOf()
    var fileInvoices: FileOutputStream? = null
    var isMyProfile: Boolean = false
    var userToken: String? = null
    var userRole: String? = null
}