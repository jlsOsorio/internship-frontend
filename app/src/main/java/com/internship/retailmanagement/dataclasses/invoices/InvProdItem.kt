package com.internship.retailmanagement.dataclasses.invoices

data class InvProdItem(
    val id: Long? = null,
    val quantity: Int? = null,
    val productId: Long? = null,
    val productName: String? = null,
    val ivaValue: Int? = null,
    val subTotalNoIva: Double? = null,
    val subTotalIva: Double? = null
)
