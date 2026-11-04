package com.spoolstudio.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.spoolstudio.app.data.remote.spoolman.SpoolmanService
import com.spoolstudio.app.domain.models.FilamentSpool
import com.spoolstudio.app.ui.theme.SpoolStudioColors
import com.spoolstudio.app.ui.theme.SpoolStudioShape
import com.spoolstudio.app.data.local.TagStorageSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun SpoolTagsPanel(url: String, spool: FilamentSpool, onRefresh: () -> Unit) {
    var expanded by remember(spool.id) { mutableStateOf(false) }
    var pending by remember(spool.id) { mutableStateOf<String?>(null) }
    var busy by remember(spool.id) { mutableStateOf(false) }
    var error by remember(spool.id) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val mode = TagStorageSettings.mode(url)
    val buttonColors = ButtonDefaults.textButtonColors(
        contentColor = SpoolStudioColors.AccentCyan,
        disabledContentColor = SpoolStudioColors.OnGraphiteMuted.copy(alpha = 0.55f)
    )
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SecondaryActionButton(
            text = if (expanded) "Hide tag links (${spool.cardUids.size})" else "Manage tag links (${spool.cardUids.size})",
            onClick = { expanded = !expanded }
        )
        if (expanded) {
            Text("Storage: ${mode.label}", color = SpoolStudioColors.OnGraphiteMuted, style = MaterialTheme.typography.bodySmall)
            if (spool.cardUids.isEmpty()) {
                Text("No tags linked in the selected storage.", color = SpoolStudioColors.OnGraphiteMuted)
            }
            spool.cardUids.forEach { uid ->
                Card(colors = CardDefaults.cardColors(containerColor = SpoolStudioColors.GraphiteRaised, contentColor = SpoolStudioColors.OnGraphite),
                    shape = SpoolStudioShape.Field, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(uid)
                            Text(if (uid in spool.nativeTagUids) "Native tag" else "card_uids", style = MaterialTheme.typography.bodySmall, color = SpoolStudioColors.OnGraphiteMuted)
                        }
                        OutlinedButton(enabled = !busy, onClick = { pending = uid },
                            modifier = Modifier.heightIn(min = 48.dp), shape = SpoolStudioShape.Button,
                            border = BorderStroke(1.dp, SpoolStudioColors.GoldSoft),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SpoolStudioColors.GoldSoft)
                        ) { Text("Unlink") }
                    }
                }
            }
            error?.let { Text(it, color = SpoolStudioColors.GoldSoft) }
        }
    }
    pending?.let { uid ->
        AlertDialog(onDismissRequest = { if (!busy) pending = null }, title = { Text("Unlink tag?") },
            containerColor = SpoolStudioColors.Graphite,
            titleContentColor = SpoolStudioColors.OnGraphite,
            textContentColor = SpoolStudioColors.OnGraphiteMuted,
            text = { Text("Remove $uid from spool #${spool.id} using ${mode.label}. Other storage is untouched in single-store modes. This does not clear the printer's loaded-spool assignment. The physical tag is unchanged.") },
            confirmButton = { TextButton(colors = buttonColors, enabled = !busy, onClick = {
                scope.launch {
                    busy = true
                    try {
                        SpoolmanService(url).unlinkCardUid(spool.id ?: error("Missing spool ID"), uid)
                        error = null
                    } catch (e: CancellationException) { throw e
                    } catch (e: Exception) { error = "Unlink incomplete: ${e.message}. Refresh and check remaining links." }
                    finally { busy = false; pending = null; onRefresh() }
                }
            }) { Text(if (busy) "Unlinking…" else "Unlink") } },
            dismissButton = { TextButton(colors = buttonColors, enabled = !busy, onClick = { pending = null }) { Text("Cancel") } })
    }
}
