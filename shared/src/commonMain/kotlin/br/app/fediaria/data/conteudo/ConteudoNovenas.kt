package br.app.fediaria.data.conteudo

import br.app.fediaria.core.novena.JanelaNovena
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OracaoNovena(
    val titulo: String,
    val texto: String,
    val fonte: String,
    val revisado: Boolean = false,
)

@Serializable
data class DiaNovena(
    val id: String,
    val dia: Int,
    val tema: String,
    val referencia: String,
    val meditacao: String,
    val fonte: String,
    val revisado: Boolean = false,
)

@Serializable
sealed interface JanelaConteudo {
    fun paraCore(): JanelaNovena

    @Serializable
    @SerialName("FIXA")
    data class Fixa(
        val mes: Int,
        val dia: Int,
    ) : JanelaConteudo {
        override fun paraCore() = JanelaNovena.Fixa(mes, dia)
    }

    @Serializable
    @SerialName("PASCOA")
    data class Pascoa(
        val diasAposPascoa: Int,
    ) : JanelaConteudo {
        override fun paraCore() = JanelaNovena.RelativaAPascoa(diasAposPascoa)
    }
}

@Serializable
data class Novena(
    val id: String,
    val nome: String,
    val festa: String,
    val descricao: String,
    val janela: JanelaConteudo,
    val oracaoInicial: OracaoNovena,
    /** Ids de orações de `terco.json` rezadas todos os dias (Pai-Nosso, Ave-Maria, Glória). */
    val oracoesDiarias: List<String>,
    val oracaoFinal: OracaoNovena,
    val jaculatoria: OracaoNovena,
    val dias: List<DiaNovena>,
    val fonte: String,
    val revisado: Boolean = false,
) {
    /** [numero] de 1 a 9. */
    fun dia(numero: Int): DiaNovena = dias[numero - 1]
}

@Serializable
data class ConteudoNovenas(
    val versao: Int,
    val novenas: List<Novena>,
) {
    private val porId by lazy { novenas.associateBy { it.id } }

    fun novena(id: String): Novena? = porId[id]

    companion object {
        private val json = kotlinx.serialization.json.Json { classDiscriminator = "tipo" }

        fun deJson(texto: String): ConteudoNovenas = json.decodeFromString(serializer(), texto)
    }
}
