package br.app.fediaria.core.lembretes

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

enum class TipoLembrete {
    /** Lembrete do terço no horário escolhido. */
    PRINCIPAL,

    /** Segundo aviso do terço, mais tarde, só se ainda não foi rezado. */
    SEGUNDO,

    /** Lembrete diário de uma novena em andamento. */
    NOVENA,
    ;

    companion object {
        val DO_TERCO = listOf(PRINCIPAL, SEGUNDO)
    }
}

/** Um disparo agendado: quando tocar e a qual data local ele se refere. */
data class Disparo(
    val tipo: TipoLembrete,
    val instante: Instant,
    val dataAlvo: LocalDate,
)

object CalculadoraLembretes {
    /**
     * Próxima ocorrência de [horario] (hora local em [fuso]) estritamente depois de [agora].
     * Num horário que "não existe" por causa do horário de verão, o disparo vai para logo depois do salto.
     * Com [aPartirDe] (ex.: novena agendada), nenhum disparo cai antes dessa data.
     */
    fun proximoDisparo(
        tipo: TipoLembrete,
        agora: Instant,
        horario: LocalTime,
        fuso: TimeZone,
        aPartirDe: LocalDate? = null,
    ): Disparo {
        val hojeReal = agora.toLocalDateTime(fuso).date
        if (aPartirDe != null && aPartirDe > hojeReal) {
            return Disparo(tipo, LocalDateTime(aPartirDe, horario).toInstant(fuso), aPartirDe)
        }
        val hoje = hojeReal
        val candidatoHoje = LocalDateTime(hoje, horario).toInstant(fuso)
        if (candidatoHoje > agora) return Disparo(tipo, candidatoHoje, hoje)
        val amanha = hoje.plus(1, DateTimeUnit.DAY)
        return Disparo(tipo, LocalDateTime(amanha, horario).toInstant(fuso), amanha)
    }

    /**
     * Decide, no momento do disparo, se a notificação deve aparecer.
     * Um disparo atrasado que chegue depois da meia-noite (data alvo já passou) é descartado.
     */
    fun deveNotificar(
        dataAlvo: LocalDate,
        hoje: LocalDate,
        rezadoHoje: Boolean,
    ): Boolean = dataAlvo == hoje && !rezadoHoje

    /** Alterna entre variações de texto sem repetir sempre a mesma (determinístico pela data). */
    fun indiceDeVariacao(
        data: LocalDate,
        quantidade: Int,
    ): Int {
        require(quantidade > 0)
        return data.toEpochDays().mod(quantidade)
    }
}
