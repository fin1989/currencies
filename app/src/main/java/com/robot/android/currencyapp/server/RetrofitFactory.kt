package com.robot.android.currencyapp.server

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.robot.android.currencyapp.BuildConfig
import com.robot.android.currencyapp.Constants.Companion.baseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactory {

    val builder: Retrofit
        get() {
            val builder = Retrofit.Builder()
            val client = client
            val converter = createGsonConverter()
            return builder
                .baseUrl(baseUrl)
                .addConverterFactory(converter)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(client)
                .build()
        }

    val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(10, TimeUnit.SECONDS)
            builder.readTimeout(10, TimeUnit.SECONDS)

            builder.addInterceptor(BaseInterceptor())

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(headerLoggerInterceptor)
            }

            return builder.build()
        }

    protected fun createGsonConverter(): Converter.Factory {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(HttpJsonResponse::class.java, ResponseJsonDeserializer())
        val gson = gsonBuilder.create()
        return GsonConverterFactory.create(gson)
    }

    internal// set your desired log level
    val headerLoggerInterceptor: HttpLoggingInterceptor
        get() {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC

            return logging
        }
}
