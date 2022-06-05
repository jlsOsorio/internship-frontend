package com.internship.retailmanagement.common

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.R
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.SignInActivity
import com.internship.retailmanagement.controllers.UserProfileActivity

object Utils {

    //Logout pop up
    fun logout(context: AppCompatActivity){

        //Get alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("LOGOUT")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("YES") { _,_ ->
            //Clear token
            val sessionManager =  SessionManager(context)
            sessionManager.deleteAuthToken()

            //Go to sign in activity
            val intent = Intent(context, SignInActivity::class.java)

            //Removes everything registered all the user way through the application (visited activities, sequence of usage, and so on)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            Toast.makeText(
                context,
                "You have successfully logged out.",
                Toast.LENGTH_SHORT
            ).show()

            context.startActivity(intent)
            context.finish()

        }
        builder.setNegativeButton("NO") { dialogInterface: DialogInterface, _ ->
            dialogInterface.cancel()
        }
        builder.show()
    }

    //redirect to login page if session expired
    fun redirectUnauthorized(context: AppCompatActivity, message: String?){
        //Get alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Session expired.")
        builder.setMessage("$message Please sign in again.")
        builder.setPositiveButton("OK") { _,_ ->
            //Clear token
            val sessionManager =  SessionManager(context)
            sessionManager.deleteAuthToken()

            //Go to sign in activity
            val intent = Intent(context, SignInActivity::class.java)

            //Removes everything registered all the user way through the application (visited activities, sequence of usage, and so on)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            context.startActivity(intent)
            context.finish()

        }
        builder.show()
    }

}