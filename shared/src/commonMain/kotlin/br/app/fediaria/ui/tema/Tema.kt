package br.app.fediaria.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

private val tipografiaPadrao = Typography()

/** Títulos em serifa (tom de missal); corpo em sem-serifa para leitura confortável. */
private val Tipografia =
    tipografiaPadrao.copy(
        displayLarge = tipografiaPadrao.displayLarge.copy(fontFamily = FontFamily.Serif),
        displayMedium = tipografiaPadrao.displayMedium.copy(fontFamily = FontFamily.Serif),
        displaySmall = tipografiaPadrao.displaySmall.copy(fontFamily = FontFamily.Serif),
        headlineLarge = tipografiaPadrao.headlineLarge.copy(fontFamily = FontFamily.Serif),
        headlineMedium = tipografiaPadrao.headlineMedium.copy(fontFamily = FontFamily.Serif),
        headlineSmall = tipografiaPadrao.headlineSmall.copy(fontFamily = FontFamily.Serif),
        titleLarge = tipografiaPadrao.titleLarge.copy(fontFamily = FontFamily.Serif),
    )

@Composable
fun FeDiariaTema(
    escuro: Boolean,
    conteudo: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (escuro) EsquemaEscuro else EsquemaClaro,
        typography = Tipografia,
        content = conteudo,
    )
}
