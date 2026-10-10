package br.app.fediaria.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import br.app.fediaria.plataforma.AcoesDoSistema
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_abrir_bateria
import br.app.fediaria.shared.resources.acao_abrir_notificacoes
import br.app.fediaria.shared.resources.acao_abrir_site
import br.app.fediaria.shared.resources.ajuda_atraso
import br.app.fediaria.shared.resources.ajuda_fabricante_motorola
import br.app.fediaria.shared.resources.ajuda_fabricante_outros
import br.app.fediaria.shared.resources.ajuda_fabricante_samsung
import br.app.fediaria.shared.resources.ajuda_fabricante_xiaomi
import br.app.fediaria.shared.resources.ajuda_intro
import br.app.fediaria.shared.resources.ajuda_mais_detalhes
import br.app.fediaria.shared.resources.ajuda_passo_bateria
import br.app.fediaria.shared.resources.ajuda_passo_fabricante
import br.app.fediaria.shared.resources.ajuda_passo_notificacoes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val dicasPorFabricante: List<Pair<Set<String>, StringResource>> =
    listOf(
        setOf("samsung") to Res.string.ajuda_fabricante_samsung,
        setOf("xiaomi", "redmi", "poco") to Res.string.ajuda_fabricante_xiaomi,
        setOf("motorola", "lenovo") to Res.string.ajuda_fabricante_motorola,
    )

@Composable
fun TelaAjudaLembretes(acoes: AcoesDoSistema = koinInject()) {
    val doAparelho = dicasPorFabricante.firstOrNull { (marcas, _) -> acoes.fabricante in marcas }?.second
    val dicas =
        listOfNotNull(doAparelho) + dicasPorFabricante.map { it.second }.filter { it != doAparelho } +
            Res.string.ajuda_fabricante_outros

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                ).padding(16.dp)
                .testTag("tela_ajuda_lembretes"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(Res.string.ajuda_intro), style = MaterialTheme.typography.bodyLarge)

        Text(stringResource(Res.string.ajuda_passo_notificacoes), style = MaterialTheme.typography.titleSmall)
        OutlinedButton(onClick = acoes::abrirConfiguracoesDeNotificacao, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.acao_abrir_notificacoes))
        }

        Text(stringResource(Res.string.ajuda_passo_bateria), style = MaterialTheme.typography.titleSmall)
        OutlinedButton(onClick = acoes::abrirConfiguracoesDeBateria, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.acao_abrir_bateria))
        }

        Text(stringResource(Res.string.ajuda_passo_fabricante), style = MaterialTheme.typography.titleSmall)
        dicas.forEachIndexed { i, dica ->
            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (i == 0 && doAparelho != null) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            },
                    ),
            ) {
                Text(
                    stringResource(dica),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        Text(
            stringResource(Res.string.ajuda_atraso),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(stringResource(Res.string.ajuda_mais_detalhes), style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = { acoes.abrirNoNavegador("https://dontkillmyapp.com") }) {
            Text(stringResource(Res.string.acao_abrir_site))
        }
    }
}
