package vn.lichviet.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import vn.lichviet.data.Updater

/** Hộp thoại có bản mới: tải về rồi mở trình cài đặt. */
@Composable
fun UpdateDialog(r: Updater.Release, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var prog by remember { mutableStateOf<Float?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    val cur = remember { Updater.current(ctx).second }
    AlertDialog(
        onDismissRequest = { if (prog == null) onDismiss() },
        icon = { Icon(Icons.Filled.SystemUpdate, null) },
        title = { Text("Có bản mới ${r.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Bạn đang dùng bản $cur.")
                if (r.notes.isNotBlank()) Text(r.notes, style = MaterialTheme.typography.bodySmall)
                prog?.let { LinearProgressIndicator(progress = { it }, modifier = Modifier.fillMaxWidth()); Text("Đang tải ${(it * 100).toInt()}%") }
                err?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = prog == null, onClick = {
                if (!Updater.canInstall(ctx)) {
                    Toast.makeText(ctx, "Hãy cho phép Lịch Việt cài ứng dụng, rồi bấm Cập nhật lại", Toast.LENGTH_LONG).show()
                    Updater.openInstallPermission(ctx); return@TextButton
                }
                err = null; prog = 0f
                scope.launch {
                    runCatching { Updater.download(ctx, r) { prog = it } }
                        .onSuccess { prog = null; Updater.install(ctx, it); onDismiss() }
                        .onFailure { prog = null; err = "Tải thất bại, kiểm tra mạng rồi thử lại." }
                }
            }) { Text("Cập nhật") }
        },
        dismissButton = { TextButton(enabled = prog == null, onClick = onDismiss) { Text("Để sau") } },
    )
}

/** Tự kiểm tra 1 lần/ngày khi mở app. */
@Composable
fun AutoUpdateCheck() {
    val ctx = LocalContext.current
    var rel by remember { mutableStateOf<Updater.Release?>(null) }
    LaunchedEffect(Unit) { if (Updater.due(ctx)) rel = runCatching { Updater.check(ctx) }.getOrNull() }
    rel?.let { UpdateDialog(it) { rel = null } }
}

@Composable
internal fun UpdateSection() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val (_, name) = remember { Updater.current(ctx) }
    var checking by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf<String?>(null) }
    var rel by remember { mutableStateOf<Updater.Release?>(null) }
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Phiên bản $name", fontWeight = FontWeight.SemiBold)
                Text(msg ?: "Tự kiểm tra bản mới mỗi ngày", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(enabled = !checking, onClick = {
                checking = true; msg = "Đang kiểm tra…"
                scope.launch {
                    runCatching { Updater.check(ctx) }
                        .onSuccess { rel = it; msg = if (it == null) "Bạn đang dùng bản mới nhất" else "Có bản ${it.name}" }
                        .onFailure { msg = "Không kết nối được, thử lại sau" }
                    checking = false
                }
            }) { Text("Kiểm tra") }
        }
    }
    rel?.let { UpdateDialog(it) { rel = null } }
}
