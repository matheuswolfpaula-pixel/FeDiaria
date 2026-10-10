package br.app.fediaria.core.novena

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

enum class SituacaoNovena {
    /** Começa numa data futura. */
    AGENDADA,
    EM_ANDAMENTO,
    CONCLUIDA,
}

/**
 * @property diaAtual próximo dia a rezar (1–9); `null` quando concluída.
 * @property previsaoTermino quando a novena termina se a pessoa rezar um dia por dia a partir de agora.
 */
data class EstadoNovena(
    val situacao: SituacaoNovena,
    val diaAtual: Int?,
    val diasRezados: Int,
    val rezouHoje: Boolean,
    val previsaoTermino: LocalDate,
) {
    /** Hoje é dia de rezar e ainda não rezou: é o que libera o botão e o lembrete. */
    val pendenteHoje: Boolean get() = situacao == SituacaoNovena.EM_ANDAMENTO && !rezouHoje
}

/**
 * Regras das novenas. O progresso é por DIA REZADO, não por calendário: o "dia atual" é sempre o
 * próximo ainda não rezado. Quem perde dias simplesmente continua de onde parou, sem punição.
 * Reza-se no máximo um dia da novena por data, como manda a tradição.
 */
object CalculadoraNovena {
    const val DIAS = 9

    /** @param rezados dia da novena (1–9) → data local em que foi rezado. */
    fun calcular(
        inicio: LocalDate,
        rezados: Map<Int, LocalDate>,
        hoje: LocalDate,
    ): EstadoNovena {
        val diaAtual = (1..DIAS).firstOrNull { it !in rezados }
        val rezouHoje = hoje in rezados.values
        val situacao =
            when {
                diaAtual == null -> SituacaoNovena.CONCLUIDA
                hoje < inicio -> SituacaoNovena.AGENDADA
                else -> SituacaoNovena.EM_ANDAMENTO
            }
        val restantes = DIAS - rezados.size
        val previsao =
            if (restantes == 0) {
                rezados.values.max()
            } else {
                val proximoDiaLivre =
                    when {
                        hoje < inicio -> inicio
                        rezouHoje -> hoje.plus(1, DateTimeUnit.DAY)
                        else -> hoje
                    }
                proximoDiaLivre.plus(restantes - 1, DateTimeUnit.DAY)
            }
        return EstadoNovena(situacao, diaAtual, rezados.size, rezouHoje, previsao)
    }

    /** Só se reza o dia atual, a partir da data de início, um por data. */
    fun podeRezar(
        dia: Int,
        estado: EstadoNovena,
    ): Boolean = estado.pendenteHoje && dia == estado.diaAtual
}
