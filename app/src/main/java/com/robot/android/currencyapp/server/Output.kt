package com.robot.android.currencyapp.server

sealed class Output<out T : Any>{
    data class Success<out T : Any>(val output : T) : Output<T>()
    data class Error<out T : Any>(val error: T)  : Output<T>()
}