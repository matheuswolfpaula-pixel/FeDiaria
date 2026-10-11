package br.app.fediaria.ui.latim

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.latim.Exercicio
import br.app.fediaria.core.latim.GeradorDeExercicios
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.latim_associar_dica
import br.app.fediaria.shared.resources.latim_associar_errado
import br.app.fediaria.shared.resources.latim_associar_feito
import br.app.fediaria.shared.resources.latim_associar_titulo
import br.app.fediaria.shared.resources.latim_certo
import br.app.fediaria.shared.resources.latim_exercicio_de
import br.app.fediaria.shared.resources.latim_fim_acertos
import br.app.fediaria.shared.resources.latim_fim_titulo
import br.app.fediaria.shared.resources.latim_fim_voltar
import br.app.fediaria.shared.resources.latim_lacuna_errado
import br.app.fediaria.shared.resources.latim_lacuna_titulo
import br.app.fediaria.shared.resources.latim_ordenar_dica
import br.app.fediaria.shared.resources.latim_ordenar_errado
import br.app.fediaria.shared.resources.latim_ordenar_titulo
import br.app.fediaria.shared.resources.latim_verificar
import br.app.fediaria.shared.resources.quiz_continuar
import br.app.fediaria.shared.resources.quiz_fim_xp
import br.app.fediaria.shared.resources.quiz_ver_resultado
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaExerciciosLatim(
    id: String,
    aoSair: () -> Unit,
    viewModel: ExerciciosLatimViewModel = koinViewModel(key = "exercicios_$id") { parametersOf(id) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_exercicios_latim"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (estado.carregando) return@Column
        if (estado.finalizada) {
            Resultado(estado, aoSair)
            return@Column
        }
        val atual = estado.atual ?: return@Column
        val resposta = estado.resposta

        LinearProgressIndicator(
            progress = { (estado.indice + if (resposta.concluida) 1 else 0) / estado.exercicios.size.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(Res.string.latim_exercicio_de, estado.indice + 1, estado.exercicios.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        when (atual) {
            is Exercicio.Lacuna -> {
                Lacuna(atual, resposta, viewModel::escolherPalavra)
            }

            is Exercicio.Ordenar -> {
                Ordenar(
                    atual,
                    resposta,
                    viewModel::tocarPalavra,
                    viewModel::desfazerPalavra,
                    viewModel::verificarOrdem,
                )
            }

            is Exercicio.Associar -> {
                Associar(
                    atual,
                    resposta,
                    viewModel::selecionarLatim,
                    viewModel::selecionarTraducao,
                )
            }
        }
        if (resposta.concluida) {
            Retorno(estado, atual)
            Button(
                onClick = viewModel::continuar,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_continuar"),
            ) {
                Text(stringResource(if (estado.ultimo) Res.string.quiz_ver_resultado else Res.string.quiz_continuar))
            }
        }
    }
}

@Composable
private fun Enunciado(texto: String) =
    Text(texto, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })

@Composable
private fun Traducao(texto: String) =
    Text(
        "“$texto”",
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

@Composable
private fun Lacuna(
    lacuna: Exercicio.Lacuna,
    resposta: RespostaExercicio,
    aoEscolher: (String) -> Unit,
) {
    Enunciado(stringResource(Res.string.latim_lacuna_titulo))
    val frase =
        lacuna.palavras
            .mapIndexed { i, palavra ->
                if (i ==
                    lacuna.indiceLacuna
                ) {
                    palavra.replace(
                        GeradorDeExercicios.limpa(palavra),
                        if (resposta.concluida) lacuna.correta else "_____",
                    )
                } else {
                    palavra
                }
            }.joinToString(" ")
    Text(frase, style = estiloLatim, modifier = Modifier.testTag("frase_lacuna"))
    Traducao(lacuna.traducao)
    lacuna.opcoes.forEachIndexed { i, opcao ->
        val certa = resposta.concluida && opcao == lacuna.correta
        val errada = resposta.concluida && opcao == resposta.escolhida && !resposta.acertou
        Opcao(opcao, certa, errada, !resposta.concluida, { aoEscolher(opcao) }, Modifier.testTag("opcao_$i"))
    }
}

@Composable
private fun Opcao(
    texto: String,
    certa: Boolean,
    errada: Boolean,
    habilitada: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cores = MaterialTheme.colorScheme
    val fundo =
        if (certa) {
            cores.primaryContainer
        } else if (errada) {
            cores.errorContainer
        } else {
            cores.surface
        }
    OutlinedCard(
        onClick = aoClicar,
        enabled = habilitada,
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = fundo,
                disabledContainerColor = fundo,
                disabledContentColor = cores.onSurface,
            ),
        border =
            BorderStroke(
                if (certa ||
                    errada
                ) {
                    2.dp
                } else {
                    1.dp
                },
                if (certa) {
                    cores.primary
                } else if (errada) {
                    cores.error
                } else {
                    cores.outlineVariant
                },
            ),
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}

@Composable
private fun Ordenar(
    ordenar: Exercicio.Ordenar,
    resposta: RespostaExercicio,
    aoTocar: (Int) -> Unit,
    aoDesfazer: (Int) -> Unit,
    aoVerificar: () -> Unit,
) {
    Enunciado(stringResource(Res.string.latim_ordenar_titulo))
    Traducao(ordenar.traducao)
    Text(
        stringResource(Res.string.latim_ordenar_dica),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp).testTag("frase_montada"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        FlowRow(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            resposta.montadas.forEachIndexed { i, posicao ->
                InputChip(
                    selected = false,
                    onClick = { aoDesfazer(i) },
                    enabled = !resposta.concluida,
                    label = { Text(ordenar.embaralhadas[posicao], fontFamily = FontFamily.Serif) },
                    modifier = Modifier.heightIn(min = 48.dp),
                )
            }
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ordenar.embaralhadas.forEachIndexed { posicao, palavra ->
            val usada = posicao in resposta.montadas
            FilterChip(
                selected = usada,
                onClick = { aoTocar(posicao) },
                enabled = !usada && !resposta.concluida,
                label = { Text(palavra, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Serif) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("palavra_$posicao"),
            )
        }
    }
    if (!resposta.concluida) {
        Button(
            onClick = aoVerificar,
            enabled = resposta.montadas.size == ordenar.embaralhadas.size,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_verificar"),
        ) { Text(stringResource(Res.string.latim_verificar)) }
    }
}

@Composable
private fun Associar(
    associar: Exercicio.Associar,
    resposta: RespostaExercicio,
    aoSelecionarLatim: (Int) -> Unit,
    aoSelecionarTraducao: (Int) -> Unit,
) {
    Enunciado(stringResource(Res.string.latim_associar_titulo))
    Text(
        stringResource(Res.string.latim_associar_dica),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    associar.pares.forEachIndexed { i, frase ->
        ItemDoPar(
            texto = frase.latim,
            serifado = true,
            ligado = i in resposta.ligados,
            selecionado = resposta.latimSelecionado == i,
            aoClicar = { aoSelecionarLatim(i) },
            modifier = Modifier.testTag("par_latim_$i"),
        )
    }
    associar.ordemTraducoes.forEachIndexed { posicao, par ->
        ItemDoPar(
            texto = associar.pares[par].traducao,
            serifado = false,
            ligado = par in resposta.ligados,
            selecionado = false,
            aoClicar = { aoSelecionarTraducao(posicao) },
            modifier = Modifier.testTag("par_trad_$posicao"),
        )
    }
    if (resposta.ultimaTentativaErrada && !resposta.concluida) {
        Text(
            stringResource(Res.string.latim_associar_errado),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Composable
private fun ItemDoPar(
    texto: String,
    serifado: Boolean,
    ligado: Boolean,
    selecionado: Boolean,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cores = MaterialTheme.colorScheme
    val fundo =
        if (ligado) {
            cores.primaryContainer
        } else if (selecionado) {
            cores.secondaryContainer
        } else {
            cores.surface
        }
    OutlinedCard(
        onClick = aoClicar,
        enabled = !ligado,
        modifier = modifier.fillMaxWidth().semantics { this.selected = selecionado || ligado },
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = fundo,
                disabledContainerColor = fundo,
                disabledContentColor = cores.onSurface,
            ),
        border =
            BorderStroke(
                if (selecionado) 2.dp else 1.dp,
                if (selecionado) cores.secondary else cores.outlineVariant,
            ),
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = if (serifado) FontFamily.Serif else null,
            modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun Retorno(
    estado: ExerciciosUiState,
    exercicio: Exercicio,
) {
    val resposta = estado.resposta
    val frase = (exercicio as? Exercicio.Lacuna)?.fraseId ?: (exercicio as? Exercicio.Ordenar)?.fraseId
    val conteudo = frase?.let(estado::frase)
    Card(
        modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }.testTag("feedback"),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (resposta.acertou) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
            ),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val titulo =
                when {
                    exercicio is Exercicio.Associar -> stringResource(Res.string.latim_associar_feito)
                    resposta.acertou -> stringResource(Res.string.latim_certo)
                    exercicio is Exercicio.Lacuna -> stringResource(Res.string.latim_lacuna_errado, exercicio.correta)
                    else -> stringResource(Res.string.latim_ordenar_errado)
                }
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            if (conteudo != null) {
                Text(conteudo.latim, style = MaterialTheme.typography.titleMedium, fontFamily = FontFamily.Serif)
                Text(
                    conteudo.pronuncia,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun Resultado(
    estado: ExerciciosUiState,
    aoSair: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp).testTag("resultado_exercicios"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            stringResource(
                Res.string.latim_fim_titulo,
            ),
            style = MaterialTheme.typography.headlineSmall,
            modifier =
                Modifier.semantics {
                    heading()
                },
        )
        Text(
            stringResource(Res.string.latim_fim_acertos, estado.acertos, estado.exercicios.size),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        if (estado.xpGanho > 0) {
            Text(
                stringResource(Res.string.quiz_fim_xp, estado.xpGanho),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Button(
            onClick = aoSair,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_concluir_exercicios"),
        ) {
            Text(stringResource(Res.string.latim_fim_voltar))
        }
    }
}
