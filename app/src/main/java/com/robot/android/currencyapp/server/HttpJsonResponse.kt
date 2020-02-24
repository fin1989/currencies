package com.robot.android.currencyapp.server

import org.json.JSONObject

class HttpJsonResponse() {

    lateinit var jsonObject: JSONObject

    constructor(json: JSONObject) : this() {
        jsonObject = json
    }
}
