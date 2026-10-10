package br.app.fediaria.data.config

import br.app.fediaria.core.terco.ConjuntoMisterios
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class ProgressoTercoTest {
    private val inicio = Instant.parse("2026-10-10T23:50:00Z")
    private val progresso = ProgressoTerco(ConjuntoMisterios.GOZOSOS, indicePasso = 40, iniciadoEm = inicio)

    @Test
    fun retomaDentroDaValidadeMesmoDepoisDaMeiaNoite() {
        assertTrue(progresso.valido(inicio + 30.minutes))
        assertTrue(progresso.valido(inicio + 6.hours))
    }

    @Test
    fun descartaProgressoAntigoOuComRelogioVoltado() {
        assertFalse(progresso.valido(inicio + 6.hours + 1.minutes))
        assertFalse(progresso.valido(inicio - 1.minutes))
    }
}
