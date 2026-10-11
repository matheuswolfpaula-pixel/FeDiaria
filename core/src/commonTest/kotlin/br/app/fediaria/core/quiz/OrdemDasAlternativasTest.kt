package br.app.fediaria.core.quiz

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrdemDasAlternativasTest {
    private val hoje = LocalDate(2026, 10, 10)

    @Test
    fun ehUmaPermutacaoEstavelParaAMesmaSemente() {
        val s = OrdemDasAlternativas.semente("quiz.escrituras.n1.001", hoje)
        val ordem = OrdemDasAlternativas.ordem(4, s)
        assertEquals(listOf(0, 1, 2, 3), ordem.sorted())
        assertEquals(ordem, OrdemDasAlternativas.ordem(4, s))
    }

    @Test
    fun verdadeiroOuFalsoNaoEhEmbaralhado() {
        assertEquals(listOf(0, 1), OrdemDasAlternativas.ordem(2, 12345, embaralhar = false))
    }

    @Test
    fun aRespostaCertaNaoFicaPresaNumaPosicao() {
        // Em 200 perguntas cuja correta é sempre a original "1", ela aparece em todas as posições.
        val posicoes =
            (1..200).map { i ->
                OrdemDasAlternativas.ordem(4, OrdemDasAlternativas.semente("q$i", hoje)).indexOf(1)
            }
        assertEquals(setOf(0, 1, 2, 3), posicoes.toSet())
        assertTrue(
            posicoes
                .groupingBy { it }
                .eachCount()
                .values
                .all { it in 25..75 },
            "$posicoes",
        )
    }

    @Test
    fun ordemMudaDeUmDiaParaOOutroEmAlgumasPerguntas() {
        val mudaram =
            (1..50).count { i ->
                OrdemDasAlternativas.ordem(4, OrdemDasAlternativas.semente("q$i", hoje)) !=
                    OrdemDasAlternativas.ordem(4, OrdemDasAlternativas.semente("q$i", LocalDate(2026, 10, 11)))
            }
        assertTrue(mudaram > 25, "só $mudaram mudaram")
    }
}
