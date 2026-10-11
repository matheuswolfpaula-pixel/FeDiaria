package br.app.fediaria.ui.latim

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.quiz.MontadorDeLicoes
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.latim_acao_exercicios
import br.app.fediaria.shared.resources.latim_acao_exercicios_desc
import br.app.fediaria.shared.resources.latim_acao_flashcards
import br.app.fediaria.shared.resources.latim_acao_flashcards_desc
import br.app.fediaria.shared.resources.latim_acao_ler
import br.app.fediaria.shared.resources.latim_acao_ler_desc
import br.app.fediaria.shared.resources.latim_acao_rezar
import br.app.fediaria.shared.resources.latim_acao_rezar_desc
import br.app.fediaria.shared.resources.latim_flashcards_desc
import br.app.fediaria.shared.resources.latim_flashcards_titulo
import br.app.fediaria.shared.resources.latim_guia
import br.app.fediaria.shared.resources.latim_intro
import br.app.fediaria.shared.resources.latim_lema_rotulo
import br.app.fediaria.shared.resources.latim_memorizadas
import br.app.fediaria.shared.resources.latim_oracoes
import br.app.fediaria.ui.comum.TituloDeSecao
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaLatim(
    aoAbrirOracao: (String) -> Unit,
    aoAbrirFlashcards: (String) -> Unit,
    aoAbrirGuia: () -> Unit,
    viewModel: LatimViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
                .testTag("tela_latim"),
    ) {
        val conteudo = estado.conteudo ?: return@Column
        val lema = conteudo.lema

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(Res.string.latim_lema_rotulo),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(lema.latim, style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif)
                Text(lema.pronuncia, style = MaterialTheme.typography.labelLarge)
                Text(lema.traducao, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
                Text(lema.nota, style = MaterialTheme.typography.bodySmall)
            }
        }

        Text(
            stringResource(Res.string.latim_intro),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        TextButton(
            onClick = aoAbrirGuia,
            modifier = Modifier.padding(horizontal = 4.dp).heightIn(min = 48.dp).testTag("botao_guia_pronuncia"),
        ) { Text(stringResource(Res.string.latim_guia)) }

        if (estado.flashcardsPendentes > 0) {
            Card(
                onClick = { aoAbrirFlashcards(MontadorDeLicoes.ID_REVISAO) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ).testTag("cartao_flashcards"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(Res.string.latim_flashcards_titulo),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        pluralStringResource(
                            Res.plurals.latim_flashcards_desc,
                            estado.flashcardsPendentes,
                            estado.flashcardsPendentes,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        TituloDeSecao(stringResource(Res.string.latim_oracoes))
        estado.oracoes.forEach { item ->
            Card(
                onClick = { aoAbrirOracao(item.oracao.id) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("oracao_${item.oracao.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.oracao.latim, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Serif)
                    Text(
                        item.oracao.titulo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(Res.string.latim_memorizadas, item.memorizadas, item.oracao.frases.size),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

/** Menu de uma oração: as quatro formas de aprender. */
@Composable
fun TelaOracaoLatim(
    id: String,
    aoLer: () -> Unit,
    aoExercitar: () -> Unit,
    aoFlashcards: () -> Unit,
    aoRezar: () -> Unit,
    viewModel: OracaoLatimViewModel = koinViewModel(key = "oracao_$id") { parametersOf(id) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val oracao = estado.oracao ?: return

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_oracao_latim"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            oracao.latim,
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            oracao.titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(oracao.descricao, style = MaterialTheme.typography.bodyMedium)
        Text(
            stringResource(Res.string.latim_memorizadas, estado.memorizadas, oracao.frases.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Acao(Res.string.latim_acao_ler, Res.string.latim_acao_ler_desc, "botao_ler", aoLer)
        Acao(Res.string.latim_acao_exercicios, Res.string.latim_acao_exercicios_desc, "botao_exercicios", aoExercitar)
        Acao(Res.string.latim_acao_flashcards, Res.string.latim_acao_flashcards_desc, "botao_flashcards", aoFlashcards)
        Acao(Res.string.latim_acao_rezar, Res.string.latim_acao_rezar_desc, "botao_rezar", aoRezar)
    }
}

@Composable
private fun Acao(
    titulo: StringResource,
    descricao: StringResource,
    tag: String,
    aoClicar: () -> Unit,
) {
    Card(
        onClick = aoClicar,
        modifier = Modifier.fillMaxWidth().testTag(tag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.heightIn(min = 56.dp).padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(titulo), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(descricao),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun TelaGuiaPronuncia(viewModel: LatimViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val guia = estado.conteudo?.guiaPronuncia ?: return
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_guia_pronuncia"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        guia.forEach { regra ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(regra.tema, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text(regra.explicacao, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
