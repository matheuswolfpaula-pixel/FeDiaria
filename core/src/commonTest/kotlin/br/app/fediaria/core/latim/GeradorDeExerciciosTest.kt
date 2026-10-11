package br.app.fediaria.core.latim

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeradorDeExerciciosTest {
    private val paterNoster =
        listOf(
            "Pater noster, qui es in caelis," to "Pai nosso, que estais nos céus,",
            "sanctificetur nomen tuum." to "santificado seja o vosso nome.",
            "Adveniat regnum tuum." to "Venha o vosso reino.",
            "Fiat voluntas tua," to "Seja feita a vossa vontade,",
            "sicut in caelo et in terra." to "assim no céu como na terra.",
            "Amen." to "Amém.",
        ).mapIndexed { i, (la, pt) -> FraseLatina("pn.$i", la, pt) }

    private val pool = paterNoster.flatMap { GeradorDeExercicios.palavras(it.latim) }

    @Test
    fun palavrasIgnoramPontuacaoSoltaELimpaTiraPontuacao() {
        assertEquals(
            listOf("Pater", "noster,", "qui", "es", "in", "caelis,"),
            GeradorDeExercicios.palavras(paterNoster[0].latim),
        )
        assertEquals("caelis", GeradorDeExercicios.limpa("caelis,"))
    }

    @Test
    fun lacunaTemQuatroOpcoesDistintasComACertaUmaVez() {
        repeat(50) { semente ->
            val e = assertNotNull(GeradorDeExercicios.lacuna(paterNoster[0], pool, Random(semente)))
            assertEquals(GeradorDeExercicios.OPCOES, e.opcoes.size)
            assertEquals(
                e.opcoes.size,
                e.opcoes
                    .map { it.lowercase() }
                    .toSet()
                    .size,
                "${e.opcoes}",
            )
            assertEquals(1, e.opcoes.count { it == e.correta })
            assertEquals(GeradorDeExercicios.limpa(e.palavras[e.indiceLacuna]), e.correta)
            assertTrue(e.correta.length >= 3, "lacuna em palavra curta: ${e.correta}")
            assertTrue(e.opcoes.none { it.contains(',') || it.contains('.') })
        }
    }

    @Test
    fun lacunaPrecisaDeDuasPalavrasEDeDistratoresSuficientes() {
        assertNull(GeradorDeExercicios.lacuna(paterNoster[5], pool, Random(1)), "frase de uma palavra")
        assertNull(GeradorDeExercicios.lacuna(paterNoster[0], listOf("Pater", "qui"), Random(1)), "sem distratores")
    }

    @Test
    fun ordenarEhUmaPermutacaoDiferenteDaOrdemCerta() {
        repeat(50) { semente ->
            val e = assertNotNull(GeradorDeExercicios.ordenar(paterNoster[4], Random(semente)))
            assertEquals(listOf("sicut", "in", "caelo", "et", "in", "terra"), e.ordemCerta)
            assertEquals(e.ordemCerta.sorted(), e.embaralhadas.sorted())
            assertNotEquals(e.ordemCerta, e.embaralhadas)
            assertTrue(e.confere(e.ordemCerta))
            assertFalse(e.confere(e.embaralhadas))
        }
    }

    @Test
    fun ordenarIgnoraFrasesMuitoCurtasOuLongas() {
        assertNull(GeradorDeExercicios.ordenar(FraseLatina("x", "et Filii,", "e do Filho,"), Random(1)))
        assertNull(GeradorDeExercicios.ordenar(FraseLatina("x", "a b c d e f g h i", "…"), Random(1)))
    }

    @Test
    fun associarUsaQuatroFrasesETraducoesForaDeOrdem() {
        repeat(50) { semente ->
            val e = assertNotNull(GeradorDeExercicios.associar(paterNoster, Random(semente)))
            assertEquals(GeradorDeExercicios.PARES, e.pares.size)
            assertEquals(e.pares.indices.toList(), e.ordemTraducoes.sorted())
            assertNotEquals(e.pares.indices.toList(), e.ordemTraducoes)
        }
        assertNull(GeradorDeExercicios.associar(paterNoster.take(3), Random(1)))
    }

    @Test
    fun licaoTemSeisExerciciosVariadosSemRepetirFrase() {
        val licao = GeradorDeExercicios.licao(paterNoster, Random(7))
        assertEquals(GeradorDeExercicios.TAMANHO_LICAO, licao.size)
        assertEquals(1, licao.count { it is Exercicio.Associar })
        assertTrue(licao.any { it is Exercicio.Lacuna } && licao.any { it is Exercicio.Ordenar })
        val individuais = licao.filter { it !is Exercicio.Associar }.flatMap { it.fraseIds }
        assertEquals(individuais.size, individuais.toSet().size)
    }

    @Test
    fun mesmaSementeGeraAMesmaLicao() {
        assertEquals(
            GeradorDeExercicios.licao(paterNoster, Random(42)),
            GeradorDeExercicios.licao(paterNoster, Random(42)),
        )
    }

    @Test
    fun oracaoCurtaGeraLicaoMenorSemQuebrar() {
        val signum =
            listOf(
                FraseLatina("s.1", "In nomine Patris,", "Em nome do Pai,"),
                FraseLatina("s.2", "et Filii,", "e do Filho,"),
                FraseLatina("s.3", "et Spiritus Sancti.", "e do Espírito Santo."),
                FraseLatina("s.4", "Amen.", "Amém."),
            )
        val licao = GeradorDeExercicios.licao(signum, Random(3))
        assertTrue(licao.size in 3..GeradorDeExercicios.TAMANHO_LICAO, "${licao.size}")
        assertTrue(licao.any { it is Exercicio.Associar })
    }
}
