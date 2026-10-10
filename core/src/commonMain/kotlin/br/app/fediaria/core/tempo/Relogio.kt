package br.app.fediaria.core.tempo

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Fonte única de "agora" e "hoje" do app. Toda regra que depende de data recebe um [Relogio]
 * em vez de ler o relógio do sistema diretamente, para ser testável.
 *
 * "Hoje" é sempre a data LOCAL no fuso atual do aparelho (consultado a cada chamada,
 * então uma viagem ou mudança de fuso é refletida imediatamente).
 */
class Relogio(
    private val clock: Clock = Clock.System,
    private val fuso: () -> TimeZone = { TimeZone.currentSystemDefault() },
) {
    fun agora(): Instant = clock.now()

    fun hoje(): LocalDate = dataLocal(agora())

    fun dataLocal(instante: Instant): LocalDate = instante.toLocalDateTime(fuso()).date
}
