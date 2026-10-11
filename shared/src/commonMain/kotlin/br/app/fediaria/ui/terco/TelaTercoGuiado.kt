package br.app.fediaria.ui.terco

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.core.terco.Intencao
import br.app.fediaria.core.terco.OracaoTerco
import br.app.fediaria.core.terco.PassoTerco
import br.app.fediaria.core.terco.SequenciaTerco
import br.app.fediaria.data.conteudo.OracaoLatim
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_anterior
import br.app.fediaria.shared.resources.acao_concluir
import br.app.fediaria.shared.resources.acao_proximo
import br.app.fediaria.shared.resources.acao_voltar_inicio
import br.app.fediaria.shared.resources.concluido_mensagem
import br.app.fediaria.shared.resources.concluido_titulo
import br.app.fediaria.shared.resources.guiado_ave_conta
import br.app.fediaria.shared.resources.guiado_contas_descricao
import br.app.fediaria.shared.resources.guiado_contemplamos
import br.app.fediaria.shared.resources.guiado_dezena
import br.app.fediaria.shared.resources.guiado_encerramento
import br.app.fediaria.shared.resources.guiado_intencao_caridade
import br.app.fediaria.shared.resources.guiado_intencao_esperanca
import br.app.fediaria.shared.resources.guiado_intencao_fe
import br.app.fediaria.shared.resources.guiado_introducao
import br.app.fediaria.shared.resources.guiado_passo
import br.app.fediaria.shared.resources.ofensiva_dias
import br.app.fediaria.ui.comum.ordinalDoMisterio
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TelaTercoGuiado(
    conjunto: String,
    aoVoltarAoInicio: () -> Unit,
    viewModel: TercoGuiadoViewModel = koinViewModel { parametersOf(conjunto) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    if (estado.carregando) return

    if (estado.concluido) {
        TercoConcluido(ofensiva = estado.ofensivaFinal, aoVoltarAoInicio = aoVoltarAoInicio)
        return
    }

    val haptico = LocalHapticFeedback.current
    var indiceAnterior by remember { mutableIntStateOf(estado.indice) }
    LaunchedEffect(estado.indice) {
        if (estado.indice != indiceAnterior &&
            estado.vibracao
        ) {
            haptico.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
        indiceAnterior = estado.indice
    }

    Column(Modifier.fillMaxSize().testTag("tela_terco_guiado")) {
        val total = estado.passos.size
        LinearProgressIndicator(
            progress = { (estado.indice + 1f) / total },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(Res.string.guiado_passo, estado.indice + 1, total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        AnimatedContent(
            targetState = estado.indice,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.weight(1f),
            label = "passo",
        ) { indice ->
            val passo = estado.passos[indice]
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    rotuloDeContexto(passo, indice, estado.inicioDasDezenas),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                )
                contasDoPasso(passo)?.let { (preenchidas, totalContas) ->
                    Spacer(Modifier.height(12.dp))
                    Contas(preenchidas, totalContas)
                }
                Spacer(Modifier.height(20.dp))
                ConteudoDoPasso(passo, estado)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = viewModel::voltar,
                enabled = estado.indice > 0,
                modifier = Modifier.heightIn(min = 64.dp),
            ) { Text(stringResource(Res.string.acao_anterior)) }
            Button(
                onClick = viewModel::avancar,
                modifier = Modifier.weight(1f).heightIn(min = 64.dp).testTag("botao_proximo"),
            ) {
                Text(
                    stringResource(if (estado.ultimo) Res.string.acao_concluir else Res.string.acao_proximo),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun ConteudoDoPasso(
    passo: PassoTerco,
    estado: TercoGuiadoUiState,
) {
    val conteudo = estado.conteudo ?: return
    when (passo) {
        is PassoTerco.Misterio -> {
            val misterio = conteudo.misterio(passo.conjunto, passo.numero)
            Text(
                stringResource(Res.string.guiado_contemplamos),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                misterio.titulo,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                misterio.referencia,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        is PassoTerco.Oracao -> {
            val oracao = conteudo.oracao(passo.oracao.id)
            val emLatim = oracao.latim?.let { estado.latim?.oracao(it) }
            if (emLatim != null) {
                OracaoEmLatim(emLatim, oracao.titulo, estado.pronunciaLatim)
                return
            }
            Text(
                oracao.titulo,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Spacer(Modifier.height(16.dp))
            Text(
                oracao.texto,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        lineHeight = 31.sp,
                    ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** A oração em latim, frase a frase, com a pronúncia opcional e o nome em português abaixo do título. */
@Composable
private fun OracaoEmLatim(
    oracao: OracaoLatim,
    tituloEmPortugues: String,
    pronuncia: Boolean,
) {
    Text(
        oracao.latim,
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = FontFamily.Serif,
        textAlign = TextAlign.Center,
        modifier = Modifier.semantics { heading() },
    )
    Text(
        tituloEmPortugues,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))
    Column(Modifier.fillMaxWidth().testTag("oracao_em_latim"), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        oracao.frases.forEach { frase ->
            Column {
                Text(
                    frase.latim,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            lineHeight = 29.sp,
                        ),
                )
                if (pronuncia) {
                    Text(
                        frase.pronuncia,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun rotuloDeContexto(
    passo: PassoTerco,
    indice: Int,
    inicioDasDezenas: Int,
): String {
    if (passo is PassoTerco.Misterio) return stringResource(passo.conjunto.ordinalDoMisterio, passo.numero)
    val oracao = passo as PassoTerco.Oracao
    val intencao = oracao.intencao
    val dezena = oracao.dezena
    val conta = oracao.conta
    return when {
        intencao != null -> {
            stringResource(
                when (intencao) {
                    Intencao.FE -> Res.string.guiado_intencao_fe
                    Intencao.ESPERANCA -> Res.string.guiado_intencao_esperanca
                    Intencao.CARIDADE -> Res.string.guiado_intencao_caridade
                },
            )
        }

        dezena != null && oracao.oracao == OracaoTerco.AVE_MARIA && conta != null -> {
            stringResource(Res.string.guiado_dezena, dezena) + " · " +
                stringResource(Res.string.guiado_ave_conta, conta, SequenciaTerco.AVE_MARIAS_POR_DEZENA)
        }

        dezena != null -> {
            stringResource(Res.string.guiado_dezena, dezena)
        }

        indice < inicioDasDezenas -> {
            stringResource(Res.string.guiado_introducao)
        }

        else -> {
            stringResource(Res.string.guiado_encerramento)
        }
    }
}

/** Quantas contas mostrar preenchidas e o total de contas visível para o passo. */
private fun contasDoPasso(passo: PassoTerco): Pair<Int, Int>? {
    val dezena = SequenciaTerco.AVE_MARIAS_POR_DEZENA
    return when (passo) {
        is PassoTerco.Misterio -> {
            0 to dezena
        }

        is PassoTerco.Oracao -> {
            when {
                passo.intencao != null -> (passo.conta ?: 0) to Intencao.entries.size
                passo.dezena == null -> null
                passo.oracao == OracaoTerco.AVE_MARIA -> (passo.conta ?: 0) to dezena
                passo.oracao == OracaoTerco.PAI_NOSSO -> 0 to dezena
                else -> dezena to dezena
            }
        }
    }
}

@Composable
private fun Contas(
    preenchidas: Int,
    total: Int,
) {
    val descricao = stringResource(Res.string.guiado_contas_descricao, preenchidas, total)
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clearAndSetSemantics { contentDescription = descricao },
    ) {
        repeat(total) { i ->
            val atual = i == preenchidas - 1
            val cheia = i < preenchidas
            Box(
                Modifier
                    .size(if (atual) 18.dp else 12.dp)
                    .then(
                        if (cheia) {
                            Modifier.background(MaterialTheme.colorScheme.secondary, CircleShape)
                        } else {
                            Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        },
                    ),
            )
        }
    }
}

@Composable
private fun TercoConcluido(
    ofensiva: Int,
    aoVoltarAoInicio: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).testTag("terco_concluido"),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Chama(acesa = true, modifier = Modifier.size(width = 108.dp, height = 144.dp))
        Text(
            stringResource(Res.string.concluido_titulo),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            "$ofensiva " + pluralStringResource(Res.plurals.ofensiva_dias, ofensiva),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.testTag("ofensiva_conclusao"),
        )
        Text(
            stringResource(Res.string.concluido_mensagem),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = aoVoltarAoInicio,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).testTag("botao_voltar_inicio"),
        ) { Text(stringResource(Res.string.acao_voltar_inicio)) }
    }
}
