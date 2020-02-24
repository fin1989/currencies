package com.robot.android.currencyapp.server

import com.robot.android.currencyapp.CurrencyModel
import org.json.JSONObject
import retrofit2.Response

class CurrenciesRepository(private val requestService: ICurrrenciesService) {
    var error : String? = null

    suspend fun getCurrencies(baseCurrency: String = "EUR"): CurrencyModel? {
        val endpoint = createEndpoint(baseCurrency)
        val response = safeApiCall(
            //await the result of deferred type
            call = { requestService.getCurrenciesAsync(endpoint) }
            //convert to mutable list
        )?.jsonObject
        return parseResponse(response)
    }

    fun getResponseError(): String? {
        return error
    }

    private fun parseResponse(json: JSONObject?): CurrencyModel? {
        return json?.let {
            if (it.has("errorMessage")) {
                error = json.optString("errorMessage")
                null
            } else {
                CurrencyModel(it)
            }
        }
    }

    private fun createEndpoint(baseCurrency: String = "EUR"): String {
        return "api/android/latest?base=$baseCurrency"
    }

    private suspend fun safeApiCall(call: suspend () -> Response<HttpJsonResponse>): HttpJsonResponse? {
        val result = apiOutput(call)

        var output: HttpJsonResponse? = null

        output = when (result) {
            is Output.Success ->
                result.output
            is Output.Error ->
                result.error
        }
        return output

    }

    private suspend fun apiOutput(call: suspend () -> Response<HttpJsonResponse>): Output<HttpJsonResponse> {
        val response = call.invoke()

        return if (response.isSuccessful) {
            Output.Success(response.body()!!)
        } else {
            Output.Error(HttpJsonResponse(onErrorResponse(response)))
        }
    }

    private fun onErrorResponse(response: Response<HttpJsonResponse>?): JSONObject {
        val json = JSONObject()
        json.put("errorMessage", response?.message())
        return json
    }
}