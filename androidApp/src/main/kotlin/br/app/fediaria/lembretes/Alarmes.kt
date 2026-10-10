package br.app.fediaria.lembretes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import br.app.fediaria.core.lembretes.Disparo
import br.app.fediaria.core.lembretes.TipoLembrete
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.minutes

/**
 * Camada fina sobre o AlarmManager: um alarme por lembrete do terço e um por novena, sempre em
 * JANELA (setWindow), que não exige a permissão de alarme exato.
 */
class Alarmes(
    private val contexto: Context,
) {
    private val gerenciador = contexto.getSystemService(AlarmManager::class.java)

    fun agendarTerco(disparo: Disparo) =
        agendar(
            disparo,
            codigoDoTerco(disparo.tipo),
            intentDoTerco(disparo.tipo, disparo.dataAlvo),
        )

    fun agendarNovena(
        id: Long,
        disparo: Disparo,
    ) = agendar(disparo, codigoDaNovena(id), intentDaNovena(id, disparo.dataAlvo))

    fun cancelarTerco(tipo: TipoLembrete) = cancelar(codigoDoTerco(tipo), intentDoTerco(tipo, null))

    fun cancelarNovena(id: Long) = cancelar(codigoDaNovena(id), intentDaNovena(id, null))

    fun tercoAgendado(tipo: TipoLembrete): Boolean = pendente(codigoDoTerco(tipo), intentDoTerco(tipo, null)) != null

    fun novenaAgendada(id: Long): Boolean = pendente(codigoDaNovena(id), intentDaNovena(id, null)) != null

    private fun agendar(
        disparo: Disparo,
        codigo: Int,
        intent: Intent,
    ) {
        val pendente =
            PendingIntent.getBroadcast(
                contexto,
                codigo,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
        gerenciador.setWindow(
            AlarmManager.RTC_WAKEUP,
            disparo.instante.toEpochMilliseconds(),
            JANELA.inWholeMilliseconds,
            pendente,
        )
    }

    private fun cancelar(
        codigo: Int,
        intent: Intent,
    ) {
        pendente(codigo, intent)?.let {
            gerenciador.cancel(it)
            it.cancel()
        }
    }

    private fun pendente(
        codigo: Int,
        intent: Intent,
    ): PendingIntent? =
        PendingIntent.getBroadcast(
            contexto,
            codigo,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE,
        )

    private fun intentDoTerco(
        tipo: TipoLembrete,
        dataAlvo: LocalDate?,
    ) = Intent(contexto, LembreteReceiver::class.java)
        .setAction(LembreteReceiver.ACAO)
        .putExtra(LembreteReceiver.EXTRA_TIPO, tipo.name)
        .apply { dataAlvo?.let { putExtra(LembreteReceiver.EXTRA_DATA_ALVO, it.toString()) } }

    private fun intentDaNovena(
        id: Long,
        dataAlvo: LocalDate?,
    ) = Intent(contexto, LembreteReceiver::class.java)
        .setAction(LembreteReceiver.ACAO_NOVENA)
        .putExtra(LembreteReceiver.EXTRA_TIPO, TipoLembrete.NOVENA.name)
        .putExtra(LembreteReceiver.EXTRA_NOVENA_ID, id)
        .apply { dataAlvo?.let { putExtra(LembreteReceiver.EXTRA_DATA_ALVO, it.toString()) } }

    private fun codigoDoTerco(tipo: TipoLembrete) = tipo.ordinal

    private fun codigoDaNovena(id: Long) = BASE_CODIGO_NOVENA + id.toInt()

    companion object {
        /** Janela mínima aceita pelo Android 12+ para alarmes não exatos. */
        val JANELA = 10.minutes

        /** Códigos de PendingIntent das novenas: 1000 + id (os do terço são 0 e 1). */
        private const val BASE_CODIGO_NOVENA = 1000
    }
}
