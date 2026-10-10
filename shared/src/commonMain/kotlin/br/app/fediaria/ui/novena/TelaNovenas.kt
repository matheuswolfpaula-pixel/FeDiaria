package br.app.fediaria.ui.novena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.novena.CalculadoraNovena
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.novena_comeca_em
import br.app.fediaria.shared.resources.novena_concluida_em
import br.app.fediaria.shared.resources.novena_em_andamento
import br.app.fediaria.shared.resources.novena_progresso_desc
import br.app.fediaria.shared.resources.novena_proxima_janela
import br.app.fediaria.shared.resources.novena_rezado_hoje_curto
import br.app.fediaria.shared.resources.novena_rezar_hoje
import br.app.fediaria.shared.resources.novenas_catalogo
import br.app.fediaria.shared.resources.novenas_concluidas
import br.app.fediaria.shared.resources.novenas_em_andamento
import br.app.fediaria.shared.resources.novenas_vazio
import br.app.fediaria.ui.comum.TituloDeSecao
import br.app.fediaria.ui.comum.dataCurta
import br.app.fediaria.ui.comum.periodo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TelaNovenas(
    aoAbrirNovenaAtiva: (Long) -> Unit,
    aoAbrirDetalhe: (String) -> Unit,
    viewModel: NovenasViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
                .testTag("tela_novenas"),
    ) {
        if (estado.carregando) return@Column

        TituloDeSecao(stringResource(Res.string.novenas_em_andamento))
        if (estado.emAndamento.isEmpty()) {
            Text(
                stringResource(Res.string.novenas_vazio),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        estado.emAndamento.forEach { item ->
            CartaoNovena(aoClicar = { aoAbrirNovenaAtiva(item.usuario.id) }) {
                Text(item.novena.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    textoDeSituacao(item),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (item.usuario.estado.pendenteHoje) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    fontWeight = if (item.usuario.estado.pendenteHoje) FontWeight.SemiBold else null,
                )
                PontosDaNovena(item.usuario.estado.diasRezados, Modifier.padding(top = 4.dp))
            }
        }

        TituloDeSecao(stringResource(Res.string.novenas_catalogo))
        estado.catalogo.forEach { item ->
            CartaoNovena(
                aoClicar = { item.idEmAndamento?.let(aoAbrirNovenaAtiva) ?: aoAbrirDetalhe(item.novena.id) },
                modifier = Modifier.testTag("catalogo_${item.novena.id}"),
            ) {
                Text(item.novena.nome, style = MaterialTheme.typography.titleMedium)
                Text(
                    item.novena.festa,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val fim = item.proximoInicio.plus(CalculadoraNovena.DIAS - 1, DateTimeUnit.DAY)
                Text(
                    if (item.idEmAndamento != null) {
                        stringResource(Res.string.novena_em_andamento)
                    } else {
                        stringResource(Res.string.novena_proxima_janela, periodo(item.proximoInicio, fim))
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        if (estado.concluidas.isNotEmpty()) {
            TituloDeSecao(stringResource(Res.string.novenas_concluidas))
            estado.concluidas.forEach { item ->
                CartaoNovena(aoClicar = { aoAbrirNovenaAtiva(item.usuario.id) }) {
                    Text(item.novena.nome, style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(Res.string.novena_concluida_em, dataCurta(item.usuario.estado.previsaoTermino)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun textoDeSituacao(item: NovenaComConteudo): String {
    val estado = item.usuario.estado
    return when {
        estado.situacao == SituacaoNovena.AGENDADA -> {
            stringResource(
                Res.string.novena_comeca_em,
                dataCurta(item.usuario.inicio),
            )
        }

        estado.rezouHoje -> {
            stringResource(Res.string.novena_rezado_hoje_curto, estado.diasRezados)
        }

        else -> {
            stringResource(Res.string.novena_rezar_hoje, estado.diaAtual ?: CalculadoraNovena.DIAS)
        }
    }
}

@Composable
private fun CartaoNovena(
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier,
    conteudo: @Composable () -> Unit,
) {
    Card(
        onClick = aoClicar,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) { conteudo() }
    }
}

/** Nove contas: preenchidas as já rezadas. */
@Composable
fun PontosDaNovena(
    rezados: Int,
    modifier: Modifier = Modifier,
) {
    val descricao = stringResource(Res.string.novena_progresso_desc, rezados)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = descricao },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(CalculadoraNovena.DIAS) { i ->
            Box(
                Modifier
                    .size(10.dp)
                    .then(
                        if (i < rezados) {
                            Modifier.background(MaterialTheme.colorScheme.secondary, CircleShape)
                        } else {
                            Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        },
                    ),
            )
        }
    }
}
