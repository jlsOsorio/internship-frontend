package com.internship.retailmanagement.dataclasses.operatingfunds

data class InsertOpFundItem (
    val entryQty: Double? = null,
    val exitQty: Double? = null,
    val moment: String? = null,
    val cashRegisterId: Long? = null
)