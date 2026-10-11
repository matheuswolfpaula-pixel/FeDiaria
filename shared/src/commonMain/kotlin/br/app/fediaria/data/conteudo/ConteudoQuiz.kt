package br.app.fediaria.data.conteudo

import br.app.fediaria.core.quiz.ChavePergunta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** De onde vem a resposta: a pessoa pode conferir na fonte. O campo `tipo` do JSON escolhe a variante. */
@Serializable
sealed interface Referencia {
    @Serializable
    @SerialName("biblia")
    data class Biblia(
        /** Abreviação pt-BR (ver [LivrosDaBiblia]). */
        val livro: String,
        val capitulo: Int,
        /** Ex.: "16", "1-11", "16.18". Ausente = capítulo inteiro. */
        val versiculos: String? = null,
    ) : Referencia

    /** Catecismo da Igreja Católica: só o número do parágrafo, nunca o texto. */
    @Serializable
    @SerialName("cic")
    data class Catecismo(
        val paragrafo: Int,
    ) : Referencia

    /** Código de Direito Canônico (1983). */
    @Serializable
    @SerialName("cdc")
    data class CodigoCanonico(
        val canon: Int,
    ) : Referencia

    /** Documento do Magistério ou livro litúrgico, pelo número do parágrafo. */
    @Serializable
    @SerialName("documento")
    data class Documento(
        val titulo: String,
        val numero: Int,
    ) : Referencia
}

@Serializable
enum class TipoPergunta {
    @SerialName("multipla_escolha")
    MULTIPLA_ESCOLHA,

    @SerialName("verdadeiro_falso")
    VERDADEIRO_FALSO,
}

@Serializable
data class Pergunta(
    val id: String,
    val trilha: String,
    val nivel: Int,
    val tipo: TipoPergunta,
    val pergunta: String,
    val alternativas: List<String>,
    /** Índice em [alternativas]. */
    val correta: Int,
    val explicacao: String,
    val referencia: Referencia,
    val fonte: String,
    val revisado: Boolean = false,
) {
    fun chave() = ChavePergunta(id, trilha, nivel)
}

@Serializable
data class Trilha(
    val id: String,
    val nome: String,
    val descricao: String,
)

@Serializable
data class ConteudoQuiz(
    val versao: Int,
    val trilhas: List<Trilha>,
    val perguntas: List<Pergunta>,
) {
    private val porId by lazy { perguntas.associateBy { it.id } }

    fun pergunta(id: String): Pergunta? = porId[id]

    companion object {
        private val json =
            Json {
                ignoreUnknownKeys = false
                classDiscriminator = "tipo"
            }

        fun deJson(texto: String): ConteudoQuiz = json.decodeFromString(serializer(), texto)
    }
}

/** Os 73 livros do cânon católico, com a abreviação usada no app e o nº de capítulos (o maior entre as edições). */
object LivrosDaBiblia {
    val capitulos: Map<String, Int> =
        linkedMapOf(
            // Antigo Testamento (46)
            "Gn" to 50,
            "Ex" to 40,
            "Lv" to 27,
            "Nm" to 36,
            "Dt" to 34,
            "Js" to 24,
            "Jz" to 21,
            "Rt" to 4,
            "1Sm" to 31,
            "2Sm" to 24,
            "1Rs" to 22,
            "2Rs" to 25,
            "1Cr" to 29,
            "2Cr" to 36,
            "Esd" to 10,
            "Ne" to 13,
            "Tb" to 14,
            "Jt" to 16,
            "Est" to 16,
            "1Mc" to 16,
            "2Mc" to 15,
            "Jó" to 42,
            "Sl" to 150,
            "Pr" to 31,
            "Ecl" to 12,
            "Ct" to 8,
            "Sb" to 19,
            "Eclo" to 51,
            "Is" to 66,
            "Jr" to 52,
            "Lm" to 5,
            "Br" to 6,
            "Ez" to 48,
            "Dn" to 14,
            "Os" to 14,
            "Jl" to 4,
            "Am" to 9,
            "Ab" to 1,
            "Jn" to 4,
            "Mq" to 7,
            "Na" to 3,
            "Hab" to 3,
            "Sf" to 3,
            "Ag" to 2,
            "Zc" to 14,
            "Ml" to 4,
            // Novo Testamento (27)
            "Mt" to 28,
            "Mc" to 16,
            "Lc" to 24,
            "Jo" to 21,
            "At" to 28,
            "Rm" to 16,
            "1Cor" to 16,
            "2Cor" to 13,
            "Gl" to 6,
            "Ef" to 6,
            "Fl" to 4,
            "Cl" to 4,
            "1Ts" to 5,
            "2Ts" to 3,
            "1Tm" to 6,
            "2Tm" to 4,
            "Tt" to 3,
            "Fm" to 1,
            "Hb" to 13,
            "Tg" to 5,
            "1Pd" to 5,
            "2Pd" to 3,
            "1Jo" to 5,
            "2Jo" to 1,
            "3Jo" to 1,
            "Jd" to 1,
            "Ap" to 22,
        )
}
