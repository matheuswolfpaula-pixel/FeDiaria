package br.app.fediaria.core.latim

import kotlinx.datetime.LocalDate
import kotlin.random.Random

/** O mínimo que o gerador precisa de uma frase de oração. */
data class FraseLatina(
    val id: String,
    val latim: String,
    val traducao: String,
)

sealed interface Exercicio {
    val fraseIds: List<String>

    /** Completar a palavra que falta. [palavras] mantém a pontuação original da frase. */
    data class Lacuna(
        val fraseId: String,
        val palavras: List<String>,
        val indiceLacuna: Int,
        /** Quatro opções sem pontuação, uma delas [correta]. */
        val opcoes: List<String>,
        val correta: String,
        val traducao: String,
    ) : Exercicio {
        override val fraseIds get() = listOf(fraseId)
    }

    /** Tocar as palavras na ordem certa. */
    data class Ordenar(
        val fraseId: String,
        val ordemCerta: List<String>,
        val embaralhadas: List<String>,
        val traducao: String,
    ) : Exercicio {
        override val fraseIds get() = listOf(fraseId)

        fun confere(resposta: List<String>) = resposta == ordemCerta
    }

    /** Ligar cada frase em latim à sua tradução. [ordemTraducoes] = índices de [pares] na ordem exibida. */
    data class Associar(
        val pares: List<FraseLatina>,
        val ordemTraducoes: List<Int>,
    ) : Exercicio {
        override val fraseIds get() = pares.map { it.id }
    }
}

/**
 * Gera os exercícios de uma oração a partir das frases. Tudo depende só da [Random] recebida,
 * então a mesma semente gera a mesma lição (o app usa oração + data; os testes, sementes fixas).
 */
object GeradorDeExercicios {
    const val OPCOES = 4
    const val PARES = 4
    const val TAMANHO_LICAO = 6
    private const val MIN_PALAVRA = 3
    private const val MIN_ORDENAR = 3
    private const val MAX_ORDENAR = 8

    private val pontuacao = Regex("""[,.;:!?]""")

    /** A lição de uma oração muda de um dia para o outro, mas fica igual durante o dia. */
    fun semente(
        oracaoId: String,
        dia: LocalDate,
    ): Int = oracaoId.hashCode() * PRIMO + dia.hashCode()

    private const val PRIMO = 31

    fun limpa(palavra: String) = palavra.replace(pontuacao, "")

    fun palavras(latim: String): List<String> = latim.split(' ').filter { limpa(it).isNotEmpty() }

    /** [pool] = outras palavras (da mesma oração, de preferência) usadas como distratores. */
    fun lacuna(
        frase: FraseLatina,
        pool: List<String>,
        random: Random,
    ): Exercicio.Lacuna? {
        val palavras = palavras(frase.latim)
        val candidatas = palavras.indices.filter { limpa(palavras[it]).length >= MIN_PALAVRA }
        if (palavras.size < 2 || candidatas.isEmpty()) return null
        val indice = candidatas.random(random)
        val correta = limpa(palavras[indice])
        val distratores =
            pool
                .map(::limpa)
                .filter { it.length >= MIN_PALAVRA && !it.equals(correta, ignoreCase = true) }
                .distinctBy { it.lowercase() }
                .shuffled(random)
                .take(OPCOES - 1)
        if (distratores.size < OPCOES - 1) return null
        return Exercicio.Lacuna(
            fraseId = frase.id,
            palavras = palavras,
            indiceLacuna = indice,
            opcoes = (distratores + correta).shuffled(random),
            correta = correta,
            traducao = frase.traducao,
        )
    }

    fun ordenar(
        frase: FraseLatina,
        random: Random,
    ): Exercicio.Ordenar? {
        val certas = palavras(frase.latim).map(::limpa)
        if (certas.size !in MIN_ORDENAR..MAX_ORDENAR || certas.distinct().size < 2) return null
        var embaralhadas = certas.shuffled(random)
        while (embaralhadas == certas) embaralhadas = certas.shuffled(random)
        return Exercicio.Ordenar(frase.id, certas, embaralhadas, frase.traducao)
    }

    fun associar(
        frases: List<FraseLatina>,
        random: Random,
    ): Exercicio.Associar? {
        val distintas = frases.distinctBy { it.traducao }.distinctBy { it.latim }
        if (distintas.size < PARES) return null
        val pares = distintas.shuffled(random).take(PARES)
        var ordem = pares.indices.shuffled(random)
        while (ordem == pares.indices.toList()) ordem = pares.indices.shuffled(random)
        return Exercicio.Associar(pares, ordem)
    }

    /**
     * Uma lição de até [tamanho] exercícios: alterna lacuna e ordenar em frases sorteadas
     * (cada frase uma vez) e põe um "associar" no meio, quando a oração tem frases suficientes.
     */
    fun licao(
        frases: List<FraseLatina>,
        random: Random,
        poolExtra: List<String> = emptyList(),
        tamanho: Int = TAMANHO_LICAO,
    ): List<Exercicio> {
        val associar = associar(frases, random)
        val alvo = tamanho - if (associar != null) 1 else 0
        val individuais = mutableListOf<Exercicio>()
        var preferirLacuna = true
        for (frase in frases.shuffled(random)) {
            if (individuais.size >= alvo) break
            val pool = frases.filter { it.id != frase.id }.flatMap { palavras(it.latim) } + poolExtra
            val primeiro = if (preferirLacuna) lacuna(frase, pool, random) else ordenar(frase, random)
            val exercicio = primeiro ?: if (preferirLacuna) ordenar(frase, random) else lacuna(frase, pool, random)
            if (exercicio != null) {
                individuais += exercicio
                preferirLacuna = !preferirLacuna
            }
        }
        if (associar != null) individuais.add(individuais.size / 2, associar)
        return individuais
    }
}
