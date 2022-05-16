package com.internship.retailmanagement.dataclasses.products

data class ProductItem(
    val id: Long? = null,
    val name: String? = null,
    val stock: Integer? = null,
    val ivaValue: Integer? = null,
    val grossPrice: Double? = null,
    val taxedPrice: Double? = null
)
