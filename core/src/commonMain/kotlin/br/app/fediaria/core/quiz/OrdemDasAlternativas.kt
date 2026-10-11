package br.app.fediaria.core.quiz

import kotlinx.datetime.LocalDate
import kotlin.random.Random

/**
 * Embaralha a ordem em que as alternativas aparecem, para a resposta certa não ficar sempre
 * na mesma posição. A ordem é estável durante o dia (mesma [semente] → mesma ordem) e muda de um dia
 * para o outro. Verdadeiro/Falso não é embaralhado.
 */
object OrdemDasAlternativas {
    /** Índices originais na ordem em que devem ser exibidos. */
    fun ordem(
        quantidade: Int,
        semente: Int,
        embaralhar: Boolean = true,
    ): List<Int> {
        val indices = (0 until quantidade).toList()
        return if (embaralhar) indices.shuffled(Random(semente)) else indices
    }

    fun semente(
        idPergunta: String,
        dia: LocalDate,
    ): Int = idPergunta.hashCode() * PRIMO + dia.hashCode()

    private const val PRIMO = 31
}
