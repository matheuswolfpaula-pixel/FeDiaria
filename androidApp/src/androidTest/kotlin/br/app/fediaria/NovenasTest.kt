package br.app.fediaria

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class NovenasTest {
    // Com a permissão já concedida, ligar o lembrete não abre o diálogo do sistema.
    @get:Rule(order = 0)
    val permissao: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule(order = 1)
    val regra = createAndroidComposeRule<MainActivity>()

    @Before
    fun preparar() {
        limparEstadoDoApp()
        regra.onNodeWithTag("aba_novenas").performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("catalogo_sao_jose"), TEMPO)
    }

    @Test
    fun iniciarNovenaEMarcarODiaUm() {
        regra.onNodeWithTag("catalogo_sao_jose").performScrollTo().performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_comecar_hoje"), TEMPO)
        regra.onNodeWithTag("botao_comecar_hoje").performScrollTo().performClick()

        regra.waitUntilAtLeastOneExists(hasTestTag("tela_novena_ativa"), TEMPO)
        regra.waitUntilAtLeastOneExists(hasText("Hoje é o 1º dia", substring = true), TEMPO)
        regra.onNodeWithTag("botao_rezar_dia").performScrollTo().performClick()

        regra.waitUntilAtLeastOneExists(hasText("1º dia rezado hoje", substring = true), TEMPO)
        regra.waitUntilDoesNotExist(hasTestTag("botao_rezar_dia"), TEMPO)

        // Desfazer devolve o botão (toque por engano).
        regra.onNodeWithTag("botao_desfazer_novena").performScrollTo().performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_rezar_dia"), TEMPO)
    }

    @Test
    fun novenaIniciadaApareceEmAndamentoNaLista() {
        regra.onNodeWithTag("catalogo_natal").performScrollTo().performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("botao_comecar_hoje"), TEMPO)
        regra.onNodeWithTag("botao_comecar_hoje").performScrollTo().performClick()
        regra.waitUntilAtLeastOneExists(hasTestTag("tela_novena_ativa"), TEMPO)

        // Dentro da novena não há barra inferior: volta para a lista.
        regra.onNodeWithContentDescription("Voltar").performClick()
        regra.waitUntilAtLeastOneExists(hasText("Dia 1 · reze hoje"), TEMPO)
    }

    private companion object {
        const val TEMPO = 5_000L
    }
}
