package org.xsafter.xmtpmessenger.data

import org.xsafter.xmtpmessenger.data.model.GeoMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoSingleton @Inject constructor() {
    var geoMessages: MutableList<GeoMessage>? = mutableListOf()
}
