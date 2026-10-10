package br.app.fediaria.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.aba_latim
import br.app.fediaria.shared.resources.aba_quiz
import br.app.fediaria.shared.resources.em_breve
import br.app.fediaria.shared.resources.ic_latim
import br.app.fediaria.shared.resources.ic_quiz
import br.app.fediaria.shared.resources.latim_descricao
import br.app.fediaria.shared.resources.quiz_descricao
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

// Telas provisórias da Fase 1: cada uma ganha conteúdo real na sua fase.

@Composable
fun TelaQuiz() = TelaEmBreve("tela_quiz", Res.drawable.ic_quiz, Res.string.aba_quiz, Res.string.quiz_descricao)

@Composable
fun TelaLatim() = TelaEmBreve("tela_latim", Res.drawable.ic_latim, Res.string.aba_latim, Res.string.latim_descricao)

@Composable
private fun TelaEmBreve(
    tag: String,
    icone: DrawableResource,
    titulo: StringResource,
    descricao: StringResource,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).testTag(tag),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painterResource(icone),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(64.dp),
        )
        Text(
            stringResource(titulo),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(stringResource(descricao), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Text(
            stringResource(Res.string.em_breve),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
