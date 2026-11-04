package com.spoolstudio.app.ui

import com.spoolstudio.app.data.local.MaterialDatabase
import com.spoolstudio.app.domain.models.FilamentSpool
import com.spoolstudio.app.hardware.nfc.NfcTagReadResult
import com.spoolstudio.app.ui.screens.SpoolFormState
import com.spoolstudio.app.ui.screens.applySpoolSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class NfcReadResolverTest {
    private val tag = NfcTagReadResult(
        data = """{"protocol":"openspool","type":"PETG","brand":"Generic","color_hex":"#12ab34","min_temp":"230","max_temp":"250","spool_id":"99","card_uid":"FFFF"}""",
        cardUid = "AA:BB"
    )

    @Test fun unreachableServerPreservesTagColorThroughFormLoading() = runBlocking {
        val resolver = NfcReadResolver { _, _ -> throw IOException("Server unreachable") }
        val result = resolver.resolve("http://unreachable.invalid", tag)
        assertTrue(result.lookupAttempted)
        assertNotNull(result.lookupError)
        assertNull(result.matchedSpool)
        assertNull(result.localData!!.spoolId)
        assertEquals("AABB", result.localData.cardUid)
        val form = SpoolFormState(MaterialDatabase.materials.first())
        form.applySpoolSource(FilamentSpool.fromOpenSpool(result.localData), SpoolMode.CREATE, emptyList())
        form.applyAutomaticFilamentName()
        assertEquals("12AB34", form.colorHex)
        assertEquals("12AB34", form.colorHexInput)
        assertEquals("PETG", form.filamentType)
    }

    @Test fun missingServerDoesNotAttemptLookupOrLoseColor() = runBlocking {
        val resolver = NfcReadResolver { _, _ -> error("Must not contact server") }
        val result = resolver.resolve("", tag)
        assertFalse(result.lookupAttempted)
        assertNull(result.lookupError)
        assertEquals("#12ab34", result.localData!!.colorHex)
        assertNull(result.localData.spoolId)
    }

    @Test fun payloadCanBeDisplayedBeforeNetworkLookupCompletes() {
        val local = localNfcTagData(tag)!!
        assertEquals("#12ab34", local.colorHex)
        assertNull(local.spoolId)
    }

    @Test fun uidOnlyTagCannotSupplyColorOffline() = runBlocking {
        val result = NfcReadResolver { _, _ -> throw IOException("Offline") }
            .resolve("http://unreachable.invalid", NfcTagReadResult(data = null, cardUid = "AABB"))
        assertNull(result.localData)
        assertNull(result.matchedSpool)
        assertNotNull(result.lookupError)
    }

    @Test fun confirmedServerMatchStillTakesPriorityOverTagContents() = runBlocking {
        val spool = FilamentSpool.fromOpenSpool(localNfcTagData(tag)!!).copy(id = 7, colorHex = "FFFFFF")
        val result = NfcReadResolver { _, uid -> assertEquals("AABB", uid); spool }.resolve("http://server", tag)
        assertEquals("FFFFFF", result.matchedSpool!!.colorHex)
        assertEquals(7, result.matchedSpool.id)
    }

    @Test fun cancelledScanDoesNotBecomeOfflineFallback() = runBlocking {
        try {
            NfcReadResolver { _, _ -> throw CancellationException("Cancelled") }.resolve("http://server", tag)
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { }
    }

    @Test fun decodedBambuColorAndWeightRemainAvailableOffline() = runBlocking {
        val vendorTag = NfcTagReadResult("""
            Bambu RFID Parsed
            Filament Type: PETG
            Detailed Type: PETG Basic
            Filament Color: #12AB34 / Green
            Min Hotend: 230 C
            Max Hotend: 250 C
            Spool Weight: 1000 g
            UID: FFFF
        """.trimIndent(), "AABB")
        val result = NfcReadResolver { _, _ -> throw IOException("Offline") }.resolve("http://server", vendorTag)
        val spool = FilamentSpool.fromOpenSpool(result.localData!!)
        assertEquals("12AB34", spool.colorHex)
        assertEquals(1000f, spool.remainingWeight)
        assertEquals("Bambu Lab", spool.brand)
        assertEquals("AABB", result.localData.cardUid)
        assertNull(spool.id)
    }
}
