package br.app.fediaria.core.tempo

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class RelogioTest {
    private class RelogioFixo(var instante: Instant) : Clock {
        override fun now(): Instant = instante
    }

    @Test
    fun hojeUsaODiaLocalDoFusoAtual() {
        // 02:30 UTC de 11/10 ainda é 23:30 de 10/10 em São Paulo (UTC-3).
        val clock = RelogioFixo(Instant.parse("2026-10-11T02:30:00Z"))

        assertEquals(LocalDate(2026, 10, 10), Relogio(clock) { TimeZone.of("America/Sao_Paulo") }.hoje())
        assertEquals(LocalDate(2026, 10, 11), Relogio(clock) { TimeZone.UTC }.hoje())
    }

    @Test
    fun mudancaDeFusoEhRefletidaSemRecriarORelogio() {
        val clock = RelogioFixo(Instant.parse("2026-10-11T02:30:00Z"))
        var fuso = TimeZone.of("America/Sao_Paulo")
        val relogio = Relogio(clock) { fuso }

        assertEquals(LocalDate(2026, 10, 10), relogio.hoje())
        fuso = TimeZone.of("Europe/Lisbon")
        assertEquals(LocalDate(2026, 10, 11), relogio.hoje())
    }

    @Test
    fun fimDoHorarioDeVeraoNaoDuplicaNemPulaODia() {
        // Lisboa volta 1h às 02:00 locais de 25/10/2026: 00:30Z e 01:30Z são ambos 25/10.
        val lisboa = TimeZone.of("Europe/Lisbon")
        val clock = RelogioFixo(Instant.parse("2026-10-24T23:30:00Z"))
        val relogio = Relogio(clock) { lisboa }

        assertEquals(LocalDate(2026, 10, 25), relogio.hoje())
        clock.instante = Instant.parse("2026-10-25T01:30:00Z")
        assertEquals(LocalDate(2026, 10, 25), relogio.hoje())
    }
}
