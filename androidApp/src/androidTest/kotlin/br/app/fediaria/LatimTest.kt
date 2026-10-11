package br.app.fediaria

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.app.fediaria.core.latim.Exercicio
import br.app.fediaria.core.latim.GeradorDeExercicios
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.db.QuizDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import kotlin.random.Random

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class LatimTest {
    @get:Rule
    val regra = createAndroidComposeRule<MainActivity>()

    private val koin get() = GlobalContext.get()

    @Before
    fun preparar() {
        limparEstadoDoApp()
        regra.onNodeWithTag("aba_latim").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("oracao_signum_crucis"), TEMPO)
    }

    private fun clicar(tag: String) {
        regra.waitUntilAtLeastOneExists(hasTestTag(tag), TEMPO)
        regra.onNodeWithTag(tag).performScrollTo().performClick()
    }

    /** A mesma lição que o app gera hoje para a oração. */
    private fun licaoDeHoje(id: String): List<Exercicio> =
        runBlocking {
            val oracao = requireNotNull(koin.get<ConteudoRepository>().latim().oracao(id))
            val hoje = koin.get<Relogio>().hoje()
            GeradorDeExercicios.licao(
                oracao.frases.map { it.paraCore() },
                Random(GeradorDeExercicios.semente(id, hoje)),
            )
        }

    private fun resolver(exercicio: Exercicio) {
        when (exercicio) {
            is Exercicio.Lacuna -> {
                clicar("opcao_${exercicio.opcoes.indexOf(exercicio.correta)}")
            }

            is Exercicio.Ordenar -> {
                val usadas = mutableSetOf<Int>()
                exercicio.ordemCerta.forEach { palavra ->
                    val posicao =
                        exercicio.embaralhadas.indices.first {
                            it !in usadas &&
                                exercicio.embaralhadas[it] == palavra
                        }
                    usadas += posicao
                    clicar("palavra_$posicao")
                }
                clicar("botao_verificar")
            }

            is Exercicio.Associar -> {
                exercicio.pares.indices.forEach { par ->
                    clicar("par_latim_$par")
                    clicar("par_trad_${exercicio.ordemTraducoes.indexOf(par)}")
                }
            }
        }
    }

    @Test
    fun fluxoCompletoDeUmaOracaoLerExercitarEFlashcards() {
        clicar("oracao_signum_crucis")

        // Leitura frase a frase até o fim.
        clicar("botao_ler")
        regra.waitUntilAtLeastOneExists(hasText("in NO-mi-ne PA-tris,"), TEMPO)
        repeat(3) { clicar("botao_proxima") }
        regra.waitUntilAtLeastOneExists(hasText("Amém."), TEMPO)
        clicar("botao_concluir_leitura")

        // Exercícios: todos certos de primeira.
        clicar("botao_exercicios")
        val licao = licaoDeHoje("signum_crucis")
        licao.forEachIndexed { i, exercicio ->
            regra.waitUntilAtLeastOneExists(hasText("Exercício ${i + 1} de ${licao.size}"), TEMPO)
            resolver(exercicio)
            regra.waitUntilAtLeastOneExists(hasTestTag("feedback"), TEMPO)
            clicar("botao_continuar")
        }
        regra.waitUntilAtLeastOneExists(hasText("Você acertou ${licao.size} de ${licao.size} de primeira."), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("+${licao.size * 10} XP"), TEMPO)
        clicar("botao_concluir_exercicios")

        // Flashcards: as 4 frases novas; lembra de todas.
        clicar("botao_flashcards")
        repeat(4) {
            clicar("botao_mostrar")
            clicar("botao_lembrei")
        }
        regra.waitUntilAtLeastOneExists(hasText("Você lembrou 4 de 4.", substring = true), TEMPO)
        val srs = runBlocking { koin.get<QuizDao>().srs("latim.signum_crucis.01") }
        check(srs?.caixa == 2) { "flashcard lembrado deveria ir para a caixa 2: $srs" }
    }

    @Test
    fun rezarEmLatimMostraAOracaoInteiraEEscondeAPronuncia() {
        clicar("oracao_pater_noster")
        clicar("botao_rezar")
        regra.waitUntilAtLeastOneExists(hasText("Pater noster, qui es in caelis,"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("PA-ter NOS-ter, kui es in TCHE-lis,"), TEMPO)
        clicar("interruptor_pronuncia")
        regra.waitUntilDoesNotExist(hasText("PA-ter NOS-ter, kui es in TCHE-lis,"), TEMPO)
        clicar("interruptor_traducao")
        regra.waitUntilAtLeastOneExists(hasText("Pai nosso, que estais nos céus,"), TEMPO)
    }

    private companion object {
        const val TEMPO = 5_000L
    }
}
