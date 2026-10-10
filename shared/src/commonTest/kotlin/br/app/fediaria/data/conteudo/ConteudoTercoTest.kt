package br.app.fediaria.data.conteudo

import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.core.terco.OracaoTerco
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
import kotlin.test.fail

/**
 * Validador do conteúdo do terço (roda no `check`): todo item tem id único, `fonte` e `revisado`
 * explícitos, e o conteúdo cobre tudo o que o terço guiado usa.
 */
class ConteudoTercoTest {
    private val texto =
        FileSystem.SYSTEM.read("src/commonMain/composeResources/files/conteudo/terco.json".toPath()) {
            readUtf8()
        }
    private val conteudo = ConteudoTerco.deJson(texto)
    private val bruto = Json.parseToJsonElement(texto).jsonObject

    @Test
    fun todaOracaoUsadaNoTercoExiste() {
        OracaoTerco.entries.forEach { oracao ->
            val item = conteudo.oracoes.firstOrNull { it.id == oracao.id } ?: fail("Falta a oração ${oracao.id}")
            assertTrue(item.texto.isNotBlank() && item.titulo.isNotBlank(), "Oração ${oracao.id} vazia")
        }
    }

    @Test
    fun cadaConjuntoTemCincoMisteriosComReferencia() {
        assertEquals(ConjuntoMisterios.entries.toSet(), conteudo.misterios.map { it.conjunto }.toSet())
        conteudo.misterios.forEach { conjunto ->
            assertEquals(5, conjunto.itens.size, "${conjunto.conjunto} deve ter 5 mistérios")
            conjunto.itens.forEachIndexed { i, misterio ->
                assertEquals("${conjunto.conjunto.name.lowercase()}.${i + 1}", misterio.id)
                assertTrue(misterio.referencia.isNotBlank(), "${misterio.id} sem referência")
            }
        }
    }

    @Test
    fun idsSaoUnicos() {
        val ids = conteudo.oracoes.map { it.id } + conteudo.misterios.flatMap { c -> c.itens.map { it.id } }
        assertEquals(
            ids.size,
            ids.toSet().size,
            "Ids duplicados: ${ids.groupBy { it }.filter { it.value.size > 1 }.keys}",
        )
    }

    @Test
    fun todoItemDeclaraFonteERevisadoExplicitamente() {
        val itens =
            bruto.getValue("oracoes").jsonArray.map { it.jsonObject } +
                bruto.getValue("misterios").jsonArray.flatMap { c ->
                    c.jsonObject
                        .getValue("itens")
                        .jsonArray
                        .map { it.jsonObject }
                }
        itens.forEach { item: JsonObject ->
            val id = item.getValue("id").jsonPrimitive.content
            assertTrue(item["fonte"]?.jsonPrimitive?.content?.isNotBlank() == true, "$id sem fonte")
            assertTrue(item.containsKey("revisado"), "$id sem o campo revisado")
        }
    }
}
