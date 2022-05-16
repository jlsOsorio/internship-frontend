package com.internship.retailmanagement.dataclasses.operatingfunds

import com.internship.retailmanagement.dataclasses.CashRegisterItem

data class OpFundItem(
    val id: Long? = null,
    val entryQty: Double? = null,
    val exitQty: Double? = null,
    val cashRegister: CashRegisterItem? = null,
    val moment: String? = null,
)
