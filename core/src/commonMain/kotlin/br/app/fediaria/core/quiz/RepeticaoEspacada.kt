package br.app.fediaria.core.quiz

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** Situação de uma pergunta (ou flashcard) já vista, no sistema de Leitner. */
data class EstadoSrs(
    /** 1 a [RepeticaoEspacada.CAIXA_MAXIMA]. */
    val caixa: Int,
    val proximaRevisao: LocalDate,
    val acertos: Int,
    val erros: Int,
)

/**
 * Repetição espaçada de Leitner: caixas com intervalos de 1, 3, 7, 14 e 30 dias.
 * Acertou → sobe uma caixa (um item novo que acerta vai direto para a 2ª). Errou → volta para a 1ª.
 * Sem punição: errar só faz a pergunta voltar mais cedo.
 */
object RepeticaoEspacada {
    val INTERVALOS_DIAS = listOf(1, 3, 7, 14, 30)
    val CAIXA_MAXIMA = INTERVALOS_DIAS.size

    fun responder(
        anterior: EstadoSrs?,
        acertou: Boolean,
        hoje: LocalDate,
    ): EstadoSrs {
        val caixa = if (acertou) minOf((anterior?.caixa ?: 1) + 1, CAIXA_MAXIMA) else 1
        return EstadoSrs(
            caixa = caixa,
            proximaRevisao = hoje.plus(INTERVALOS_DIAS[caixa - 1], DateTimeUnit.DAY),
            acertos = (anterior?.acertos ?: 0) + if (acertou) 1 else 0,
            erros = (anterior?.erros ?: 0) + if (acertou) 0 else 1,
        )
    }

    fun vencido(
        estado: EstadoSrs,
        hoje: LocalDate,
    ): Boolean = estado.proximaRevisao <= hoje
}
