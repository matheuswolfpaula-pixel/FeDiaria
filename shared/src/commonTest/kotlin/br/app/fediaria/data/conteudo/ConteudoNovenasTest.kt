package br.app.fediaria.data.conteudo

import br.app.fediaria.core.novena.CalculadoraNovena
import br.app.fediaria.core.novena.JanelaNovena
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Validador do conteúdo das novenas (roda no `check`). */
class ConteudoNovenasTest {
    private val pasta = "src/commonMain/composeResources/files/conteudo".toPath()
    private val texto = FileSystem.SYSTEM.read(pasta / "novenas.json") { readUtf8() }
    private val conteudo = ConteudoNovenas.deJson(texto)
    private val terco = ConteudoTerco.deJson(FileSystem.SYSTEM.read(pasta / "terco.json") { readUtf8() })

    @Test
    fun catalogoTemAsSeisNovenasPrevistas() {
        assertEquals(
            setOf("espirito_santo", "imaculada_conceicao", "natal", "aparecida", "sao_jose", "sagrado_coracao"),
            conteudo.novenas.map { it.id }.toSet(),
        )
    }

    @Test
    fun cadaNovenaTemNoveDiasNumeradosComTextoEReferencia() {
        conteudo.novenas.forEach { novena ->
            assertEquals(CalculadoraNovena.DIAS, novena.dias.size, novena.id)
            novena.dias.forEachIndexed { i, dia ->
                assertEquals(i + 1, dia.dia, "${novena.id} fora de ordem")
                assertEquals("${novena.id}.${i + 1}", dia.id)
                assertTrue(dia.tema.isNotBlank() && dia.meditacao.isNotBlank(), "${dia.id} vazio")
                assertTrue(dia.referencia.isNotBlank(), "${dia.id} sem referência")
            }
            listOf(novena.oracaoInicial, novena.oracaoFinal, novena.jaculatoria).forEach {
                assertTrue(it.texto.isNotBlank() && it.fonte.isNotBlank(), "${novena.id}: oração incompleta")
            }
        }
    }

    @Test
    fun oracoesDiariasExistemNoConteudoDoTerco() {
        val ids = terco.oracoes.map { it.id }.toSet()
        conteudo.novenas.forEach { novena ->
            assertTrue(novena.oracoesDiarias.isNotEmpty())
            novena.oracoesDiarias.forEach {
                assertTrue(
                    it in ids,
                    "${novena.id}: oração '$it' não existe em terco.json",
                )
            }
        }
    }

    @Test
    fun janelasSaoPlausiveis() {
        conteudo.novenas.forEach { novena ->
            when (val janela = novena.janela.paraCore()) {
                is JanelaNovena.Fixa -> assertTrue(janela.mes in 1..12 && janela.dia in 1..31, novena.id)

                // Entre a Páscoa e o fim do tempo pascal estendido (Sagrado Coração = +68).
                is JanelaNovena.RelativaAPascoa -> assertTrue(janela.diasAposPascoa in 1..70, novena.id)
            }
        }
    }

    @Test
    fun todoTextoDeclaraFonteERevisadoExplicitamente() {
        val bruto = Json.parseToJsonElement(texto).jsonObject
        bruto.getValue("novenas").jsonArray.map { it.jsonObject }.forEach { novena ->
            val id = novena.getValue("id").jsonPrimitive.content
            val itens: List<Pair<String, JsonObject>> =
                listOf("novena" to novena) +
                    listOf("oracaoInicial", "oracaoFinal", "jaculatoria").map {
                        "$id.$it" to
                            novena.getValue(it).jsonObject
                    } +
                    novena.getValue("dias").jsonArray.map {
                        it.jsonObject
                            .getValue("id")
                            .jsonPrimitive.content to
                            it.jsonObject
                    }
            itens.forEach { (nome, item) ->
                assertTrue(item["fonte"]?.jsonPrimitive?.content?.isNotBlank() == true, "$nome sem fonte")
                assertTrue(item.containsKey("revisado"), "$nome sem o campo revisado")
            }
        }
    }
}
