package br.app.fediaria

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import br.app.fediaria.plataforma.AcoesDoSistema

class AcoesDoSistemaAndroid(
    private val contexto: Context,
) : AcoesDoSistema {
    override val fabricante: String = Build.MANUFACTURER.lowercase()

    override fun abrirConfiguracoesDeNotificacao() {
        abrir(
            Intent(
                Settings.ACTION_APP_NOTIFICATION_SETTINGS,
            ).putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName),
            alternativa = detalhesDoApp(),
        )
    }

    /** Lista de otimização de bateria do sistema (não exige a permissão restrita REQUEST_IGNORE_...). */
    override fun abrirConfiguracoesDeBateria() {
        abrir(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), alternativa = detalhesDoApp())
    }

    override fun abrirNoNavegador(url: String) {
        abrir(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    private fun detalhesDoApp() =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", contexto.packageName, null),
        )

    private fun abrir(
        intent: Intent,
        alternativa: Intent? = null,
    ) {
        try {
            contexto.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: ActivityNotFoundException) {
            alternativa?.let { abrir(it) }
        }
    }
}
