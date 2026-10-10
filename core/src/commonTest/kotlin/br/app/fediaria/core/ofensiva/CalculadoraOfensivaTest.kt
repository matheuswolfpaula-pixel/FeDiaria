package br.app.fediaria.core.ofensiva

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalculadoraOfensivaTest {
    private val hoje = LocalDate(2026, 10, 10)

    private fun LocalDate.menos(dias: Int) = minus(dias, DateTimeUnit.DAY)

    private fun LocalDate.mais(dias: Int) = plus(dias, DateTimeUnit.DAY)

    /** Dias consecutivos terminando em [fim] (inclusive). */
    private fun sequencia(
        fim: LocalDate,
        dias: Int,
    ) = (0 until dias).map { fim.menos(it) }.toSet()

    private fun calcular(
        dias: Set<LocalDate>,
        em: LocalDate = hoje,
    ) = CalculadoraOfensiva.calcular(dias, em)

    @Test
    fun semHistoricoTudoZerado() {
        val estado = calcular(emptySet())
        assertEquals(EstadoOfensiva.VAZIO, estado)
        assertFalse(estado.emRisco)
    }

    @Test
    fun rezouSoHojeOfensivaUm() {
        val estado = calcular(setOf(hoje))
        assertEquals(1, estado.atual)
        assertEquals(1, estado.recorde)
        assertTrue(estado.rezadoHoje)
        assertFalse(estado.emRisco)
    }

    @Test
    fun hojeAindaNaoRezadoFicaPendenteSemQuebrar() {
        val estado = calcular(sequencia(hoje.menos(1), 5))
        assertEquals(5, estado.atual)
        assertFalse(estado.rezadoHoje)
        assertTrue(estado.emRisco)
    }

    @Test
    fun diaPerdidoSemGracaZeraMasMantemRecorde() {
        // 5 dias seguidos, um buraco anteontem, ontem e hoje rezados.
        val dias = sequencia(hoje.menos(3), 5) + setOf(hoje.menos(1), hoje)
        val estado = calcular(dias)
        assertEquals(2, estado.atual)
        assertEquals(5, estado.recorde)
        assertTrue(estado.diasProtegidos.isEmpty())
    }

    @Test
    fun seteDiasGanhamUmaGraca() {
        val estado = calcular(sequencia(hoje, 7))
        assertEquals(1, estado.gracasDisponiveis)
        assertEquals(7, estado.diasParaProximaGraca)

        val seis = calcular(sequencia(hoje, 6))
        assertEquals(0, seis.gracasDisponiveis)
        assertEquals(1, seis.diasParaProximaGraca)
    }

    @Test
    fun gracaCobreDiaPerdidoEAOfensivaContinua() {
        // 7 dias (ganha graça), perde 1 dia, reza ontem e hoje.
        val perdido = hoje.menos(2)
        val dias = sequencia(perdido.menos(1), 7) + setOf(hoje.menos(1), hoje)
        val estado = calcular(dias)
        assertEquals(9, estado.atual) // dia protegido não soma
        assertEquals(setOf(perdido), estado.diasProtegidos)
        assertEquals(0, estado.gracasDisponiveis)
    }

    @Test
    fun gracasNaoPassamDeDuas() {
        val estado = calcular(sequencia(hoje, 30))
        assertEquals(CalculadoraOfensiva.MAX_GRACAS, estado.gracasDisponiveis)
        assertNull(estado.diasParaProximaGraca)
    }

    @Test
    fun duasGracasCobremDoisDiasSeguidos() {
        val dias = sequencia(hoje.menos(3), 14) + setOf(hoje)
        val estado = calcular(dias)
        assertEquals(15, estado.atual)
        assertEquals(setOf(hoje.menos(1), hoje.menos(2)), estado.diasProtegidos)
        assertEquals(0, estado.gracasDisponiveis)
    }

    @Test
    fun tresDiasPerdidosComDuasGracasQuebra() {
        val dias = sequencia(hoje.menos(4), 14) + setOf(hoje)
        val estado = calcular(dias)
        assertEquals(1, estado.atual)
        assertEquals(14, estado.recorde)
    }

    @Test
    fun gracaNaoEhGastaSemOfensivaEmAndamento() {
        // Ofensiva quebrou; dias seguintes sem terço não "gastam" nada nem geram dias protegidos.
        val dias = sequencia(hoje.menos(10), 3)
        val estado = calcular(dias)
        assertEquals(0, estado.atual)
        assertTrue(estado.diasProtegidos.isEmpty())
    }

    @Test
    fun ontemPerdidoComGracaProtegeEHojePendenteMantem() {
        val dias = sequencia(hoje.menos(2), 7)
        val estado = calcular(dias)
        assertEquals(7, estado.atual)
        assertEquals(setOf(hoje.menos(1)), estado.diasProtegidos)
        assertTrue(estado.emRisco)
    }

    @Test
    fun registroFuturoEhIgnoradoAteADataChegar() {
        // Voltou de viagem para um fuso "atrasado": existe registro de amanhã.
        val dias = setOf(hoje.menos(1), hoje.mais(1))
        val estado = calcular(dias)
        assertEquals(1, estado.atual)
        assertFalse(estado.rezadoHoje)
        assertEquals(1, estado.totalDiasRezados)

        val amanha = calcular(dias, em = hoje.mais(1))
        assertEquals(1, amanha.atual) // hoje (10/10) ficou sem terço e sem graça
    }

    @Test
    fun atravessaViradaDeAnoEAnoBissexto() {
        val fim = LocalDate(2028, 3, 1)
        val estado = calcular(sequencia(fim, 70), em = fim)
        assertEquals(70, estado.atual)
        assertTrue(LocalDate(2028, 2, 29) in sequencia(fim, 70))
        assertTrue(LocalDate(2027, 12, 31) in sequencia(fim, 70))
    }

    @Test
    fun ganhoDeGracaReiniciaAposQuebra() {
        // 6 dias, quebra (sem graça), mais 6 dias até hoje: ainda sem graça.
        val dias = sequencia(hoje.menos(7), 6) + sequencia(hoje, 6)
        val estado = calcular(dias)
        assertEquals(6, estado.atual)
        assertEquals(0, estado.gracasDisponiveis)
        assertEquals(1, estado.diasParaProximaGraca)
    }

    @Test
    fun marcacaoManualSoHojeOuOntem() {
        assertTrue(CalculadoraOfensiva.podeMarcarManualmente(hoje, hoje))
        assertTrue(CalculadoraOfensiva.podeMarcarManualmente(hoje.menos(1), hoje))
        assertFalse(CalculadoraOfensiva.podeMarcarManualmente(hoje.menos(2), hoje))
        assertFalse(CalculadoraOfensiva.podeMarcarManualmente(hoje.mais(1), hoje))
    }

    @Test
    fun conquistasDerivadasDoEstado() {
        val estado = calcular(sequencia(hoje, 8))
        val alcancadas = Conquista.entries.filter { it.alcancada(estado) }
        assertEquals(listOf(Conquista.PRIMEIRO_TERCO, Conquista.UMA_SEMANA), alcancadas)
    }
}
