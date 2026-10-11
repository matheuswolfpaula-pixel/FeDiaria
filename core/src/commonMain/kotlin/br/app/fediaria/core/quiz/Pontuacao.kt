package br.app.fediaria.core.quiz

/** XP existe só no quiz e no latim; a oração nunca vale XP. */
object Pontuacao {
    const val XP_POR_ACERTO = 10

    fun xp(acertos: Int): Int = acertos * XP_POR_ACERTO
}
