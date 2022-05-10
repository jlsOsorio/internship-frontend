package com.internship.retailmanagement.dataclasses

data class InvoiceItem(
    val invoiceNumber: Long? = null,
    val transaction: String? = null,
    val user: UserItem? = null,
    val cashRegister: CashRegisterItem? = null,
    val invoicedProducts: MutableList<InvoicedProductItem>? = null,
    val totalNoIva: Double? = null,
    val totalIva: Double? = null
)
