package com.robot.android.currencyapp.server

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BaseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        populateHeaders(builder)
        return chain.proceed(builder.build())
    }

    fun populateHeaders(builder: Request.Builder) {
        builder.addHeader("Accept", "application/json")
        builder.addHeader("Content-Type", "application/json")
    }
}