package br.app.fediaria.ui.latim

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.data.conteudo.FraseConteudo
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.latim_anterior
import br.app.fediaria.shared.resources.latim_concluir_leitura
import br.app.fediaria.shared.resources.latim_frase_de
import br.app.fediaria.shared.resources.latim_mostrar_pronuncia
import br.app.fediaria.shared.resources.latim_mostrar_traducao
import br.app.fediaria.shared.resources.latim_pronuncia_rotulo
import br.app.fediaria.shared.resources.latim_proxima
import br.app.fediaria.shared.resources.latim_resposta
import br.app.fediaria.shared.resources.latim_traducao_rotulo
import br.app.fediaria.shared.resources.latim_versiculo
import br.app.fediaria.ui.comum.LinhaComInterruptor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal val estiloLatim
    @Composable get() =
        MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif, lineHeight = 34.sp)

/** Frase em latim com ℣./℟. quando for dialogada. */
@Composable
internal fun textoLatino(frase: FraseConteudo): String =
    when (frase.papel) {
        "V" -> "${stringResource(Res.string.latim_versiculo)} ${frase.latim}"
        "R" -> "${stringResource(Res.string.latim_resposta)} ${frase.latim}"
        else -> frase.latim
    }

@Composable
fun TelaLeituraLatim(
    id: String,
    aoConcluir: () -> Unit,
    viewModel: OracaoLatimViewModel = koinViewModel(key = "oracao_$id") { parametersOf(id) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val oracao = estado.oracao ?: return
    var indice by rememberSaveable { mutableIntStateOf(0) }
    val frase = oracao.frases[indice]
    val ultima = indice == oracao.frases.lastIndex

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_leitura_latim"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LinearProgressIndicator(progress = { (indice + 1f) / oracao.frases.size }, modifier = Modifier.fillMaxWidth())
        Text(
            stringResource(Res.string.latim_frase_de, indice + 1, oracao.frases.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Column(
            Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(textoLatino(frase), style = estiloLatim, modifier = Modifier.testTag("frase_latim"))
            Rotulado(stringResource(Res.string.latim_pronuncia_rotulo)) {
                Text(
                    frase.pronuncia,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Rotulado(stringResource(Res.string.latim_traducao_rotulo)) {
                Text(frase.traducao, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
            }
            frase.nota?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.heightIn(min = 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { indice-- },
                enabled = indice > 0,
                modifier = Modifier.weight(1f).heightIn(min = 56.dp),
            ) { Text(stringResource(Res.string.latim_anterior)) }
            Button(
                onClick = { if (ultima) aoConcluir() else indice++ },
                modifier =
                    Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .testTag(if (ultima) "botao_concluir_leitura" else "botao_proxima"),
            ) { Text(stringResource(if (ultima) Res.string.latim_concluir_leitura else Res.string.latim_proxima)) }
        }
    }
}

@Composable
private fun Rotulado(
    rotulo: String,
    conteudo: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(rotulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        conteudo()
    }
}

/** A oração inteira, sem jogo nem pontos: só para rezar. */
@Composable
fun TelaRezarLatim(
    id: String,
    viewModel: OracaoLatimViewModel = koinViewModel(key = "oracao_$id") { parametersOf(id) },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val oracao = estado.oracao ?: return
    var pronuncia by rememberSaveable { mutableStateOf(true) }
    var traducao by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(vertical = 8.dp)
                .testTag("tela_rezar_latim"),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LinhaComInterruptor(
            stringResource(Res.string.latim_mostrar_pronuncia),
            pronuncia,
            { pronuncia = it },
            modifier = Modifier.testTag("interruptor_pronuncia"),
        )
        LinhaComInterruptor(
            stringResource(Res.string.latim_mostrar_traducao),
            traducao,
            { traducao = it },
            modifier = Modifier.testTag("interruptor_traducao"),
        )
        Text(
            oracao.latim,
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        oracao.frases.forEach { frase ->
            Column(
                Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(textoLatino(frase), style = estiloLatim.copy(fontSize = 22.sp, lineHeight = 31.sp))
                if (pronuncia) {
                    Text(
                        frase.pronuncia,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                if (traducao) {
                    Text(
                        frase.traducao,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
