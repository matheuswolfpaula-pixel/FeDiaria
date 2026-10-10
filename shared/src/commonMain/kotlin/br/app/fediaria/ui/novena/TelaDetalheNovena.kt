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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.plataforma.lembrarPedidoDePermissaoDeNotificacao
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.config_horario
import br.app.fediaria.shared.resources.novena_comecar_em
import br.app.fediaria.shared.resources.novena_comecar_hoje
import br.app.fediaria.shared.resources.novena_continuar
import br.app.fediaria.shared.resources.novena_ja_em_andamento
import br.app.fediaria.shared.resources.novena_janela_em_curso
import br.app.fediaria.shared.resources.novena_janela_explicacao
import br.app.fediaria.shared.resources.novena_lembrete
import br.app.fediaria.shared.resources.novena_lembrete_desc
import br.app.fediaria.ui.comum.DialogoDeHorario
import br.app.fediaria.ui.comum.LinhaComInterruptor
import br.app.fediaria.ui.comum.dataCurta
import br.app.fediaria.ui.comum.formatado
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaDetalheNovena(
    novenaId: String,
    aoAbrirNovenaAtiva: (Long) -> Unit,
    viewModel: DetalheNovenaViewModel = koinViewModel { parametersOf(novenaId) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.iniciadaId) { estado.iniciadaId?.let(aoAbrirNovenaAtiva) }

    var dataEscolhida by remember { mutableStateOf<LocalDate?>(null) }
    var editandoHorario by remember { mutableStateOf(false) }
    val pedirPermissao =
        lembrarPedidoDePermissaoDeNotificacao { permitido ->
            dataEscolhida?.let { viewModel.iniciar(it, permitido) }
        }
    val comecar: (LocalDate) -> Unit = { data ->
        dataEscolhida = data
        if (estado.lembreteAtivo) pedirPermissao() else viewModel.iniciar(data, lembretePermitido = false)
    }

    val novena = estado.novena ?: return
    val hoje = estado.hoje ?: return
    val proximoInicio = estado.proximoInicio ?: hoje

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_detalhe_novena"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(novena.nome, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.semantics { heading() })
        Text(novena.festa, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Text(novena.descricao, style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(
                if (proximoInicio < hoje) Res.string.novena_janela_em_curso else Res.string.novena_janela_explicacao,
                dataCurta(proximoInicio),
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        val idEmAndamento = estado.idEmAndamento
        if (idEmAndamento != null) {
            Text(stringResource(Res.string.novena_ja_em_andamento), style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { aoAbrirNovenaAtiva(idEmAndamento) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            ) {
                Text(stringResource(Res.string.novena_continuar))
            }
            return@Column
        }

        LinhaComInterruptor(
            titulo = stringResource(Res.string.novena_lembrete),
            descricao = stringResource(Res.string.novena_lembrete_desc),
            marcado = estado.lembreteAtivo,
            aoMudar = viewModel::definirLembrete,
            recuo = 0.dp,
        )
        if (estado.lembreteAtivo) {
            Row(
                Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable { editandoHorario = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(Res.string.config_horario),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    estado.horario.formatado(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Button(
            onClick = { comecar(hoje) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_comecar_hoje"),
        ) { Text(stringResource(Res.string.novena_comecar_hoje), style = MaterialTheme.typography.titleMedium) }
        if (proximoInicio > hoje) {
            OutlinedButton(
                onClick = { comecar(proximoInicio) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
                Text(stringResource(Res.string.novena_comecar_em, dataCurta(proximoInicio)))
            }
        }
    }

    if (editandoHorario) {
        DialogoDeHorario(
            inicial = estado.horario,
            aoConfirmar = {
                viewModel.definirHorario(it)
                editandoHorario = false
            },
            aoCancelar = { editandoHorario = false },
        )
    }
}
