package com.spoolstudio.app.data.local

import android.content.Context
import com.spoolstudio.app.domain.models.TagStorageMode

/** Persisted per server: changing a printer profile must not change another server's policy. */
object TagStorageSettings {
    private var context: Context? = null
    fun initialize(context: Context) { this.context = context.applicationContext }
    private fun serverKey(url: String) = url.trim().trimEnd('/')
    fun mode(url: String): TagStorageMode {
        val prefs = context?.getSharedPreferences("tag_storage", Context.MODE_PRIVATE)
        return TagStorageMode.fromStoredValue(
            prefs?.getString("tag_storage_mode:" + serverKey(url), null),
            prefs?.getBoolean("native_tags_only:" + serverKey(url), false) ?: false
        )
    }
    fun setMode(url: String, value: TagStorageMode) {
        context?.getSharedPreferences("tag_storage", Context.MODE_PRIVATE)?.edit()
            ?.putString("tag_storage_mode:" + serverKey(url), value.storedValue)?.apply()
    }
}
