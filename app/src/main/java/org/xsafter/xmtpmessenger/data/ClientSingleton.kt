package org.xsafter.xmtpmessenger.data

import org.xmtp.android.library.Client
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientSingleton @Inject constructor() {
    var client: Client? = null
}
