package br.app.fediaria.ui.terco

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.ofensiva.Conquista
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.conquista_bloqueada
import br.app.fediaria.shared.resources.conquista_cem_dias
import br.app.fediaria.shared.resources.conquista_cem_dias_desc
import br.app.fediaria.shared.resources.conquista_primeiro_terco
import br.app.fediaria.shared.resources.conquista_primeiro_terco_desc
import br.app.fediaria.shared.resources.conquista_trinta_tercos
import br.app.fediaria.shared.resources.conquista_trinta_tercos_desc
import br.app.fediaria.shared.resources.conquista_um_ano
import br.app.fediaria.shared.resources.conquista_um_ano_desc
import br.app.fediaria.shared.resources.conquista_um_mes
import br.app.fediaria.shared.resources.conquista_um_mes_desc
import br.app.fediaria.shared.resources.conquista_uma_semana
import br.app.fediaria.shared.resources.conquista_uma_semana_desc
import br.app.fediaria.shared.resources.conquistas_titulo
import br.app.fediaria.shared.resources.dia_graca_desc
import br.app.fediaria.shared.resources.dia_rezado_desc
import br.app.fediaria.shared.resources.dia_vazio_desc
import br.app.fediaria.shared.resources.historico_atual
import br.app.fediaria.shared.resources.historico_mes_anterior
import br.app.fediaria.shared.resources.historico_mes_seguinte
import br.app.fediaria.shared.resources.historico_recorde
import br.app.fediaria.shared.resources.historico_total
import br.app.fediaria.shared.resources.ic_voltar
import br.app.fediaria.shared.resources.iniciais_semana
import br.app.fediaria.shared.resources.legenda_graca
import br.app.fediaria.shared.resources.legenda_hoje
import br.app.fediaria.shared.resources.legenda_rezado
import br.app.fediaria.shared.resources.mes_ano
import br.app.fediaria.shared.resources.ofensiva_explicacao_graca
import br.app.fediaria.ui.comum.TituloDeSecao
import br.app.fediaria.ui.comum.nomeDoMes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TelaHistorico(viewModel: HistoricoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val mes = estado.mes ?: return
    val hoje = estado.hoje ?: return

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(vertical = 8.dp)
                .testTag("tela_historico"),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Numero(Res.string.historico_atual, estado.ofensiva.atual, Modifier.weight(1f))
            Numero(Res.string.historico_recorde, estado.ofensiva.recorde, Modifier.weight(1f))
            Numero(Res.string.historico_total, estado.ofensiva.totalDiasRezados, Modifier.weight(1f))
        }

        Spacer(Modifier.size(16.dp))
        CabecalhoDoMes(
            mes = mes,
            podeAvancar = estado.podeAvancarMes,
            aoVoltar = viewModel::mesAnterior,
            aoAvancar = viewModel::mesSeguinte,
        )
        Calendario(mes = mes, hoje = hoje, estado = estado)
        Legenda()
        Text(
            stringResource(Res.string.ofensiva_explicacao_graca),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        TituloDeSecao(stringResource(Res.string.conquistas_titulo))
        Conquista.entries.forEach { conquista ->
            LinhaConquista(conquista, alcancada = conquista.alcancada(estado.ofensiva))
        }
    }
}

@Composable
private fun Numero(
    rotulo: StringResource,
    valor: Int,
    modifier: Modifier = Modifier,
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp).semantics(mergeDescendants = true) {},
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(stringResource(rotulo), style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun CabecalhoDoMes(
    mes: LocalDate,
    podeAvancar: Boolean,
    aoVoltar: () -> Unit,
    aoAvancar: () -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = aoVoltar) {
            Icon(painterResource(Res.drawable.ic_voltar), stringResource(Res.string.historico_mes_anterior))
        }
        Text(
            stringResource(Res.string.mes_ano, nomeDoMes(mes).replaceFirstChar { it.uppercase() }, mes.year),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        IconButton(onClick = aoAvancar, enabled = podeAvancar) {
            Icon(
                painterResource(Res.drawable.ic_voltar),
                stringResource(Res.string.historico_mes_seguinte),
                modifier = Modifier.rotate(180f),
            )
        }
    }
}

private enum class TipoDia { REZADO, GRACA, VAZIO }

