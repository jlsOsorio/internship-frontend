package com.internship.retailmanagement.dataclasses.invoices

import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.users.UserItem

data class InvoiceItem(
    val invoiceNumber: Long? = null,
    val transaction: String? = null,
    val user: UserItem? = null,
    val cashRegister: CashRegisterItem? = null,
    val invoicedProducts: MutableList<InvProdItem>? = null,
    val totalNoIva: Double? = null,
    val totalIva: Double? = null,
)
