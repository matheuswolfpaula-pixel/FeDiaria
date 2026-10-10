package br.app.fediaria.ui.novena

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.novena.CalculadoraNovena
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.data.conteudo.OracaoNovena
import br.app.fediaria.data.novena.NovenaDoUsuario
import br.app.fediaria.plataforma.lembrarPedidoDePermissaoDeNotificacao
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_cancelar
import br.app.fediaria.shared.resources.config_horario
import br.app.fediaria.shared.resources.novena_desfazer
import br.app.fediaria.shared.resources.novena_dia_de
import br.app.fediaria.shared.resources.novena_encerrar
import br.app.fediaria.shared.resources.novena_encerrar_texto
import br.app.fediaria.shared.resources.novena_encerrar_titulo
import br.app.fediaria.shared.resources.novena_lembrete
import br.app.fediaria.shared.resources.novena_lembrete_desc
import br.app.fediaria.shared.resources.novena_marcar_dia
import br.app.fediaria.shared.resources.novena_oracao_do_dia
import br.app.fediaria.shared.resources.novena_oracoes_diarias
import br.app.fediaria.shared.resources.novena_previsao
import br.app.fediaria.shared.resources.novena_remover
import br.app.fediaria.shared.resources.novena_retomada
import br.app.fediaria.shared.resources.novena_status_agendada
import br.app.fediaria.shared.resources.novena_status_concluida
import br.app.fediaria.shared.resources.novena_status_pendente
import br.app.fediaria.shared.resources.novena_status_rezado
import br.app.fediaria.ui.comum.DialogoDeHorario
import br.app.fediaria.ui.comum.LinhaComInterruptor
import br.app.fediaria.ui.comum.TituloDeSecao
import br.app.fediaria.ui.comum.dataCurta
import br.app.fediaria.ui.comum.dataPorExtenso
import br.app.fediaria.ui.comum.formatado
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val estiloOracao
    @Composable get() =
        MaterialTheme.typography.bodyLarge.copy(
            fontFamily = FontFamily.Serif,
            fontSize = 19.sp,
            lineHeight = 29.sp,
        )

