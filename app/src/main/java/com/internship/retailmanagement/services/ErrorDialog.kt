package com.internship.retailmanagement.services

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object ErrorDialog {

    fun setDialog(context: Context, message: String) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(com.internship.retailmanagement.R.string.oops)
        builder.setIcon(com.internship.retailmanagement.R.drawable.warning_icon)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
            dialogInterface.cancel()
        }
        return builder
    }
}