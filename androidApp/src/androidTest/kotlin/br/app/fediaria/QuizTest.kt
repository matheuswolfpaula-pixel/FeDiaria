package br.app.fediaria

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.app.fediaria.core.quiz.MontadorDeLicoes
import br.app.fediaria.core.quiz.OrdemDasAlternativas
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.TipoPergunta
import br.app.fediaria.data.db.QuizDao
import br.app.fediaria.data.db.SrsItemEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class QuizTest {
    @get:Rule
    val regra = createAndroidComposeRule<MainActivity>()

    private val koin get() = GlobalContext.get()

    @Before
    fun preparar() {
        limparEstadoDoApp()
        regra.onNodeWithTag("aba_quiz").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("licao_escrituras.n1.l1"), TEMPO)
    }

    /** Posição na tela da alternativa certa, com a mesma ordem embaralhada que o app usa hoje. */
    private fun posicoesCertas(licaoId: String): List<Int> =
        runBlocking {
            val quiz = koin.get<ConteudoRepository>().quiz()
            val hoje = koin.get<Relogio>().hoje()
            val licao = MontadorDeLicoes.licoes(quiz.perguntas.map { it.chave() }).first { it.id == licaoId }
            licao.perguntas.map { id ->
                val p = requireNotNull(quiz.pergunta(id))
                OrdemDasAlternativas
                    .ordem(
                        p.alternativas.size,
                        OrdemDasAlternativas.semente(p.id, hoje),
                        p.tipo != TipoPergunta.VERDADEIRO_FALSO,
                    ).indexOf(p.correta)
            }
        }

    @Test
    fun licaoCompletaComTodasCertasDaXpEMarcaComoFeita() {
        val certas = posicoesCertas("escrituras.n1.l1")
        regra.onNodeWithTag("licao_escrituras.n1.l1").performScrollTo().performClick()

        certas.forEachIndexed { i, posicao ->
            regra.waitUntilAtLeastOneExists(hasText("Pergunta ${i + 1} de ${certas.size}"), TEMPO)
            regra.onNodeWithTag("alternativa_$posicao").performScrollTo().performClick()
            regra.waitUntilAtLeastOneExists(hasText("Isso mesmo!"), TEMPO)
            regra.waitUntilAtLeastOneExists(hasText("Para conferir:", substring = true), TEMPO)
            regra.onNodeWithTag("botao_continuar").performScrollTo().performClick()
        }

        regra.waitUntilAtLeastOneExists(hasTestTag("resultado_licao"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("Você acertou ${certas.size} de ${certas.size}."), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("+${certas.size * 10} XP"), TEMPO)
        regra.onNodeWithTag("botao_concluir_licao").performScrollTo().performClick()

        regra.waitUntilAtLeastOneExists(hasText("${certas.size * 10} XP"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("1 lição feita"), TEMPO)
    }

    @Test
    fun errarMostraARespostaCertaSemPunir() {
        val certa = posicoesCertas("escrituras.n1.l1").first()
        val errada = if (certa == 0) 1 else 0
        regra.onNodeWithTag("licao_escrituras.n1.l1").performScrollTo().performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("alternativa_$errada"), TEMPO)
        regra.onNodeWithTag("alternativa_$errada").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Quase! A resposta é: 73"), TEMPO)
        // A pergunta errada entra na revisão (caixa 1, volta amanhã).
        val srs = runBlocking { koin.get<QuizDao>().srs("quiz.escrituras.n1.001") }
        check(srs?.caixa == 1) { "esperava caixa 1, veio $srs" }
    }

    @Test
    fun perguntaVencidaApareceNaRevisaoDeHoje() {
        runBlocking {
            val hoje = koin.get<Relogio>().hoje()
            koin.get<QuizDao>().salvarSrs(SrsItemEntity("quiz.catecismo.n1.002", 1, hoje, 0, 1))
        }
        regra.waitUntilAtLeastOneExists(hasText("1 pergunta voltou para você relembrar."), TEMPO)
        regra.onNodeWithTag("cartao_revisao").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Quantos são os sacramentos?"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("Pergunta 1 de 1"), TEMPO)
    }

    private companion object {
        const val TEMPO = 5_000L
    }
}
