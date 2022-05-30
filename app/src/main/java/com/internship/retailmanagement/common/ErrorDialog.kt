package com.internship.retailmanagement.common

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.controllers.MainActivity

object ErrorDialog {

    fun setDialog(context: AppCompatActivity, message: String) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(com.internship.retailmanagement.R.string.oops)
        builder.setIcon(com.internship.retailmanagement.R.drawable.warning_icon)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
            dialogInterface.cancel()
        }
        return builder
    }

    fun setPermissionDialog(context: AppCompatActivity, message: String) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(com.internship.retailmanagement.R.string.no_permisson)
        builder.setIcon(com.internship.retailmanagement.R.drawable.remove_icon)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
            val intent = Intent(context, MainActivity::class.java)
            dialogInterface.cancel()
            context.startActivity(intent)
            context.finish()
        }
        return builder
    }
}