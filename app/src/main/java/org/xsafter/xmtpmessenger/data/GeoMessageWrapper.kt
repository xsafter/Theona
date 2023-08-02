package org.xsafter.xmtpmessenger.data

import androidx.annotation.Keep

@Keep
data class GeoMessageWrapper(val geoMessage: GeoMessage, val user: User)