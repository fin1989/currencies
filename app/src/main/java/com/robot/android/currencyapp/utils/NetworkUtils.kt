package com.robot.android.currencyapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {

    fun isConnectingToInternet(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.getActiveNetworkInfo()
            ni?.let {
                (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE))
            } ?: false
        } else {
            val network = cm.activeNetwork
            val networkCapabilities = cm.getNetworkCapabilities(network)

            networkCapabilities?.let {
                (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                ))
            } ?: false
        }


        /*val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting*/
    }
}