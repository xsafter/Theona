package org.xsafter.xmtpmessenger.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeoMessage(@SerializedName("latitude") val latitude: Double, @SerializedName("longitude") val longitude: Double) {
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
}