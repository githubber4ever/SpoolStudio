package com.spoolstudio.app.domain.models

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class TagStorageModeTest {
    @Test fun previousPreferencesArePreservedAndDefaultIsBoth() {
        assertEquals(TagStorageMode.BOTH, TagStorageMode.fromStoredValue(null))
        assertEquals(TagStorageMode.NATIVE_ONLY, TagStorageMode.fromStoredValue(null, true))
        assertEquals(TagStorageMode.LEGACY_ONLY, TagStorageMode.fromStoredValue("card_uids", true))
        TagStorageMode.entries.forEach { assertEquals(it, TagStorageMode.fromStoredValue(it.storedValue)) }
        assertEquals(TagStorageMode.BOTH, TagStorageMode.fromStoredValue("unknown"))
    }

    private val spools = Gson().fromJson("""[
      {"id":1,"filament":{"id":3,"name":"White","material":"PETG"},"extra":{"card_uids":"AABB"},"tags":[]},
      {"id":2,"filament":{"id":3,"name":"White","material":"PETG"},"extra":{"card_uids":"CCDD"},"tags":[{"uid":"AABB"}]}
    ]""", Array<SpoolmanSpool>::class.java).toList()

    @Test fun bothUsesNativeOwnerOverStaleLegacyEntry() {
        val result = spoolTagViews(spools, TagStorageMode.BOTH)
        assertTrue(result[0].cardUids.isEmpty())
        assertEquals(listOf("AABB", "CCDD"), result[1].cardUids)
    }
    @Test fun legacyOnlyIgnoresNativeOwnershipAndDoesNotDisplayNativeTags() {
        val result = spoolTagViews(spools, TagStorageMode.LEGACY_ONLY)
        assertEquals(listOf("AABB"), result[0].cardUids)
        assertEquals(listOf("CCDD"), result[1].cardUids)
        assertTrue(result.all { it.nativeTagUids.isEmpty() })
    }
    @Test fun nativeOnlyNeverDisplaysLegacyLinks() {
        val result = spoolTagViews(spools, TagStorageMode.NATIVE_ONLY)
        assertTrue(result[0].cardUids.isEmpty())
        assertEquals(listOf("AABB"), result[1].cardUids)
    }
    @Test fun filamentNativeTagSuppressesLegacySpoolOnlyWhenReadingBoth() {
        val filament = Gson().fromJson("""{"id":9,"name":"Swatch","material":"PETG","tags":[{"uid":"AABB"}]}""", SpoolmanFilament::class.java)
        assertTrue(spoolTagViews(spools.take(1), TagStorageMode.BOTH, listOf(filament)).single().cardUids.isEmpty())
        assertEquals(listOf("AABB"), spoolTagViews(spools.take(1), TagStorageMode.LEGACY_ONLY, listOf(filament)).single().cardUids)
    }
}
