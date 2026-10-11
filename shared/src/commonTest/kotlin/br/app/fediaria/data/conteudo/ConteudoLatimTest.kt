package br.app.fediaria.data.conteudo

import br.app.fediaria.core.latim.GeradorDeExercicios
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Validador do conteúdo de latim (roda no `check`). */
class ConteudoLatimTest {
    private val texto =
        FileSystem.SYSTEM.read("src/commonMain/composeResources/files/conteudo/latim.json".toPath()) { readUtf8() }
    private val conteudo = ConteudoLatim.deJson(texto)
    private val frases = conteudo.oracoes.flatMap { it.frases }

    @Test
    fun temAsDezOracoesPrevistas() {
        assertEquals(
            setOf(
                "signum_crucis",
                "pater_noster",
                "ave_maria",
                "gloria_patri",
                "credo",
                "salve_regina",
                "angelus",
                "anima_christi",
                "veni_sancte_spiritus",
                "sub_tuum",
            ),
            conteudo.catalogo.map { it.id }.toSet(),
        )
        // Fora do catálogo: só as usadas no terço em latim.
        assertEquals(
            setOf("per_signum_crucis", "o_mi_iesu"),
            conteudo.oracoes
                .filterNot { it.catalogo }
                .map { it.id }
                .toSet(),
        )
        assertEquals("Ad Maiorem Dei Gloriam", conteudo.lema.latim)
    }

    @Test
    fun idsDasFrasesSaoUnicosEDescrevemAOracao() {
        assertEquals(frases.size, frases.map { it.id }.toSet().size)
        conteudo.oracoes.forEach { o ->
            assertTrue(o.frases.isNotEmpty(), o.id)
            o.frases.forEachIndexed { i, f ->
                assertEquals("latim.${o.id}.${(i + 1).toString().padStart(2, '0')}", f.id)
            }
        }
    }

    @Test
    fun cadaFraseTemTextoTraducaoEPapelValido() {
        frases.forEach { f ->
            assertTrue(f.latim.isNotBlank() && f.pronuncia.isNotBlank() && f.traducao.isNotBlank(), f.id)
            assertTrue(f.papel == null || f.papel in setOf("V", "R"), "${f.id}: papel ${f.papel}")
        }
    }

    @Test
    fun pronunciaTemUmaPalavraParaCadaPalavraLatinaETonicaMarcada() {
        frases.forEach { f ->
            val latim = f.latim.split(' ')
            val pronuncia = f.pronuncia.split(' ')
            assertEquals(latim.size, pronuncia.size, "${f.id}: '${f.latim}' × '${f.pronuncia}'")
            pronuncia.filter { it.contains('-') }.forEach { palavra ->
                val silabas = palavra.trim(',', '.', ';', ':').split('-')
                assertEquals(
                    1,
                    silabas.count { s -> s.any(Char::isLetter) && s.filter(Char::isLetter).all(Char::isUpperCase) },
                    "${f.id}: '$palavra' precisa de exatamente uma sílaba tônica",
                )
            }
        }
    }

    @Test
    fun todaOracaoGeraUmaLicaoDeExercicios() {
        conteudo.oracoes.forEach { o ->
            val licao = GeradorDeExercicios.licao(o.frases.map { it.paraCore() }, Random(1))
            assertTrue(licao.size >= 3, "${o.id}: só ${licao.size} exercícios")
        }
    }

    @Test
    fun todoItemDeclaraFonteERevisadoExplicitamente() {
        val raiz = Json.parseToJsonElement(texto).jsonObject
        val itens = raiz.getValue("oracoes").jsonArray.map { it.jsonObject } + raiz.getValue("lema").jsonObject
        itens.forEach { item ->
            assertTrue(item["fonte"]?.jsonPrimitive?.content?.isNotBlank() == true, "$item sem fonte")
            assertTrue(item.containsKey("revisado"), "$item sem o campo revisado")
        }
    }
}
