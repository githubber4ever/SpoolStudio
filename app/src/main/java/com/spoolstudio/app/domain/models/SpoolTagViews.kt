package com.spoolstudio.app.domain.models

/** Only BOTH reconciles stores. Single-store modes must ignore the other store entirely. */
fun spoolTagViews(
    spools: List<SpoolmanSpool>,
    mode: TagStorageMode,
    filaments: List<SpoolmanFilament> = emptyList()
): List<FilamentSpool> {
    val nativeOwners = if (mode.usesNative) spools.flatMap { spool ->
        spool.tags.orEmpty().map { it.uid to spool.id } + spool.filament.tags.orEmpty().map { it.uid to null }
    }.toMap() else emptyMap()
    val filamentTags = if (mode.usesNative) filaments.flatMap { it.tags.orEmpty().map { tag -> tag.uid } }.toSet() else emptySet()
    return spools.map { spool ->
        val native = if (mode.usesNative) spool.tags.orEmpty().map { it.uid } else emptyList()
        val legacy = if (mode.usesLegacy) parseCardUids(spool.extra.stringValue("card_uids") ?: spool.extra.stringValue("card_uid")) else emptyList()
        FilamentSpool.fromSpoolman(spool).copy(
            nativeTagUids = native,
            cardUids = (native + legacy.filter { (it !in nativeOwners || nativeOwners[it] == spool.id) && it !in filamentTags }).distinct()
        )
    }
}
