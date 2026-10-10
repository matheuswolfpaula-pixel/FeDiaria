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
import br.app.fediaria.shared.resources.canal_novenas_desc
import br.app.fediaria.shared.resources.canal_novenas_nome
import br.app.fediaria.shared.resources.notif_chama_texto
import br.app.fediaria.shared.resources.notif_chama_titulo
import br.app.fediaria.shared.resources.notif_novena_texto
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

/** Canais e exibição das notificações de lembrete. Tom sempre acolhedor, nunca culpabilizante. */
class Notificacoes(
    private val contexto: Context,
) {
    private val gerenciador = NotificationManagerCompat.from(contexto)

    suspend fun criarCanais() {
        gerenciador.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CANAL_LEMBRETES,
                    getString(Res.string.canal_lembretes_nome),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = getString(Res.string.canal_lembretes_desc) },
                NotificationChannel(
                    CANAL_NOVENAS,
                    getString(Res.string.canal_novenas_nome),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = getString(Res.string.canal_novenas_desc) },
            ),
        )
    }

    /** Permissão concedida, app com notificações ligadas e [canal] não silenciado pelo usuário. */
    fun permitidas(canal: String = CANAL_LEMBRETES): Boolean {
        if (!gerenciador.areNotificationsEnabled()) return false
        val registrado = gerenciador.getNotificationChannel(canal) ?: return true
        return registrado.importance != NotificationManager.IMPORTANCE_NONE
    }

    suspend fun mostrarLembrete(
        tipo: TipoLembrete,
        ofensiva: EstadoOfensiva,
        hoje: LocalDate,
    ) {
        if (!permitidas()) return
        criarCanais()
        val (titulo, texto) = textos(tipo, ofensiva, hoje)
        notificar(ID_LEMBRETE, CANAL_LEMBRETES, titulo, texto, abrirApp(novenaId = null))
    }

    suspend fun mostrarLembreteNovena(
        id: Long,
        nome: String,
        dia: Int,
    ) {
        if (!permitidas(CANAL_NOVENAS)) return
        criarCanais()
        notificar(
            idNovena(id),
            CANAL_NOVENAS,
            nome,
            getString(Res.string.notif_novena_texto, dia),
            abrirApp(novenaId = id),
        )
    }

    fun recolherLembrete() = gerenciador.cancel(ID_LEMBRETE)

    fun recolherLembreteNovena(id: Long) = gerenciador.cancel(idNovena(id))

    @Suppress("MissingPermission") // Quem chama verifica permitidas() antes.
    private fun notificar(
        id: Int,
        canal: String,
        titulo: String,
        texto: String,
        aoTocar: PendingIntent,
    ) {
        val notificacao =
            NotificationCompat
                .Builder(contexto, canal)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setColor(contexto.getColor(R.color.azul_mariano))
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
                .setContentIntent(aoTocar)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        gerenciador.notify(id, notificacao)
    }

    /** Abre o app; com [novenaId], direto na tela daquela novena. */
    private fun abrirApp(novenaId: Long?): PendingIntent {
        val intent =
            Intent(contexto, MainActivity::class.java)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
                )
        if (novenaId != null) intent.putExtra(MainActivity.EXTRA_NOVENA_ID, novenaId)
        return PendingIntent.getActivity(
            contexto,
            novenaId?.let { idNovena(it) } ?: 0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

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
        const val CANAL_NOVENAS = "lembretes_novenas"
        private const val ID_LEMBRETE = 1001
        private const val BASE_ID_NOVENA = 2000

        private fun idNovena(id: Long) = BASE_ID_NOVENA + id.toInt()

        private val VARIACOES: List<Pair<StringResource, StringResource>> =
            listOf(
                Res.string.notif_principal_titulo_1 to Res.string.notif_principal_texto_1,
                Res.string.notif_principal_titulo_2 to Res.string.notif_principal_texto_2,
                Res.string.notif_principal_titulo_3 to Res.string.notif_principal_texto_3,
            )
    }
}
