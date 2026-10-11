package br.app.fediaria.data.conteudo

import br.app.fediaria.core.quiz.MontadorDeLicoes
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Validador do conteúdo do quiz (roda no `check`). */
class ConteudoQuizTest {
    private val texto =
        FileSystem.SYSTEM.read("src/commonMain/composeResources/files/conteudo/quiz.json".toPath()) { readUtf8() }
    private val conteudo = ConteudoQuiz.deJson(texto)
    private val perguntas = conteudo.perguntas

    @Test
    fun idsSaoUnicosEDescrevemTrilhaENivel() {
        val repetidos = perguntas.groupingBy { it.id }.eachCount().filterValues { it > 1 }
        assertTrue(repetidos.isEmpty(), "ids repetidos: ${repetidos.keys}")
        perguntas.forEach {
            assertTrue(
                Regex("""quiz\.${it.trilha}\.n${it.nivel}\.\d{3}""").matches(it.id),
                "id fora do padrão: ${it.id}",
            )
        }
    }

    @Test
    fun tresTrilhasComTresNiveisECercaDeCentoECinquentaPerguntas() {
        assertEquals(setOf("escrituras", "catecismo", "fe_catolica"), conteudo.trilhas.map { it.id }.toSet())
        assertTrue(perguntas.size in 140..200, "${perguntas.size} perguntas")
        conteudo.trilhas.forEach { trilha ->
            (1..3).forEach { nivel ->
                val n = perguntas.count { it.trilha == trilha.id && it.nivel == nivel }
                assertTrue(n >= MontadorDeLicoes.MINIMO, "${trilha.id} nível $nivel tem só $n perguntas")
            }
        }
        assertTrue(perguntas.all { p -> conteudo.trilhas.any { it.id == p.trilha } && p.nivel in 1..3 })
    }

    @Test
    fun alternativasEOpcaoCorretaSaoCoerentes() {
        perguntas.forEach { p ->
            assertTrue(p.pergunta.isNotBlank() && p.explicacao.isNotBlank(), "${p.id} sem texto")
            assertTrue(p.correta in p.alternativas.indices, "${p.id}: correta fora das alternativas")
            assertEquals(p.alternativas.size, p.alternativas.toSet().size, "${p.id}: alternativas repetidas")
            assertTrue(p.alternativas.none { it.isBlank() }, "${p.id}: alternativa vazia")
            when (p.tipo) {
                TipoPergunta.VERDADEIRO_FALSO -> {
                    assertEquals(listOf("Verdadeiro", "Falso"), p.alternativas, p.id)
                }

                TipoPergunta.MULTIPLA_ESCOLHA -> {
                    assertTrue(
                        p.alternativas.size in 3..4,
                        "${p.id}: ${p.alternativas.size} alternativas",
                    )
                }
            }
        }
    }

    @Test
    fun todaReferenciaEhValida() {
        perguntas.forEach { p ->
            when (val r = p.referencia) {
                is Referencia.Biblia -> {
                    val max = LivrosDaBiblia.capitulos[r.livro]
                    assertTrue(max != null, "${p.id}: livro desconhecido '${r.livro}'")
                    assertTrue(r.capitulo in 1..max, "${p.id}: ${r.livro} não tem o capítulo ${r.capitulo}")
                    r.versiculos?.let {
                        assertTrue(Regex("""\d+(-\d+)?([.,]\d+(-\d+)?)*""").matches(it), "${p.id}: versículos '$it'")
                    }
                }

                is Referencia.Catecismo -> {
                    assertTrue(r.paragrafo in 1..2865, "${p.id}: §${r.paragrafo}")
                }

                is Referencia.CodigoCanonico -> {
                    assertTrue(r.canon in 1..1752, "${p.id}: cân. ${r.canon}")
                }

                is Referencia.Documento -> {
                    assertTrue(r.titulo.isNotBlank() && r.numero > 0, "${p.id}: documento")
                }
            }
        }
    }

    @Test
    fun listaDeLivrosTemOCanonCatolicoCompleto() {
        assertEquals(73, LivrosDaBiblia.capitulos.size)
    }

    @Test
    fun todoItemDeclaraFonteERevisadoExplicitamente() {
        val itens =
            Json
                .parseToJsonElement(texto)
                .jsonObject
                .getValue("perguntas")
                .jsonArray
                .map { it.jsonObject }
        itens.forEach { item ->
            val id = item.getValue("id").jsonPrimitive.content
            assertTrue(item["fonte"]?.jsonPrimitive?.content?.isNotBlank() == true, "$id sem fonte")
            assertTrue(item.containsKey("revisado"), "$id sem o campo revisado")
            assertTrue(item.containsKey("referencia"), "$id sem referência")
        }
    }

    @Test
    fun todasAsLicoesFicamEntreCincoEDezPerguntas() {
        val licoes = MontadorDeLicoes.licoes(perguntas.map { it.chave() })
        assertTrue(licoes.all { it.perguntas.size in MontadorDeLicoes.MINIMO..MontadorDeLicoes.MAXIMO })
        assertEquals(perguntas.size, licoes.sumOf { it.perguntas.size })
    }
}