@Composable
fun TelaNovenaAtiva(
    id: Long,
    aoSair: () -> Unit,
    viewModel: NovenaAtivaViewModel = koinViewModel { parametersOf(id) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.removida) { if (estado.removida && !estado.carregando) aoSair() }
    var confirmandoEncerrar by remember { mutableStateOf(false) }

    val usuario = estado.usuario ?: return
    val novena = estado.novena ?: return
    val terco = estado.terco ?: return
    val situacao = usuario.estado.situacao
    val dia = novena.dia(estado.diaExibido)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_novena_ativa"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(novena.nome, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(Res.string.novena_dia_de, estado.diaExibido, CalculadoraNovena.DIAS),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            PontosDaNovena(usuario.estado.diasRezados)
        }

        CartaoDeSituacao(usuario, retomada = estado.retomada, aoDesfazer = viewModel::desfazerHoje)

        BlocoDeOracao(novena.oracaoInicial)

        TituloDeSecao(
            stringResource(Res.string.novena_oracao_do_dia, dia.dia, dia.tema),
            recuo = 0.dp,
        )
        Text(
            dia.referencia,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(dia.meditacao, style = estiloOracao)

        TituloDeSecao(stringResource(Res.string.novena_oracoes_diarias), recuo = 0.dp)
        novena.oracoesDiarias.forEach { idOracao ->
            val oracao = terco.oracao(idOracao)
            Text(oracao.titulo, style = MaterialTheme.typography.titleSmall)
            Text(oracao.texto, style = estiloOracao)
        }

        BlocoDeOracao(novena.oracaoFinal)
        BlocoDeOracao(novena.jaculatoria)

        if (CalculadoraNovena.podeRezar(estado.diaExibido, usuario.estado)) {
            Button(
                onClick = viewModel::rezarDiaAtual,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_rezar_dia"),
            ) {
                Text(
                    stringResource(Res.string.novena_marcar_dia, estado.diaExibido),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        if (situacao != SituacaoNovena.CONCLUIDA) {
            AjusteDeLembrete(usuario, aoMudar = viewModel::definirLembrete)
        }
        TextButton(onClick = { confirmandoEncerrar = true }) {
            Text(
                stringResource(
                    if (situacao ==
                        SituacaoNovena.CONCLUIDA
                    ) {
                        Res.string.novena_remover
                    } else {
                        Res.string.novena_encerrar
                    },
                ),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }

    if (confirmandoEncerrar) {
        AlertDialog(
            onDismissRequest = { confirmandoEncerrar = false },
            title = { Text(stringResource(Res.string.novena_encerrar_titulo)) },
            text = { Text(stringResource(Res.string.novena_encerrar_texto)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmandoEncerrar = false
                    viewModel.remover()
                }) { Text(stringResource(Res.string.novena_encerrar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmandoEncerrar = false }) { Text(stringResource(Res.string.acao_cancelar)) }
            },
        )
    }
}

@Composable
private fun CartaoDeSituacao(
    usuario: NovenaDoUsuario,
    retomada: Boolean,
    aoDesfazer: () -> Unit,
) {
    val estado = usuario.estado
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(Modifier.padding(16.dp).testTag("status_novena"), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                when {
                    estado.situacao == SituacaoNovena.CONCLUIDA -> {
                        stringResource(Res.string.novena_status_concluida)
                    }

                    estado.situacao == SituacaoNovena.AGENDADA -> {
                        stringResource(Res.string.novena_status_agendada, dataPorExtenso(usuario.inicio))
                    }

                    estado.rezouHoje -> {
                        stringResource(Res.string.novena_status_rezado, estado.diasRezados)
                    }

                    else -> {
                        stringResource(Res.string.novena_status_pendente, estado.diaAtual ?: 1)
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (estado.situacao != SituacaoNovena.CONCLUIDA) {
                Text(
                    stringResource(Res.string.novena_previsao, dataCurta(estado.previsaoTermino)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            if (retomada) {
                Text(
                    stringResource(Res.string.novena_retomada),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            if (estado.rezouHoje && estado.situacao != SituacaoNovena.CONCLUIDA) {
                TextButton(onClick = aoDesfazer, modifier = Modifier.testTag("botao_desfazer_novena")) {
                    Text(stringResource(Res.string.novena_desfazer))
                }
            }
        }
    }
}

@Composable
private fun BlocoDeOracao(oracao: OracaoNovena) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(oracao.titulo, style = MaterialTheme.typography.titleSmall)
        Text(oracao.texto, style = estiloOracao)
    }
}

@Composable
private fun AjusteDeLembrete(
    usuario: NovenaDoUsuario,
    aoMudar: (Boolean, kotlinx.datetime.LocalTime) -> Unit,
) {
    var editando by remember { mutableStateOf(false) }
    val pedirPermissao =
        lembrarPedidoDePermissaoDeNotificacao { permitido ->
            if (permitido) aoMudar(true, usuario.horarioLembrete)
        }

    LinhaComInterruptor(
        titulo = stringResource(Res.string.novena_lembrete),
        descricao = stringResource(Res.string.novena_lembrete_desc),
        marcado = usuario.lembreteAtivo,
        aoMudar = { ligar -> if (ligar) pedirPermissao() else aoMudar(false, usuario.horarioLembrete) },
        recuo = 0.dp,
    )
    if (usuario.lembreteAtivo) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable { editando = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(Res.string.config_horario),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                usuario.horarioLembrete.formatado(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
    if (editando) {
        DialogoDeHorario(
            inicial = usuario.horarioLembrete,
            aoConfirmar = {
                aoMudar(true, it)
                editando = false
            },
            aoCancelar = { editando = false },
        )
    }
}
