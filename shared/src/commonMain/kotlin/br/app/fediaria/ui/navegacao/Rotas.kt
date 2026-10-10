package br.app.fediaria.ui.navegacao

import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.aba_latim
import br.app.fediaria.shared.resources.aba_novenas
import br.app.fediaria.shared.resources.aba_quiz
import br.app.fediaria.shared.resources.aba_terco
import br.app.fediaria.shared.resources.ic_latim
import br.app.fediaria.shared.resources.ic_novenas
import br.app.fediaria.shared.resources.ic_quiz
import br.app.fediaria.shared.resources.ic_terco
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** Destinos de navegação (type-safe). */
sealed interface Rota {
    @Serializable data object Terco : Rota

    @Serializable data object Novenas : Rota

    @Serializable data object Quiz : Rota

    @Serializable data object Latim : Rota

    @Serializable data object Configuracoes : Rota

    /** [conjunto] = nome de [br.app.fediaria.core.terco.ConjuntoMisterios]. */
    @Serializable data class TercoGuiado(
        val conjunto: String,
    ) : Rota

    @Serializable data object Historico : Rota

    @Serializable data object AjudaLembretes : Rota

    /** Catálogo: apresentação de uma novena antes de começar ([novenaId] do conteúdo). */
    @Serializable data class DetalheNovena(
        val novenaId: String,
    ) : Rota

    /** Uma novena do usuário (id no banco). */
    @Serializable data class NovenaAtiva(
        val id: Long,
    ) : Rota
}

/** Abas da barra inferior, na ordem em que aparecem. */
enum class Aba(
    val rota: Rota,
    val titulo: StringResource,
    val icone: DrawableResource,
    val tag: String,
) {
    TERCO(Rota.Terco, Res.string.aba_terco, Res.drawable.ic_terco, "aba_terco"),
    NOVENAS(Rota.Novenas, Res.string.aba_novenas, Res.drawable.ic_novenas, "aba_novenas"),
    QUIZ(Rota.Quiz, Res.string.aba_quiz, Res.drawable.ic_quiz, "aba_quiz"),
    LATIM(Rota.Latim, Res.string.aba_latim, Res.drawable.ic_latim, "aba_latim"),
}
