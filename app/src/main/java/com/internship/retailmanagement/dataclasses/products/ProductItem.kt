package com.internship.retailmanagement.dataclasses.products

data class ProductItem(
    val id: Long? = null,
    val name: String? = null,
    val stock: Int? = null,
    val ivaValue: Int? = null,
    val grossPrice: Double? = null,
    val taxedPrice: Double? = null
)
