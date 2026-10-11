package br.app.fediaria.ui.quiz

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.data.conteudo.Referencia
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.ic_certo
import br.app.fediaria.shared.resources.quiz_alternativa_certa
import br.app.fediaria.shared.resources.quiz_alternativa_escolhida
import br.app.fediaria.shared.resources.quiz_certo
import br.app.fediaria.shared.resources.quiz_continuar
import br.app.fediaria.shared.resources.quiz_errado
import br.app.fediaria.shared.resources.quiz_fim_acertos
import br.app.fediaria.shared.resources.quiz_fim_incentivo
import br.app.fediaria.shared.resources.quiz_fim_titulo
import br.app.fediaria.shared.resources.quiz_fim_voltar
import br.app.fediaria.shared.resources.quiz_fim_xp
import br.app.fediaria.shared.resources.quiz_pergunta_de
import br.app.fediaria.shared.resources.quiz_revisao_vazia
import br.app.fediaria.shared.resources.quiz_ver_resultado
import br.app.fediaria.shared.resources.ref_biblia
import br.app.fediaria.shared.resources.ref_biblia_capitulo
import br.app.fediaria.shared.resources.ref_cdc
import br.app.fediaria.shared.resources.ref_cic
import br.app.fediaria.shared.resources.ref_documento
import br.app.fediaria.shared.resources.ref_rotulo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaLicao(
    licaoId: String,
    aoSair: () -> Unit,
    viewModel: LicaoViewModel = koinViewModel(key = licaoId) { parametersOf(licaoId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("tela_licao"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val atual = estado.atual
        when {
            estado.carregando -> {}

            estado.vazia -> {
                Text(stringResource(Res.string.quiz_revisao_vazia), style = MaterialTheme.typography.bodyLarge)
                Button(onClick = aoSair, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Text(stringResource(Res.string.quiz_fim_voltar))
                }
            }

            estado.concluida -> {
                Resultado(estado, aoSair)
            }

            atual != null -> {
                Pergunta(estado, atual, viewModel::escolher, viewModel::continuar)
            }
        }
    }
}

@Composable
private fun Pergunta(
    estado: LicaoUiState,
    exibida: PerguntaExibida,
    aoEscolher: (Int) -> Unit,
    aoContinuar: () -> Unit,
) {
    val pergunta = exibida.pergunta
    val escolhida = estado.escolhida
    val respondida = escolhida != null

    LinearProgressIndicator(
        progress = { (estado.indice + if (respondida) 1 else 0) / estado.perguntas.size.toFloat() },
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        stringResource(Res.string.quiz_pergunta_de, estado.indice + 1, estado.perguntas.size),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
    )
    Text(
        pergunta.pergunta,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.semantics { heading() }.testTag("enunciado"),
    )

    exibida.ordem.forEachIndexed { posicao, original ->
        Alternativa(
            texto = pergunta.alternativas[original],
            certa = respondida && original == pergunta.correta,
            errada = respondida && original == escolhida && original != pergunta.correta,
            habilitada = !respondida,
            aoClicar = { aoEscolher(original) },
            modifier = Modifier.testTag("alternativa_$posicao"),
        )
    }

    if (escolhida != null) {
        val acertou = escolhida == pergunta.correta
        Card(
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }.testTag("feedback"),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (acertou) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                ),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (acertou) {
                        stringResource(Res.string.quiz_certo)
                    } else {
                        stringResource(Res.string.quiz_errado, pergunta.alternativas[pergunta.correta])
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(pergunta.explicacao, style = MaterialTheme.typography.bodyLarge)
                Text(
                    stringResource(Res.string.ref_rotulo, textoDaReferencia(pergunta.referencia)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Button(
            onClick = aoContinuar,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_continuar"),
        ) {
            Text(
                stringResource(if (estado.ultima) Res.string.quiz_ver_resultado else Res.string.quiz_continuar),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun Alternativa(
    texto: String,
    certa: Boolean,
    errada: Boolean,
    habilitada: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visual = visualDaAlternativa(certa, errada)
    val estadoLido =
        when {
            certa -> stringResource(Res.string.quiz_alternativa_certa)
            errada -> stringResource(Res.string.quiz_alternativa_escolhida)
            else -> null
        }
    OutlinedCard(
        onClick = aoClicar,
        enabled = habilitada,
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    role = Role.Button
                    if (estadoLido != null) stateDescription = estadoLido
                },
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = visual.fundo,
                disabledContainerColor = visual.fundo,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = visual.borda,
    ) {
        Row(
            Modifier.heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(texto, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            if (certa) {
                Icon(
                    painterResource(Res.drawable.ic_certo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private class VisualDaAlternativa(
    val fundo: Color,
    val borda: BorderStroke,
)

/** Certa: azul com visto; escolhida errada: tom de erro. Nunca só a cor: o texto do feedback e o visto acompanham. */
@Composable
private fun visualDaAlternativa(
    certa: Boolean,
    errada: Boolean,
): VisualDaAlternativa {
    val cores = MaterialTheme.colorScheme
    return when {
        certa -> VisualDaAlternativa(cores.primaryContainer, BorderStroke(2.dp, cores.primary))
        errada -> VisualDaAlternativa(cores.errorContainer, BorderStroke(2.dp, cores.error))
        else -> VisualDaAlternativa(cores.surface, BorderStroke(1.dp, cores.outlineVariant))
    }
}

@Composable
private fun Resultado(
    estado: LicaoUiState,
    aoSair: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp).testTag("resultado_licao"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(Res.string.quiz_fim_titulo),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            stringResource(Res.string.quiz_fim_acertos, estado.acertos, estado.perguntas.size),
            style = MaterialTheme.typography.titleMedium,
        )
        // Sem "+0 XP" em destaque: errar tudo não deve soar como castigo.
        if (estado.xpGanho > 0) {
            Text(
                stringResource(Res.string.quiz_fim_xp, estado.xpGanho),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        if (estado.acertos < estado.perguntas.size) {
            Text(
                stringResource(Res.string.quiz_fim_incentivo),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Button(
            onClick = aoSair,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 56.dp,
                    ).padding(top = 8.dp)
                    .testTag("botao_concluir_licao"),
        ) {
            Text(stringResource(Res.string.quiz_fim_voltar), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun textoDaReferencia(referencia: Referencia): String =
    when (referencia) {
        is Referencia.Biblia -> {
            val versiculos = referencia.versiculos
            if (versiculos == null) {
                stringResource(Res.string.ref_biblia_capitulo, referencia.livro, referencia.capitulo)
            } else {
                stringResource(Res.string.ref_biblia, referencia.livro, referencia.capitulo, versiculos)
            }
        }

        is Referencia.Catecismo -> {
            stringResource(Res.string.ref_cic, referencia.paragrafo)
        }

        is Referencia.CodigoCanonico -> {
            stringResource(Res.string.ref_cdc, referencia.canon)
        }

        is Referencia.Documento -> {
            stringResource(Res.string.ref_documento, referencia.titulo, referencia.numero)
        }
    }
