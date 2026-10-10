package br.app.fediaria.lembretes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.app.fediaria.MainActivity
import br.app.fediaria.R
import br.app.fediaria.core.lembretes.CalculadoraLembretes
import br.app.fediaria.core.lembretes.TipoLembrete
import br.app.fediaria.core.ofensiva.EstadoOfensiva
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.canal_lembretes_desc
import br.app.fediaria.shared.resources.canal_lembretes_nome
import br.app.fediaria.shared.resources.notif_chama_texto
import br.app.fediaria.shared.resources.notif_chama_titulo
import br.app.fediaria.shared.resources.notif_principal_texto_1
import br.app.fediaria.shared.resources.notif_principal_texto_2
import br.app.fediaria.shared.resources.notif_principal_texto_3
import br.app.fediaria.shared.resources.notif_principal_titulo_1
import br.app.fediaria.shared.resources.notif_principal_titulo_2
import br.app.fediaria.shared.resources.notif_principal_titulo_3
import br.app.fediaria.shared.resources.notif_risco_texto
import br.app.fediaria.shared.resources.notif_risco_titulo
import br.app.fediaria.shared.resources.notif_segundo_texto
import br.app.fediaria.shared.resources.notif_segundo_titulo
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

/** Canal e exibição das notificações de lembrete. Tom sempre acolhedor, nunca culpabilizante. */
class Notificacoes(
    private val contexto: Context,
) {
    private val gerenciador = NotificationManagerCompat.from(contexto)

    suspend fun criarCanal() {
        val canal =
            NotificationChannel(
                CANAL_LEMBRETES,
                getString(Res.string.canal_lembretes_nome),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = getString(Res.string.canal_lembretes_desc) }
        gerenciador.createNotificationChannel(canal)
    }

    /** Permissão concedida, app com notificações ligadas e canal não silenciado pelo usuário. */
    fun permitidas(): Boolean {
        if (!gerenciador.areNotificationsEnabled()) return false
        val canal = gerenciador.getNotificationChannel(CANAL_LEMBRETES) ?: return true
        return canal.importance != NotificationManager.IMPORTANCE_NONE
    }

    @Suppress("MissingPermission") // permitidas() verifica a permissão antes de notificar.
    suspend fun mostrarLembrete(
        tipo: TipoLembrete,
        ofensiva: EstadoOfensiva,
        hoje: LocalDate,
    ) {
        if (!permitidas()) return
        criarCanal()
        val (titulo, texto) = textos(tipo, ofensiva, hoje)
        val abrirApp =
            PendingIntent.getActivity(
                contexto,
                0,
                Intent(
                    contexto,
                    MainActivity::class.java,
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        val notificacao =
            NotificationCompat
                .Builder(contexto, CANAL_LEMBRETES)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setColor(contexto.getColor(R.color.azul_mariano))
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
                .setContentIntent(abrirApp)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        gerenciador.notify(ID_LEMBRETE, notificacao)
    }

    fun recolherLembrete() = gerenciador.cancel(ID_LEMBRETE)

    private suspend fun textos(
        tipo: TipoLembrete,
        ofensiva: EstadoOfensiva,
        hoje: LocalDate,
    ): Pair<String, String> =
        when {
            tipo == TipoLembrete.SEGUNDO && ofensiva.emRisco -> {
                getString(Res.string.notif_risco_titulo) to getString(Res.string.notif_risco_texto)
            }

            tipo == TipoLembrete.SEGUNDO -> {
                getString(Res.string.notif_segundo_titulo) to getString(Res.string.notif_segundo_texto)
            }

            ofensiva.atual > 0 -> {
                getPluralString(Res.plurals.notif_chama_titulo, ofensiva.atual, ofensiva.atual) to
                    getString(Res.string.notif_chama_texto)
            }

            else -> {
                val (titulo, texto) = VARIACOES[CalculadoraLembretes.indiceDeVariacao(hoje, VARIACOES.size)]
                getString(titulo) to getString(texto)
            }
        }

    companion object {
        const val CANAL_LEMBRETES = "lembretes_terco"
        private const val ID_LEMBRETE = 1001

        private val VARIACOES: List<Pair<StringResource, StringResource>> =
            listOf(
                Res.string.notif_principal_titulo_1 to Res.string.notif_principal_texto_1,
                Res.string.notif_principal_titulo_2 to Res.string.notif_principal_texto_2,
                Res.string.notif_principal_titulo_3 to Res.string.notif_principal_texto_3,
            )
    }
}
