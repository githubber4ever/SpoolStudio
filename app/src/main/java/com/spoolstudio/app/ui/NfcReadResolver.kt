package com.spoolstudio.app.ui

import com.spoolstudio.app.data.remote.spoolman.SpoolmanService
import com.spoolstudio.app.domain.models.FilamentSpool
import com.spoolstudio.app.domain.models.OpenSpoolData
import com.spoolstudio.app.domain.models.normalizeCardUid
import com.spoolstudio.app.hardware.nfc.NfcTagReadResult
import kotlinx.coroutines.CancellationException
import com.spoolstudio.app.ui.screens.isBambuRfidDump
import com.spoolstudio.app.ui.screens.parseBambuRfidFormData
import com.spoolstudio.app.data.local.MaterialDatabase

/** Tag contents can be displayed offline; an embedded spool ID is not a server-confirmed link. */
fun localNfcTagData(tag: NfcTagReadResult): OpenSpoolData? {
    parseNfcTagData(tag.data)?.readData?.let { data ->
        return data.copy(spoolId = null, cardUid = normalizeCardUid(tag.cardUid).ifBlank { data.cardUid })
    }
    val raw = tag.data ?: return null
    if (!isBambuRfidDump(raw)) return null
    val data = parseBambuRfidFormData(raw, "Unknown")
    if (data.colorHex.isNullOrBlank()) return null
    val material = MaterialDatabase.getMaterial(data.material ?: "Unknown")
    return OpenSpoolData(
        type = data.material ?: "Unknown", colorHex = data.colorHex, brand = "Bambu Lab",
        minTemp = (data.minHotend ?: material?.defaultMinTemp ?: 200).toString(),
        maxTemp = (data.maxHotend ?: material?.defaultMaxTemp ?: 220).toString(),
        bedMinTemp = (data.bedTemp ?: material?.defaultBedMinTemp)?.toString(),
        bedMaxTemp = (data.bedTemp ?: material?.defaultBedMaxTemp)?.toString(),
        subtype = data.normalizedVariant,
        cardUid = normalizeCardUid(tag.cardUid).ifBlank { data.uid },
        formRemainingWeight = data.spoolWeightGrams?.toFloat()
    )
}

data class NfcReadResolution(
    val localData: OpenSpoolData?,
    val matchedSpool: FilamentSpool? = null,
    val lookupError: Exception? = null,
    val lookupAttempted: Boolean = false
)

class NfcReadResolver(
    private val lookup: suspend (String, String) -> FilamentSpool? = { server, uid ->
        SpoolmanService(server).findSpoolByCardUid(uid)
    }
) {
    suspend fun resolve(server: String, tag: NfcTagReadResult, localData: OpenSpoolData? = localNfcTagData(tag)): NfcReadResolution {
        val uid = normalizeCardUid(tag.cardUid)
        if (server.isBlank() || uid.isBlank()) return NfcReadResolution(localData)
        return try {
            NfcReadResolution(localData, matchedSpool = lookup(server, uid), lookupAttempted = true)
        } catch (e: CancellationException) { throw e
        } catch (e: Exception) {
            NfcReadResolution(localData, lookupError = e, lookupAttempted = true)
        }
    }
}
