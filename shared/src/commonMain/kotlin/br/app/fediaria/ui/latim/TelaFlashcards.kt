package br.app.fediaria.ui.latim

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.latim_fim_voltar
import br.app.fediaria.shared.resources.latim_flashcards_fim
import br.app.fediaria.shared.resources.latim_flashcards_fim_desc
import br.app.fediaria.shared.resources.latim_flashcards_vazio
import br.app.fediaria.shared.resources.latim_frase_de
import br.app.fediaria.shared.resources.latim_lembrei
import br.app.fediaria.shared.resources.latim_mostrar_resposta
import br.app.fediaria.shared.resources.latim_nao_lembrei
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaFlashcards(
    alvo: String,
    aoSair: () -> Unit,
    viewModel: FlashcardsViewModel = koinViewModel(key = "flashcards_$alvo") { parametersOf(alvo) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_flashcards"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (estado.carregando) return@Column
        val carta = estado.atual
        if (carta == null) {
            Fim(estado, aoSair)
            return@Column
        }
        LinearProgressIndicator(
            progress = { estado.indice / estado.cartas.size.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(Res.string.latim_frase_de, estado.indice + 1, estado.cartas.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            Column(
                Modifier.padding(20.dp).semantics { liveRegion = LiveRegionMode.Polite },
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(textoLatino(carta), style = estiloLatim, modifier = Modifier.testTag("carta_latim"))
                if (estado.virada) {
                    Text(
                        carta.pronuncia,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(carta.traducao, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
                }
            }
        }
        if (!estado.virada) {
            Button(
                onClick = viewModel::virar,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_mostrar"),
            ) {
                Text(stringResource(Res.string.latim_mostrar_resposta))
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { viewModel.responder(lembrei = false) },
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp).testTag("botao_nao_lembrei"),
                ) { Text(stringResource(Res.string.latim_nao_lembrei)) }
                Button(
                    onClick = { viewModel.responder(lembrei = true) },
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp).testTag("botao_lembrei"),
                ) { Text(stringResource(Res.string.latim_lembrei)) }
            }
        }
    }
}

@Composable
private fun Fim(
    estado: FlashcardsUiState,
    aoSair: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp).testTag("fim_flashcards"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (estado.cartas.isEmpty()) {
            Text(
                stringResource(Res.string.latim_flashcards_vazio),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                stringResource(
                    Res.string.latim_flashcards_fim,
                ),
                style = MaterialTheme.typography.headlineSmall,
                modifier =
                    Modifier.semantics {
                        heading()
                    },
            )
            Text(
                stringResource(Res.string.latim_flashcards_fim_desc, estado.lembradas, estado.cartas.size),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
        Button(
            onClick = aoSair,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_concluir_flashcards"),
        ) {
            Text(stringResource(Res.string.latim_fim_voltar))
        }
    }
}
