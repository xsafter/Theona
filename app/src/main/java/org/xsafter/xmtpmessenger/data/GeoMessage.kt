package org.xsafter.xmtpmessenger.data

import com.google.gson.annotations.SerializedName

data class GeoMessage(@SerializedName("latitude") val latitude: Double, @SerializedName("longitude") val longitude: Double) {
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
}