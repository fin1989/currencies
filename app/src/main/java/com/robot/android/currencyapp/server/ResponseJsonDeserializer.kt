package com.robot.android.currencyapp.server

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.json.JSONObject
import java.lang.reflect.Type

class ResponseJsonDeserializer : com.google.gson.JsonDeserializer<HttpJsonResponse> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HttpJsonResponse {
        var gson: String
        try {
            gson = json.asJsonObject.toString()
        } catch (e: Exception) {
            gson = json.asJsonArray.toString()
            gson = "{arr : $gson}"
        }

        val httpJsonResponse = HttpJsonResponse()
        val jsonObject = JSONObject(gson)
        httpJsonResponse.jsonObject = jsonObject


        return httpJsonResponse
    }
}
