package br.app.fediaria.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.ic_cadeado
import br.app.fediaria.shared.resources.ic_certo
import br.app.fediaria.shared.resources.quiz_licao_fechada
import br.app.fediaria.shared.resources.quiz_licao_feita
import br.app.fediaria.shared.resources.quiz_licao_n
import br.app.fediaria.shared.resources.quiz_licao_perguntas
import br.app.fediaria.shared.resources.quiz_licoes_feitas
import br.app.fediaria.shared.resources.quiz_nivel_1
import br.app.fediaria.shared.resources.quiz_nivel_2
import br.app.fediaria.shared.resources.quiz_nivel_3
import br.app.fediaria.shared.resources.quiz_nivel_fechado
import br.app.fediaria.shared.resources.quiz_revisao_desc
import br.app.fediaria.shared.resources.quiz_revisao_titulo
import br.app.fediaria.shared.resources.quiz_xp_desc
import br.app.fediaria.shared.resources.quiz_xp_total
import br.app.fediaria.ui.comum.TituloDeSecao
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TelaQuiz(
    aoAbrirLicao: (String) -> Unit,
    viewModel: QuizViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
                .testTag("tela_quiz"),
    ) {
        if (estado.carregando) return@Column

        Placar(estado.xpTotal, estado.licoesFeitas)

        if (estado.revisaoPendente > 0) {
            Card(
                onClick = { aoAbrirLicao(br.app.fediaria.core.quiz.MontadorDeLicoes.ID_REVISAO) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ).testTag("cartao_revisao"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(Res.string.quiz_revisao_titulo), style = MaterialTheme.typography.titleMedium)
                    Text(
                        pluralStringResource(
                            Res.plurals.quiz_revisao_desc,
                            estado.revisaoPendente,
                            estado.revisaoPendente,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        estado.trilhas.forEach { trilha ->
            TituloDeSecao(trilha.trilha.nome)
            Text(
                trilha.trilha.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            trilha.niveis.forEach { nivel -> BlocoDoNivel(nivel, aoAbrirLicao) }
        }
    }
}

@Composable
private fun Placar(
    xp: Int,
    licoes: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(Res.string.quiz_xp_total, xp),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag("quiz_xp"),
                )
                Text(
                    pluralStringResource(Res.plurals.quiz_licoes_feitas, licoes, licoes),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Text(stringResource(Res.string.quiz_xp_desc), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BlocoDoNivel(
    nivel: NivelUi,
    aoAbrirLicao: (String) -> Unit,
) {
    val titulo =
        when (nivel.nivel) {
            1 -> Res.string.quiz_nivel_1
            2 -> Res.string.quiz_nivel_2
            else -> Res.string.quiz_nivel_3
        }
    Text(
        stringResource(titulo),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
    )
    if (!nivel.aberto) {
        Text(
            stringResource(Res.string.quiz_nivel_fechado),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
    nivel.licoes.forEach { item -> LinhaDaLicao(item, nivel.aberto) { aoAbrirLicao(item.licao.id) } }
}

@Composable
private fun LinhaDaLicao(
    item: LicaoUi,
    aberta: Boolean,
    aoClicar: () -> Unit,
) {
    val nome = stringResource(Res.string.quiz_licao_n, item.licao.numero)
    val perguntas =
        pluralStringResource(Res.plurals.quiz_licao_perguntas, item.licao.perguntas.size, item.licao.perguntas.size)
    val situacao =
        when {
            !aberta -> stringResource(Res.string.quiz_licao_fechada)
            item.feita -> stringResource(Res.string.quiz_licao_feita)
            else -> null
        }
    Card(
        onClick = aoClicar,
        enabled = aberta,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 3.dp)
                .testTag("licao_${item.licao.id}")
                .semantics { contentDescription = listOfNotNull(nome, perguntas, situacao).joinToString(", ") },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            Modifier.heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(nome, style = MaterialTheme.typography.titleSmall)
                Text(
                    perguntas,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            when {
                !aberta -> {
                    Icon(
                        painterResource(Res.drawable.ic_cadeado),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp),
                    )
                }

                item.feita -> {
                    Icon(
                        painterResource(Res.drawable.ic_certo),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
