package com.internship.retailmanagement.dataclasses.products

import com.internship.retailmanagement.dataclasses.IvaItem

data class UpdateProductItem(
    val name: String? = null,
    val ivaValue: Int? = null,
    val grossPrice: Double? = null,
)
