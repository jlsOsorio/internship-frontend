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
const val BASE_URL = "http://localhost:8080"

/**
 * Service that provides the JSON result from the base URL, based on Retrofit.
 */
object ServiceGenerator {
    //Tentar implementar interceptor para capturar respostas dos erros de forma global
    /*var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request: Request = chain.request()
            val response = chain.proceed(request)

            // todo deal with the issues the way you need to
            if (response.code >= 400) {
                /*var jsonObject = JSONObject(response.body!!.string())
                val message : String = jsonObject.getString("message")
                val builder = AlertDialog.Builder()
                builder.setTitle("ERROR")
                builder.setIcon(R.drawable.remove_icon)
                builder.setMessage(message)
                builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
                    dialogInterface.cancel()
                }
                builder.show()*/
                return@Interceptor response
            }
            response
        })
        .build()*/

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