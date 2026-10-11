package br.app.fediaria

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.app.fediaria.data.config.ConfigTerco
import br.app.fediaria.data.config.ConfiguracoesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class TercoTest {
    @get:Rule
    val regra = createAndroidComposeRule<MainActivity>()

    @Before
    fun preparar() {
        limparEstadoDoApp()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_rezar"), TEMPO)
        esperarOfensiva("Ofensiva de 0 dias")
    }

    private fun esperarOfensiva(descricao: String) {
        regra.waitUntil(TEMPO) {
            runCatching {
                regra.onNodeWithTag("contador_ofensiva").assertContentDescriptionEquals(descricao)
            }.isSuccess
        }
    }

    @Test
    fun rezarOTercoGuiadoInteiroAcendeAChama() {
        regra.onNodeWithTag("botao_rezar").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_proximo"), TEMPO)

        var cliques = 0
        while (regra.onAllNodes(hasTestTag("terco_concluido")).fetchSemanticsNodes().isEmpty()) {
            check(cliques++ < 100) { "O terço deveria terminar em 79 passos" }
            regra.onNodeWithTag("botao_proximo").performClick()
            regra.waitForIdle()
        }
        check(cliques == 79) { "Esperados 79 passos, foram $cliques" }

        regra.onNodeWithTag("ofensiva_conclusao").assertTextContains("1 dia de ofensiva")
        regra.onNodeWithTag("botao_voltar_inicio").performClick()
        esperarOfensiva("Ofensiva de 1 dia")
    }

    @Test
    fun marcarManualmenteEDesfazer() {
        regra.onNodeWithTag("botao_ja_rezei").performScrollTo().performClick()
        esperarOfensiva("Ofensiva de 1 dia")

        regra.onNodeWithTag("botao_desfazer").performScrollTo().performClick()
        esperarOfensiva("Ofensiva de 0 dias")
    }

    @Test
    fun tercoInterrompidoContinuaDeOndeParou() {
        regra.onNodeWithTag("botao_rezar").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_proximo"), TEMPO)
        repeat(5) {
            regra.onNodeWithTag("botao_proximo").performClick()
            regra.waitForIdle()
        }
        regra.onNodeWithContentDescription("Voltar").performClick()

        regra.waitUntilAtLeastOneExists(hasText("Continuar o terço (6 de 79)"), TEMPO)
        regra.onNodeWithTag("botao_rezar").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Passo 6 de 79"), TEMPO)
        regra.onNodeWithText("Passo 6 de 79").assertExists()
    }

    @Test
    fun tercoGuiadoEmLatimMostraAsOracoesEmLatim() {
        val configuracoes = GlobalContext.get().get<ConfiguracoesRepository>()
        runBlocking { configuracoes.definirTerco(ConfigTerco(latim = true)) }

        regra.onNodeWithTag("botao_rezar").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Per signum Crucis"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("per SI-nhum KRU-tchis"), TEMPO)
        regra.onNodeWithText("Pelo sinal da Santa Cruz", substring = true).assertDoesNotExist()

        // Credo e Pai-Nosso também em latim.
        regra.onNodeWithTag("botao_proximo").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Credo in Deum Patrem omnipotentem,"), TEMPO)
        regra.onNodeWithTag("botao_proximo").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Pater noster, qui es in caelis,"), TEMPO)

        // Sem pronúncia, a frase em latim continua e a pronúncia some.
        runBlocking { configuracoes.definirTerco(ConfigTerco(latim = true, pronunciaLatim = false)) }
        regra.onNodeWithContentDescription("Voltar").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_rezar"), TEMPO)
        regra.onNodeWithTag("botao_rezar").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Pater noster, qui es in caelis,"), TEMPO)
        regra.onNodeWithText("PA-ter NOS-ter, kui es in TCHE-lis,").assertDoesNotExist()
    }

    private companion object {
        const val TEMPO = 5_000L
    }
}
