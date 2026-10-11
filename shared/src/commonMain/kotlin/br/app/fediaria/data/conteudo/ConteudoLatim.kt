package br.app.fediaria.data.conteudo

import br.app.fediaria.core.latim.FraseLatina
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FraseConteudo(
    /** `latim.<oracao>.<nn>`: também é o id do flashcard no SRS. */
    val id: String,
    /** "V" (versículo) ou "R" (resposta), quando a frase é dialogada. */
    val papel: String? = null,
    val latim: String,
    /** Pronúncia eclesiástica: sílaba tônica em MAIÚSCULAS. */
    val pronuncia: String,
    val traducao: String,
    val nota: String? = null,
) {
    fun paraCore() = FraseLatina(id, latim, traducao)
}

@Serializable
data class OracaoLatim(
    val id: String,
    val latim: String,
    val titulo: String,
    val descricao: String,
    val fonte: String,
    val revisado: Boolean = false,
    /** false = usada só no terço em latim, fora da lista da aba Latim. */
    val catalogo: Boolean = true,
    val frases: List<FraseConteudo>,
)

@Serializable
data class LemaLatim(
    val latim: String,
    val pronuncia: String,
    val traducao: String,
    val nota: String,
    val fonte: String,
    val revisado: Boolean = false,
)

@Serializable
data class RegraDePronuncia(
    val tema: String,
    val explicacao: String,
)

@Serializable
data class ConteudoLatim(
    val versao: Int,
    val lema: LemaLatim,
    val guiaPronuncia: List<RegraDePronuncia>,
    val oracoes: List<OracaoLatim>,
) {
    private val porId by lazy { oracoes.associateBy { it.id } }

    fun oracao(id: String): OracaoLatim? = porId[id]

    val catalogo: List<OracaoLatim> by lazy { oracoes.filter { it.catalogo } }

    val idsDasFrases: Set<String> by lazy { oracoes.flatMap { o -> o.frases.map { it.id } }.toSet() }

    companion object {
        private val json = Json { ignoreUnknownKeys = false }

        fun deJson(texto: String): ConteudoLatim = json.decodeFromString(serializer(), texto)
    }
}
