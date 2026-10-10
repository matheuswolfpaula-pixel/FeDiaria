package br.app.fediaria.plataforma

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

@Composable
actual fun lembrarPedidoDePermissaoDeNotificacao(aoResponder: (Boolean) -> Unit): () -> Unit {
    val contexto = LocalContext.current
    val callback = rememberUpdatedState(aoResponder)
    val lancador =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedida ->
            callback.value(concedida && NotificationManagerCompat.from(contexto).areNotificationsEnabled())
        }
    return {
        val precisaPedir =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
        if (precisaPedir) {
            lancador.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            callback.value(NotificationManagerCompat.from(contexto).areNotificationsEnabled())
        }
    }
}
