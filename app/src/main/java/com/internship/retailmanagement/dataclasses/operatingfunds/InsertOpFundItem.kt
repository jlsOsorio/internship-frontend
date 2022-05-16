package com.internship.retailmanagement.dataclasses.operatingfunds

import com.internship.retailmanagement.dataclasses.CashRegisterItem

data class InsertOpFundItem (
    val entryQty: Double? = null,
    val exitQty: Double? = null,
    val moment: String? = null,
    val cashRegisterId: Long? = null
)