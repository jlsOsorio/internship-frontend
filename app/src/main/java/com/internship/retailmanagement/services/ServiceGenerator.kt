package com.internship.retailmanagement.services

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.ChangeUserDataActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.AccessController.getContext


//base url
const val BASE_URL = "http://192.168.1.64:8080"

/**
 * Service that provides the JSON result from the base URL, based on Retrofit.
 */
object ServiceGenerator {

    private val client = OkHttpClient.Builder().build()

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    fun <T> buildService(service: Class<T>): T {
        return retrofitBuilder.create(service)
    }
}