@Composable
private fun Calendario(
    mes: LocalDate,
    hoje: LocalDate,
    estado: HistoricoUiState,
) {
    val iniciais = stringArrayResource(Res.array.iniciais_semana)
    val diasNoMes = mes.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
    // Calendário começando no domingo: domingo (ISO 7) na coluna 0.
    val deslocamento = mes.dayOfWeek.isoDayNumber % 7
    val celulas = List(deslocamento) { null } + (1..diasNoMes).map { LocalDate(mes.year, mes.month, it) }

    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().clearAndSetSemantics { }) {
            iniciais.forEach {
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        celulas.chunked(7).forEach { semana ->
            Row(Modifier.fillMaxWidth()) {
                semana.forEach { dia ->
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                        if (dia != null) {
                            val tipo =
                                when (dia) {
                                    in estado.dias -> TipoDia.REZADO
                                    in estado.ofensiva.diasProtegidos -> TipoDia.GRACA
                                    else -> TipoDia.VAZIO
                                }
                            Dia(dia, tipo, eHoje = dia == hoje, futuro = dia > hoje)
                        }
                    }
                }
                repeat(7 - semana.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun Dia(
    dia: LocalDate,
    tipo: TipoDia,
    eHoje: Boolean,
    futuro: Boolean,
) {
    val cores = MaterialTheme.colorScheme
    val descricao =
        stringResource(
            when (tipo) {
                TipoDia.REZADO -> Res.string.dia_rezado_desc
                TipoDia.GRACA -> Res.string.dia_graca_desc
                TipoDia.VAZIO -> Res.string.dia_vazio_desc
            },
            dia.day,
        )
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clearAndSetSemantics { contentDescription = descricao }
                .then(if (tipo == TipoDia.REZADO) Modifier.background(cores.secondary, CircleShape) else Modifier)
                .then(if (eHoje) Modifier.border(2.dp, cores.primary, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (tipo == TipoDia.GRACA) CirculoTracejado(cores.secondary)
        Text(
            dia.day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (tipo == TipoDia.REZADO) cores.onSecondary else cores.onSurface,
            modifier = if (futuro) Modifier.alpha(0.4f) else Modifier,
        )
    }
}

@Composable
private fun CirculoTracejado(cor: Color) {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = cor,
            radius = size.minDimension / 2 - 2.dp.toPx(),
            style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f))),
        )
    }
}

@Composable
private fun Legenda() {
    val cores = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemLegenda(Res.string.legenda_rezado) { Box(Modifier.size(14.dp).background(cores.secondary, CircleShape)) }
        ItemLegenda(Res.string.legenda_graca) { Box(Modifier.size(16.dp)) { CirculoTracejado(cores.secondary) } }
        ItemLegenda(Res.string.legenda_hoje) { Box(Modifier.size(14.dp).border(2.dp, cores.primary, CircleShape)) }
    }
}

@Composable
private fun ItemLegenda(
    texto: StringResource,
    marcador: @Composable () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        marcador()
        Spacer(Modifier.width(6.dp))
        Text(stringResource(texto), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun LinhaConquista(
    conquista: Conquista,
    alcancada: Boolean,
) {
    val (titulo, descricao) = conquista.textos()
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Chama(acesa = alcancada, modifier = Modifier.size(width = 24.dp, height = 32.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.alpha(if (alcancada) 1f else 0.6f)) {
            Text(stringResource(titulo), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                stringResource(if (alcancada) descricao else Res.string.conquista_bloqueada) +
                    if (alcancada) "" else " · " + stringResource(descricao),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun Conquista.textos(): Pair<StringResource, StringResource> =
    when (this) {
        Conquista.PRIMEIRO_TERCO -> Res.string.conquista_primeiro_terco to Res.string.conquista_primeiro_terco_desc
        Conquista.UMA_SEMANA -> Res.string.conquista_uma_semana to Res.string.conquista_uma_semana_desc
        Conquista.TRINTA_TERCOS -> Res.string.conquista_trinta_tercos to Res.string.conquista_trinta_tercos_desc
        Conquista.UM_MES -> Res.string.conquista_um_mes to Res.string.conquista_um_mes_desc
        Conquista.CEM_DIAS -> Res.string.conquista_cem_dias to Res.string.conquista_cem_dias_desc
        Conquista.UM_ANO -> Res.string.conquista_um_ano to Res.string.conquista_um_ano_desc
    }
