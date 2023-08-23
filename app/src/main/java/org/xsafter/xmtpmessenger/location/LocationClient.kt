package org.xsafter.xmtpmessenger.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class AnyException(message: String): Exception()
}