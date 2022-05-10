package com.internship.retailmanagement.dataclasses

data class InvoicedProductItem(
    val id: Long? = null,
    val quantity: Integer? = null,
    val productId: Long? = null,
    val productName: String? = null,
    val ivaValue: Integer? = null,
    val subTotalNoIva: Double? = null,
    val subTotalIva: Double? = null
)
