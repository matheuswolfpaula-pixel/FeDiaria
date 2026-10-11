package br.app.fediaria.core.quiz

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MontadorDeLicoesTest {
    private val hoje = LocalDate(2026, 10, 10)

    private fun ids(n: Int) = (1..n).map { "q$it" }

    @Test
    fun divideEmLicoesEquilibradasDeCincoADez() {
        assertEquals(listOf(6, 6, 5), MontadorDeLicoes.dividir(ids(17)).map { it.size })
        assertEquals(listOf(8), MontadorDeLicoes.dividir(ids(8)).map { it.size })
        assertEquals(listOf(9), MontadorDeLicoes.dividir(ids(9)).map { it.size })
        assertEquals(listOf(6, 5), MontadorDeLicoes.dividir(ids(11)).map { it.size })
        for (n in MontadorDeLicoes.MINIMO..200) {
            val partes = MontadorDeLicoes.dividir(ids(n))
            assertEquals(ids(n), partes.flatten(), "n=$n perde ou reordena perguntas")
            assertTrue(
                partes.all { it.size in MontadorDeLicoes.MINIMO..MontadorDeLicoes.MAXIMO },
                "n=$n: ${partes.map { it.size }}",
            )
        }
    }

    @Test
    fun poucasPerguntasViramUmaLicaoSo() {
        assertEquals(listOf(3), MontadorDeLicoes.dividir(ids(3)).map { it.size })
        assertEquals(emptyList(), MontadorDeLicoes.dividir(emptyList()))
    }

    @Test
    fun licoesTemIdsEstaveisPorTrilhaENivel() {
        val perguntas =
            (1..17).map { ChavePergunta("e1.$it", "escrituras", 1) } +
                (1..8).map { ChavePergunta("e2.$it", "escrituras", 2) } +
                (1..6).map { ChavePergunta("c1.$it", "catecismo", 1) }
        val licoes = MontadorDeLicoes.licoes(perguntas.shuffled())
        assertEquals(
            listOf("catecismo.n1.l1", "escrituras.n1.l1", "escrituras.n1.l2", "escrituras.n1.l3", "escrituras.n2.l1"),
            licoes.map { it.id },
        )
        assertEquals(
            perguntas.filter { it.trilha == "escrituras" && it.nivel == 1 }.map { it.id }.toSet(),
            licoes.filter { it.trilha == "escrituras" && it.nivel == 1 }.flatMap { it.perguntas }.toSet(),
        )
    }

    @Test
    fun revisaoPegaSoVencidasDasMaisAtrasadasEDeCaixaMaisBaixa() {
        val estados =
            mapOf(
                "a" to EstadoSrs(3, LocalDate(2026, 10, 9), 2, 0),
                "b" to EstadoSrs(1, LocalDate(2026, 10, 10), 0, 1),
                "c" to EstadoSrs(1, LocalDate(2026, 10, 9), 0, 2),
                "d" to EstadoSrs(2, LocalDate(2026, 10, 11), 1, 0), // ainda não venceu
                "removida" to EstadoSrs(1, LocalDate(2026, 10, 1), 0, 1), // saiu do conteúdo
            )
        assertEquals(listOf("c", "a", "b"), MontadorDeLicoes.revisao(estados, hoje, setOf("a", "b", "c", "d")))
    }

    @Test
    fun revisaoLimitadaAoMaximo() {
        val estados = (1..25).associate { "q$it" to EstadoSrs(1, hoje, 0, 1) }
        assertEquals(MontadorDeLicoes.MAXIMO_REVISAO, MontadorDeLicoes.revisao(estados, hoje, estados.keys).size)
        assertEquals(emptyList(), MontadorDeLicoes.revisao(emptyMap(), hoje, emptySet()))
    }

    @Test
    fun nivelSeguinteAbreQuandoTodasAsLicoesDoAnteriorForamFeitas() {
        val licoes = MontadorDeLicoes.licoes((1..17).map { ChavePergunta("q$it", "fe", 1) })
        assertTrue(MontadorDeLicoes.nivelAberto("fe", 1, licoes, emptySet()))
        assertFalse(MontadorDeLicoes.nivelAberto("fe", 2, licoes, setOf("fe.n1.l1", "fe.n1.l2")))
        assertTrue(MontadorDeLicoes.nivelAberto("fe", 2, licoes, setOf("fe.n1.l1", "fe.n1.l2", "fe.n1.l3")))
    }

    @Test
    fun xpDezPorAcerto() {
        assertEquals(0, Pontuacao.xp(0))
        assertEquals(70, Pontuacao.xp(7))
    }
}
