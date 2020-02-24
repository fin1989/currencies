package com.robot.android.currencyapp.server

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ICurrrenciesService {
    @GET
    suspend fun getCurrenciesAsync(@Url url: String): Response<HttpJsonResponse>
}