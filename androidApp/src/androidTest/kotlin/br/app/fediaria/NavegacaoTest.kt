package br.app.fediaria

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavegacaoTest {
    @get:Rule
    val regra = createAndroidComposeRule<MainActivity>()

    @Test
    fun abreNoTercoENavegaPelasQuatroAbas() {
        regra.onNodeWithTag("tela_terco").assertIsDisplayed()
        regra.onNodeWithTag("aba_terco").assertIsSelected()

        listOf("novenas", "quiz", "latim", "terco").forEach { aba ->
            regra.onNodeWithTag("aba_$aba").performClick()
            regra.onNodeWithTag("tela_$aba").assertIsDisplayed()
            regra.onNodeWithTag("aba_$aba").assertIsSelected()
        }
    }

    @Test
    fun abreConfiguracoesEVolta() {
        regra.onNodeWithTag("aba_quiz").performClick()
        regra.onNodeWithContentDescription("Configurações").performClick()
        regra.onNodeWithTag("tela_configuracoes").assertIsDisplayed()
        regra.onNodeWithTag("aba_quiz").assertDoesNotExist()

        regra.onNodeWithContentDescription("Voltar").performClick()
        regra.onNodeWithTag("tela_quiz").assertIsDisplayed()
    }
}
