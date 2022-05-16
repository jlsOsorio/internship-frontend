package com.internship.retailmanagement.dataclasses.products

data class InsertProductItem(
    val name: String? = null,
    val stock: Int? = null,
    val ivaValue: Int? = null,
    val grossPrice: Double? = null,
)
