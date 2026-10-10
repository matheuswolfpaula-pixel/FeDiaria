package br.app.fediaria.data.conteudo

import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.shared.resources.Res
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OracaoConteudo(
    val id: String,
    val titulo: String,
    val texto: String,
    val fonte: String,
    val revisado: Boolean = false,
)

@Serializable
data class MisterioConteudo(
    val id: String,
    val titulo: String,
    val referencia: String,
    val fonte: String,
    val revisado: Boolean = false,
)

@Serializable
data class ConjuntoConteudo(
    val conjunto: ConjuntoMisterios,
    val itens: List<MisterioConteudo>,
)

@Serializable
data class ConteudoTerco(
    val versao: Int,
    val oracoes: List<OracaoConteudo>,
    val misterios: List<ConjuntoConteudo>,
) {
    private val oracoesPorId by lazy { oracoes.associateBy { it.id } }
    private val conjuntosPorTipo by lazy { misterios.associateBy { it.conjunto } }

    fun oracao(id: String): OracaoConteudo = oracoesPorId.getValue(id)

    fun conjunto(tipo: ConjuntoMisterios): ConjuntoConteudo = conjuntosPorTipo.getValue(tipo)

    /** [numero] de 1 a 5. */
    fun misterio(
        tipo: ConjuntoMisterios,
        numero: Int,
    ): MisterioConteudo = conjunto(tipo).itens[numero - 1]

    companion object {
        val json = Json { ignoreUnknownKeys = false }

        fun deJson(texto: String): ConteudoTerco = json.decodeFromString(serializer(), texto)
    }
}

/** Lê o conteúdo empacotado no app (somente leitura) e guarda em memória. */
class ConteudoRepository {
    private val mutex = Mutex()
    private var terco: ConteudoTerco? = null

    suspend fun terco(): ConteudoTerco =
        mutex.withLock {
            terco
                ?: ConteudoTerco.deJson(Res.readBytes("files/conteudo/terco.json").decodeToString()).also { terco = it }
        }
}
