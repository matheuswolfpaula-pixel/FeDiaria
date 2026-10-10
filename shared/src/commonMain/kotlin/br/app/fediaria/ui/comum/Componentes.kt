package br.app.fediaria.ui.comum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_cancelar
import br.app.fediaria.shared.resources.acao_ok
import br.app.fediaria.shared.resources.seletor_horario_titulo
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun TituloDeSecao(
    texto: String,
    modifier: Modifier = Modifier,
    recuo: Dp = 16.dp,
) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = recuo, vertical = 12.dp).semantics { heading() },
    )
}

/** Linha inteira clicável com interruptor (um só alvo de toque para o TalkBack). */
@Composable
fun LinhaComInterruptor(
    titulo: String,
    marcado: Boolean,
    aoMudar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    descricao: String? = null,
    habilitado: Boolean = true,
    recuo: Dp = 16.dp,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .toggleable(value = marcado, enabled = habilitado, role = Role.Switch, onValueChange = aoMudar)
                .padding(horizontal = recuo, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (descricao != null) {
                Text(
                    descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = marcado,
            onCheckedChange = null,
            enabled = habilitado,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDeHorario(
    inicial: LocalTime,
    aoConfirmar: (LocalTime) -> Unit,
    aoCancelar: () -> Unit,
) {
    val estado = rememberTimePickerState(initialHour = inicial.hour, initialMinute = inicial.minute, is24Hour = true)
    AlertDialog(
        onDismissRequest = aoCancelar,
        title = { Text(stringResource(Res.string.seletor_horario_titulo)) },
        text = { TimePicker(state = estado) },
        confirmButton = {
            TextButton(onClick = { aoConfirmar(LocalTime(estado.hour, estado.minute)) }) {
                Text(stringResource(Res.string.acao_ok))
            }
        },
        dismissButton = { TextButton(onClick = aoCancelar) { Text(stringResource(Res.string.acao_cancelar)) } },
    )
}
