package br.app.fediaria.ui.terco

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.ofensiva.EstadoOfensiva
import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.plataforma.lembrarPedidoDePermissaoDeNotificacao
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_ajustar
import br.app.fediaria.shared.resources.acao_ativar_lembrete
import br.app.fediaria.shared.resources.acao_cancelar
import br.app.fediaria.shared.resources.acao_confirmar
import br.app.fediaria.shared.resources.acao_continuar_terco
import br.app.fediaria.shared.resources.acao_desfazer_marcacao
import br.app.fediaria.shared.resources.acao_esqueci_ontem
import br.app.fediaria.shared.resources.acao_ja_rezei
import br.app.fediaria.shared.resources.acao_liberar_notificacoes
import br.app.fediaria.shared.resources.acao_recomecar
import br.app.fediaria.shared.resources.acao_rezar_terco
import br.app.fediaria.shared.resources.acao_ver_historico
import br.app.fediaria.shared.resources.confirmar_ontem_texto
import br.app.fediaria.shared.resources.confirmar_ontem_titulo
import br.app.fediaria.shared.resources.hoje_misterios
import br.app.fediaria.shared.resources.lembrete_ativo_resumo
import br.app.fediaria.shared.resources.lembrete_bloqueado
import br.app.fediaria.shared.resources.lembrete_inativo_resumo
import br.app.fediaria.shared.resources.lembrete_negado
import br.app.fediaria.shared.resources.lembrete_titulo
import br.app.fediaria.shared.resources.ofensiva_apagada
import br.app.fediaria.shared.resources.ofensiva_descricao_acessivel
import br.app.fediaria.shared.resources.ofensiva_dias
import br.app.fediaria.shared.resources.ofensiva_em_risco
import br.app.fediaria.shared.resources.ofensiva_gracas
import br.app.fediaria.shared.resources.ofensiva_gracas_cheias
import br.app.fediaria.shared.resources.ofensiva_proxima_graca
import br.app.fediaria.shared.resources.ofensiva_recorde
import br.app.fediaria.shared.resources.ofensiva_rezado_hoje
import br.app.fediaria.shared.resources.outros_misterios
import br.app.fediaria.ui.comum.dataPorExtenso
import br.app.fediaria.ui.comum.formatado
import br.app.fediaria.ui.comum.nome
import br.app.fediaria.ui.comum.nomeCurto
import br.app.fediaria.ui.comum.nomeDoDiaDaSemana
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TelaTerco(
    aoRezar: (ConjuntoMisterios) -> Unit,
    aoVerHistorico: () -> Unit,
    aoAjustarLembretes: () -> Unit,
    aoLiberarNotificacoes: () -> Unit,
    viewModel: TercoViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.atualizarPermissao()
        onPauseOrDispose { }
    }
    val pedirPermissao = lembrarPedidoDePermissaoDeNotificacao(viewModel::aoResponderPermissao)
    var confirmandoOntem by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("tela_terco"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (estado.carregando) return@Column

        BlocoOfensiva(estado.ofensiva)

        CartaoHoje(
            estado = estado,
            aoRezar = aoRezar,
            aoRecomecar = viewModel::recomecar,
            aoMarcarHoje = viewModel::marcarHoje,
            aoDesfazer = viewModel::desfazerHoje,
            aoEsqueciOntem = { confirmandoOntem = true },
        )

        CartaoLembrete(
            estado = estado,
            aoAtivar = pedirPermissao,
            aoAjustar = aoAjustarLembretes,
            aoLiberar = aoLiberarNotificacoes,
        )

        TextButton(onClick = aoVerHistorico, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(Res.string.acao_ver_historico))
        }
    }

    val ontem = estado.ontem
    if (confirmandoOntem && ontem != null) {
        AlertDialog(
            onDismissRequest = { confirmandoOntem = false },
            title = { Text(stringResource(Res.string.confirmar_ontem_titulo)) },
            text = { Text(stringResource(Res.string.confirmar_ontem_texto, dataPorExtenso(ontem))) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.marcarOntem()
                    confirmandoOntem = false
                }) { Text(stringResource(Res.string.acao_confirmar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmandoOntem = false }) { Text(stringResource(Res.string.acao_cancelar)) }
            },
        )
    }
}

