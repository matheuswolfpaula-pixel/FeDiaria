package br.app.fediaria.lembretes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import br.app.fediaria.core.lembretes.CalculadoraLembretes
import br.app.fediaria.core.lembretes.Disparo
import br.app.fediaria.core.lembretes.TipoLembrete
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.minutes

/**
 * Agenda os lembretes com AlarmManager em JANELA (setWindow), sem alarme exato:
 * não exige SCHEDULE_EXACT_ALARM. O aviso chega dentro de [JANELA] após o horário; com o aparelho em
 * repouso profundo (Doze), chega quando ele "acorda" (por exemplo, ao ser pego). O disparo apenas
 * DECIDE no momento se notifica (só se o terço de hoje ainda não foi rezado) e agenda o próximo.
 */
class AgendadorDeLembretes(
    private val contexto: Context,
    private val configuracoes: ConfiguracoesRepository,
    private val dias: DiasRezadosRepository,
    private val notificacoes: Notificacoes,
    private val relogio: Relogio,
) : Lembretes {
    private val alarmes = contexto.getSystemService(AlarmManager::class.java)

    override suspend fun reagendar() {
        val config = configuracoes.lembretesAtuais()
        TipoLembrete.entries.forEach { tipo ->
            if (ativo(tipo, config)) {
                val disparo =
                    CalculadoraLembretes.proximoDisparo(
                        tipo = tipo,
                        agora = relogio.agora(),
                        horario = if (tipo == TipoLembrete.PRINCIPAL) config.horario else config.horarioSegundo,
                        fuso = TimeZone.currentSystemDefault(),
                    )
                agendar(disparo)
            } else {
                cancelar(tipo)
            }
        }
    }

    /** Chamado pelo [LembreteReceiver] quando um alarme toca. */
    suspend fun aoDisparar(
        tipo: TipoLembrete,
        dataAlvo: LocalDate,
    ) {
        val config = configuracoes.lembretesAtuais()
        if (ativo(tipo, config)) {
            val ofensiva = dias.estadoAtual()
            val hoje = relogio.hoje()
            if (CalculadoraLembretes.deveNotificar(dataAlvo, hoje, ofensiva.rezadoHoje)) {
                notificacoes.mostrarLembrete(tipo, ofensiva, hoje)
            }
        }
        reagendar()
    }

    override fun aoRegistrarOracao() = notificacoes.recolherLembrete()

    override fun notificacoesPermitidas(): Boolean = notificacoes.permitidas()

    /** Há alarme pendente deste tipo? (usado em testes e diagnóstico) */
    fun agendado(tipo: TipoLembrete): Boolean = intentPendente(tipo, PendingIntent.FLAG_NO_CREATE) != null

    private fun ativo(
        tipo: TipoLembrete,
        config: ConfigLembretes,
    ) = config.ativo && (tipo == TipoLembrete.PRINCIPAL || config.segundoAtivo)

    private fun agendar(disparo: Disparo) {
        val intent =
            Intent(contexto, LembreteReceiver::class.java)
                .setAction(LembreteReceiver.ACAO)
                .putExtra(LembreteReceiver.EXTRA_TIPO, disparo.tipo.name)
                .putExtra(LembreteReceiver.EXTRA_DATA_ALVO, disparo.dataAlvo.toString())
        val pendente =
            PendingIntent.getBroadcast(
                contexto,
                disparo.tipo.ordinal,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        alarmes.setWindow(
            AlarmManager.RTC_WAKEUP,
            disparo.instante.toEpochMilliseconds(),
            JANELA.inWholeMilliseconds,
            pendente,
        )
    }

    private fun cancelar(tipo: TipoLembrete) {
        intentPendente(tipo, PendingIntent.FLAG_NO_CREATE)?.let {
            alarmes.cancel(it)
            it.cancel()
        }
    }

    private fun intentPendente(
        tipo: TipoLembrete,
        flags: Int,
    ): PendingIntent? =
        PendingIntent.getBroadcast(
            contexto,
            tipo.ordinal,
            Intent(contexto, LembreteReceiver::class.java).setAction(LembreteReceiver.ACAO),
            PendingIntent.FLAG_IMMUTABLE or flags,
        )

    companion object {
        /** Janela mínima aceita pelo Android 12+ para alarmes não exatos. */
        val JANELA = 10.minutes
    }
}
