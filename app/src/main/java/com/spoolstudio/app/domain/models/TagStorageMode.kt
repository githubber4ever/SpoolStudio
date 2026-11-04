package com.spoolstudio.app.domain.models

enum class TagStorageMode(val storedValue: String, val label: String, val usesNative: Boolean, val usesLegacy: Boolean) {
    BOTH("both", "Both (compatibility)", true, true),
    LEGACY_ONLY("card_uids", "Only card_uids", false, true),
    NATIVE_ONLY("native", "Only native tags", true, false);

    companion object {
        fun fromStoredValue(value: String?, oldNativeOnly: Boolean = false): TagStorageMode =
            entries.firstOrNull { it.storedValue == value } ?: if (oldNativeOnly) NATIVE_ONLY else BOTH
    }
}