@Composable
private fun BlocoOfensiva(ofensiva: EstadoOfensiva) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Chama(acesa = ofensiva.atual > 0, modifier = Modifier.size(width = 84.dp, height = 112.dp))
        val descricao = pluralStringResource(Res.plurals.ofensiva_descricao_acessivel, ofensiva.atual, ofensiva.atual)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.clearAndSetSemantics {
                    contentDescription = descricao
                    testTag = "contador_ofensiva"
                },
        ) {
            Text(
                ofensiva.atual.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                pluralStringResource(Res.plurals.ofensiva_dias, ofensiva.atual),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(
                when {
                    ofensiva.rezadoHoje -> Res.string.ofensiva_rezado_hoje
                    ofensiva.emRisco -> Res.string.ofensiva_em_risco
                    else -> Res.string.ofensiva_apagada
                },
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        val detalhes =
            buildList {
                if (ofensiva.recorde > 0) add(stringResource(Res.string.ofensiva_recorde, ofensiva.recorde))
                if (ofensiva.gracasDisponiveis > 0) {
                    add(
                        pluralStringResource(
                            Res.plurals.ofensiva_gracas,
                            ofensiva.gracasDisponiveis,
                            ofensiva.gracasDisponiveis,
                        ),
                    )
                }
            }
        if (detalhes.isNotEmpty()) {
            Text(
                detalhes.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (ofensiva.atual > 0) {
            val faltam = ofensiva.diasParaProximaGraca
            Text(
                if (faltam != null) {
                    pluralStringResource(Res.plurals.ofensiva_proxima_graca, faltam, faltam)
                } else {
                    stringResource(Res.string.ofensiva_gracas_cheias)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CartaoHoje(
    estado: TercoUiState,
    aoRezar: (ConjuntoMisterios) -> Unit,
    aoRecomecar: () -> Unit,
    aoMarcarHoje: () -> Unit,
    aoDesfazer: () -> Unit,
    aoEsqueciOntem: () -> Unit,
) {
    val hoje = estado.hoje ?: return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(
                    Res.string.hoje_misterios,
                    nomeDoDiaDaSemana(hoje).replaceFirstChar { it.uppercase() },
                    stringResource(estado.conjuntoDoDia.nome),
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )

            val progresso = estado.progresso
            if (progresso != null) {
                BotaoGrande(
                    texto =
                        stringResource(
                            Res.string.acao_continuar_terco,
                            progresso.indicePasso + 1,
                            estado.totalPassosProgresso,
                        ),
                    aoClicar = { aoRezar(progresso.conjunto) },
                    tag = "botao_rezar",
                )
                TextButton(onClick = aoRecomecar) { Text(stringResource(Res.string.acao_recomecar)) }
            } else {
                BotaoGrande(
                    texto = stringResource(Res.string.acao_rezar_terco),
                    aoClicar = { aoRezar(estado.conjuntoDoDia) },
                    tag = "botao_rezar",
                )
                Text(stringResource(Res.string.outros_misterios), style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConjuntoMisterios.entries.filter { it != estado.conjuntoDoDia }.forEach { conjunto ->
                        SuggestionChip(
                            onClick = { aoRezar(conjunto) },
                            label = { Text(stringResource(conjunto.nomeCurto)) },
                        )
                    }
                }
            }

            when {
                !estado.ofensiva.rezadoHoje -> {
                    OutlinedButton(
                        onClick = aoMarcarHoje,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("botao_ja_rezei"),
                    ) { Text(stringResource(Res.string.acao_ja_rezei)) }
                }

                estado.hojeFoiManual -> {
                    TextButton(onClick = aoDesfazer, modifier = Modifier.testTag("botao_desfazer")) {
                        Text(stringResource(Res.string.acao_desfazer_marcacao))
                    }
                }
            }
            if (!estado.ontemRezado && estado.ofensiva.totalDiasRezados > 0) {
                TextButton(onClick = aoEsqueciOntem) { Text(stringResource(Res.string.acao_esqueci_ontem)) }
            }
        }
    }
}

@Composable
private fun BotaoGrande(
    texto: String,
    aoClicar: () -> Unit,
    tag: String,
) {
    Button(onClick = aoClicar, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag(tag)) {
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun CartaoLembrete(
    estado: TercoUiState,
    aoAtivar: () -> Unit,
    aoAjustar: () -> Unit,
    aoLiberar: () -> Unit,
) {
    val lembretes = estado.lembretes
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(Res.string.lembrete_titulo), style = MaterialTheme.typography.titleMedium)
            when {
                lembretes.ativo && !estado.permissao.notificacoesPermitidas -> {
                    Text(stringResource(Res.string.lembrete_bloqueado), style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = aoLiberar) { Text(stringResource(Res.string.acao_liberar_notificacoes)) }
                }

                lembretes.ativo -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(Res.string.lembrete_ativo_resumo, lembretes.horario.formatado()),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = aoAjustar) { Text(stringResource(Res.string.acao_ajustar)) }
                    }
                }

                else -> {
                    Text(
                        stringResource(Res.string.lembrete_inativo_resumo),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (estado.permissao.negadaAgora) {
                        Text(
                            stringResource(Res.string.lembrete_negado),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        OutlinedButton(
                            onClick = aoLiberar,
                        ) { Text(stringResource(Res.string.acao_liberar_notificacoes)) }
                    } else {
                        OutlinedButton(onClick = aoAtivar, modifier = Modifier.testTag("botao_ativar_lembrete")) {
                            Text(stringResource(Res.string.acao_ativar_lembrete))
                        }
                    }
                }
            }
        }
    }
}
