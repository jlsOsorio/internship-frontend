package com.internship.retailmanagement.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//base url
const val BASE_URL = "http://192.168.1.102:8080"

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