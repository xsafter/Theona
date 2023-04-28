package org.xsafter.xmtpmessenger

import android.accounts.AccountManager
import android.content.Context

class KeyUtil(val context: Context) {
    fun loadKeys(): String? {
        val accountManager = AccountManager.get(context)
        val accounts =
            accountManager.getAccountsByType(context.packageName)
        val account = accounts.firstOrNull() ?: return null
        return accountManager.getPassword(account)
    }
}