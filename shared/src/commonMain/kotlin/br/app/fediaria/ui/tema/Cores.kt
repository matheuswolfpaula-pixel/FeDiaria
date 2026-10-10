package br.app.fediaria.ui.tema

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Paleta FéDiária: azul mariano (primária), dourado (secundária), creme (superfícies).
// Superfícies definidas explicitamente para não herdar o lilás padrão do Material 3.

val EsquemaClaro =
    lightColorScheme(
        primary = Color(0xFF1F3F7A),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD8E2FF),
        onPrimaryContainer = Color(0xFF001A43),
        secondary = Color(0xFF7A5E00),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFE08A),
        onSecondaryContainer = Color(0xFF251A00),
        tertiary = Color(0xFF5B5D72),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFE0E0F9),
        onTertiaryContainer = Color(0xFF181A2C),
        background = Color(0xFFFBF8F1),
        onBackground = Color(0xFF1B1B1F),
        surface = Color(0xFFFBF8F1),
        onSurface = Color(0xFF1B1B1F),
        surfaceVariant = Color(0xFFE4E1D8),
        onSurfaceVariant = Color(0xFF47464F),
        surfaceTint = Color(0xFF1F3F7A),
        outline = Color(0xFF77767F),
        outlineVariant = Color(0xFFC8C5BC),
        surfaceDim = Color(0xFFDCD9D0),
        surfaceBright = Color(0xFFFBF8F1),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF6F2EA),
        surfaceContainer = Color(0xFFF0ECE3),
        surfaceContainerHigh = Color(0xFFEAE6DD),
        surfaceContainerHighest = Color(0xFFE4E1D8),
    )

val EsquemaEscuro =
    darkColorScheme(
        primary = Color(0xFFADC6FF),
        onPrimary = Color(0xFF002E69),
        primaryContainer = Color(0xFF1F3F7A),
        onPrimaryContainer = Color(0xFFD8E2FF),
        secondary = Color(0xFFE9C349),
        onSecondary = Color(0xFF3E2E00),
        secondaryContainer = Color(0xFF594400),
        onSecondaryContainer = Color(0xFFFFE08A),
        tertiary = Color(0xFFC4C4DD),
        onTertiary = Color(0xFF2D2F42),
        tertiaryContainer = Color(0xFF434559),
        onTertiaryContainer = Color(0xFFE0E0F9),
        background = Color(0xFF121318),
        onBackground = Color(0xFFE3E2E8),
        surface = Color(0xFF121318),
        onSurface = Color(0xFFE3E2E8),
        surfaceVariant = Color(0xFF45464F),
        onSurfaceVariant = Color(0xFFC6C5D0),
        surfaceTint = Color(0xFFADC6FF),
        outline = Color(0xFF8F909A),
        outlineVariant = Color(0xFF45464F),
        surfaceDim = Color(0xFF121318),
        surfaceBright = Color(0xFF38393F),
        surfaceContainerLowest = Color(0xFF0D0E13),
        surfaceContainerLow = Color(0xFF1A1B21),
        surfaceContainer = Color(0xFF1E1F25),
        surfaceContainerHigh = Color(0xFF292A2F),
        surfaceContainerHighest = Color(0xFF34343A),
    )
