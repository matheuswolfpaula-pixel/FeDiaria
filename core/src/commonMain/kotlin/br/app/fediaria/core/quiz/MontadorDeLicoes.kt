package br.app.fediaria.core.quiz

import kotlinx.datetime.LocalDate

/** O mínimo que o montador precisa saber de uma pergunta. */
data class ChavePergunta(
    val id: String,
    val trilha: String,
    val nivel: Int,
)

data class Licao(
    /** Estável: `<trilha>.n<nivel>.l<numero>` (ex.: `escrituras.n1.l2`). */
    val id: String,
    val trilha: String,
    val nivel: Int,
    val numero: Int,
    val perguntas: List<String>,
)

/** Monta as lições fixas de cada trilha/nível e a lição de revisão. */
object MontadorDeLicoes {
    const val MINIMO = 5
    const val MAXIMO = 10
    private const val ALVO = 8
    const val MAXIMO_REVISAO = 10
    const val ID_REVISAO = "revisao"

    /**
     * Divide cada trilha/nível, na ordem do conteúdo, em lições equilibradas de [MINIMO] a [MAXIMO]
     * perguntas (ex.: 9 → 9; 17 → 6 + 6 + 5). Um nível com menos de [MINIMO] perguntas vira uma lição só.
     */
    fun licoes(perguntas: List<ChavePergunta>): List<Licao> =
        perguntas
            .groupBy { it.trilha to it.nivel }
            .entries
            .sortedWith(compareBy({ it.key.first }, { it.key.second }))
            .flatMap { (chave, doNivel) ->
                val (trilha, nivel) = chave
                dividir(doNivel.map { it.id }).mapIndexed { i, ids ->
                    Licao("$trilha.n$nivel.l${i + 1}", trilha, nivel, i + 1, ids)
                }
            }

    fun dividir(ids: List<String>): List<List<String>> {
        if (ids.isEmpty()) return emptyList()
        // Até MAXIMO cabe numa lição só; acima disso, ⌈n/8⌉ partes já garantem 5..8 por lição.
        val partes = if (ids.size <= MAXIMO) 1 else (ids.size + ALVO - 1) / ALVO
        val base = ids.size / partes
        val sobra = ids.size % partes
        var inicio = 0
        return List(partes) { i ->
            val tamanho = base + if (i < sobra) 1 else 0
            ids.subList(inicio, inicio + tamanho).also { inicio += tamanho }
        }
    }

    /**
     * Perguntas vencidas para a revisão de hoje: as mais atrasadas primeiro e, no empate,
     * as de caixa mais baixa (as que a pessoa mais erra). Ignora ids que não existem mais no conteúdo.
     */
    fun revisao(
        estados: Map<String, EstadoSrs>,
        hoje: LocalDate,
        idsExistentes: Set<String>,
        maximo: Int = MAXIMO_REVISAO,
    ): List<String> =
        estados
            .filter { (id, estado) -> id in idsExistentes && RepeticaoEspacada.vencido(estado, hoje) }
            .entries
            .sortedWith(compareBy({ it.value.proximaRevisao }, { it.value.caixa }, { it.key }))
            .take(maximo)
            .map { it.key }

    /** O nível 1 está sempre aberto; os seguintes, quando todas as lições do nível anterior foram feitas. */
    fun nivelAberto(
        trilha: String,
        nivel: Int,
        licoes: List<Licao>,
        concluidas: Set<String>,
    ): Boolean =
        nivel <= 1 ||
            licoes
                .filter { it.trilha == trilha && it.nivel == nivel - 1 }
                .all { it.id in concluidas }
}
