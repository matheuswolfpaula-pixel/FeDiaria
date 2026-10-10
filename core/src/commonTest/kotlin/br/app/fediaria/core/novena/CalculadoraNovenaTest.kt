package br.app.fediaria.core.novena

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalculadoraNovenaTest {
    private val inicio = LocalDate(2026, 10, 3)

    private fun LocalDate.mais(dias: Int) = plus(dias, DateTimeUnit.DAY)

    /** Dias 1..n rezados em datas consecutivas a partir do início. */
    private fun rezadosEmSequencia(n: Int) = (1..n).associateWith { inicio.mais(it - 1) }

    @Test
    fun antesDoInicioFicaAgendada() {
        val estado = CalculadoraNovena.calcular(inicio, emptyMap(), hoje = inicio.mais(-2))
        assertEquals(SituacaoNovena.AGENDADA, estado.situacao)
        assertFalse(estado.pendenteHoje)
        assertEquals(inicio.mais(8), estado.previsaoTermino)
    }

    @Test
    fun noPrimeiroDiaEstaPendenteNoDiaUm() {
        val estado = CalculadoraNovena.calcular(inicio, emptyMap(), hoje = inicio)
        assertEquals(SituacaoNovena.EM_ANDAMENTO, estado.situacao)
        assertEquals(1, estado.diaAtual)
        assertTrue(estado.pendenteHoje)
        assertTrue(CalculadoraNovena.podeRezar(1, estado))
        assertFalse(CalculadoraNovena.podeRezar(2, estado))
    }

    @Test
    fun soUmDiaDaNovenaPorData() {
        val estado = CalculadoraNovena.calcular(inicio, mapOf(1 to inicio), hoje = inicio)
        assertEquals(2, estado.diaAtual)
        assertTrue(estado.rezouHoje)
        assertFalse(CalculadoraNovena.podeRezar(2, estado))
        assertEquals(inicio.mais(8), estado.previsaoTermino)
    }

    @Test
    fun diasPerdidosNaoPunemApenasAdiamOTermino() {
        // Rezou dias 1–3, ficou 4 dias sem rezar: continua do dia 4.
        val hoje = inicio.mais(7)
        val estado = CalculadoraNovena.calcular(inicio, rezadosEmSequencia(3), hoje)
        assertEquals(SituacaoNovena.EM_ANDAMENTO, estado.situacao)
        assertEquals(4, estado.diaAtual)
        assertTrue(estado.pendenteHoje)
        assertEquals(hoje.mais(5), estado.previsaoTermino) // dias 4..9 a partir de hoje
    }

    @Test
    fun noveDiasRezadosConcluiENaoLembraMais() {
        val rezados = rezadosEmSequencia(9)
        val estado = CalculadoraNovena.calcular(inicio, rezados, hoje = inicio.mais(10))
        assertEquals(SituacaoNovena.CONCLUIDA, estado.situacao)
        assertNull(estado.diaAtual)
        assertFalse(estado.pendenteHoje)
        assertEquals(inicio.mais(8), estado.previsaoTermino)
    }

    @Test
    fun janelasFixasSugeremADoAnoOuADoProximo() {
        val aparecida = JanelaNovena.Fixa(10, 3)
        assertEquals(LocalDate(2026, 10, 3), aparecida.proximoInicio(LocalDate(2026, 9, 1)))
        // Dentro da janela (até 11/10) ainda sugere a deste ano.
        assertEquals(LocalDate(2026, 10, 3), aparecida.proximoInicio(LocalDate(2026, 10, 11)))
        assertEquals(LocalDate(2027, 10, 3), aparecida.proximoInicio(LocalDate(2026, 10, 12)))
    }

    @Test
    fun espiritoSantoComecaNaSextaAposAAscensaoETerminaNaVesperaDePentecostes() {
        val janela = JanelaNovena.RelativaAPascoa(40)
        val inicio2026 = janela.inicioNoAno(2026) // Páscoa 05/04/2026
        assertEquals(LocalDate(2026, 5, 15), inicio2026)
        assertEquals(DayOfWeek.FRIDAY, inicio2026.dayOfWeek)
        val pentecostes = domingoDePascoa(2026).plus(49, DateTimeUnit.DAY)
        assertEquals(pentecostes, inicio2026.plus(CalculadoraNovena.DIAS, DateTimeUnit.DAY))
    }

    @Test
    fun sagradoCoracaoTerminaNaVesperaDaSolenidade() {
        // Solenidade: sexta-feira, 68 dias após a Páscoa (12/06/2026).
        val inicio = JanelaNovena.RelativaAPascoa(59).inicioNoAno(2026)
        val solenidade = inicio.plus(CalculadoraNovena.DIAS, DateTimeUnit.DAY)
        assertEquals(LocalDate(2026, 6, 12), solenidade)
        assertEquals(DayOfWeek.FRIDAY, solenidade.dayOfWeek)
    }
}
