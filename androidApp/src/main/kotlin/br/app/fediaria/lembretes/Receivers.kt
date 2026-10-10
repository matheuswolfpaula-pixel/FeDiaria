package br.app.fediaria.lembretes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.app.fediaria.core.lembretes.TipoLembrete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val escopoDosReceivers = CoroutineScope(SupervisorJob() + Dispatchers.Default)

/** Executa [bloco] fora da thread principal, mantendo o receiver vivo até terminar. */
private fun BroadcastReceiver.emSegundoPlano(bloco: suspend () -> Unit) {
    val pendente = goAsync()
    escopoDosReceivers.launch {
        try {
            bloco()
        } finally {
            pendente.finish()
        }
    }
}

/** Alarme de lembrete tocou. Não exportado: só o próprio app (via AlarmManager) o aciona. */
class LembreteReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val agendador: AgendadorDeLembretes by inject()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val tipo =
            intent.getStringExtra(EXTRA_TIPO)?.let { nome ->
                TipoLembrete.entries.firstOrNull { it.name == nome }
            }
        val dataAlvo = intent.getStringExtra(EXTRA_DATA_ALVO)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        if (tipo == null || dataAlvo == null) return
        if (tipo == TipoLembrete.NOVENA) {
            val id = intent.getLongExtra(EXTRA_NOVENA_ID, -1)
            if (id >= 0) emSegundoPlano { agendador.aoDispararNovena(id, dataAlvo) }
        } else {
            emSegundoPlano { agendador.aoDisparar(tipo, dataAlvo) }
        }
    }

    companion object {
        const val ACAO = "br.app.fediaria.LEMBRETE"
        const val ACAO_NOVENA = "br.app.fediaria.LEMBRETE_NOVENA"
        const val EXTRA_TIPO = "tipo"
        const val EXTRA_DATA_ALVO = "data_alvo"
        const val EXTRA_NOVENA_ID = "novena_id"
    }
}

/**
 * Reinício do aparelho, atualização do app, mudança de hora ou de fuso: o Android descarta ou
 * desalinha os alarmes, então reagendamos tudo. Só recebe broadcasts protegidos do sistema.
 */
class ReagendamentoReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val agendador: AgendadorDeLembretes by inject()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action !in ACOES) return
        emSegundoPlano { agendador.reagendar() }
    }

    private companion object {
        val ACOES =
            setOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED,
            )
    }
}
