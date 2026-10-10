package br.app.fediaria.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.TemaPreferido
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.configuracoes_tema
import br.app.fediaria.shared.resources.tema_claro
import br.app.fediaria.shared.resources.tema_escuro
import br.app.fediaria.shared.resources.tema_sistema
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

class ConfiguracoesViewModel(
    private val configuracoes: ConfiguracoesRepository,
) : ViewModel() {
    val tema: StateFlow<TemaPreferido> =
        configuracoes.tema.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TemaPreferido.SISTEMA)

    fun definirTema(tema: TemaPreferido) {
        viewModelScope.launch { configuracoes.definirTema(tema) }
    }
}

@Composable
fun TelaConfiguracoes(viewModel: ConfiguracoesViewModel = koinViewModel()) {
    val tema by viewModel.tema.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp).testTag("tela_configuracoes")) {
        Text(
            stringResource(Res.string.configuracoes_tema),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).semantics { heading() },
        )
        Column(Modifier.selectableGroup()) {
            TemaPreferido.entries.forEach { opcao ->
                OpcaoDeTema(
                    rotulo = stringResource(opcao.rotulo()),
                    selecionada = opcao == tema,
                    aoSelecionar = { viewModel.definirTema(opcao) },
                )
            }
        }
    }
}

@Composable
private fun OpcaoDeTema(
    rotulo: String,
    selecionada: Boolean,
    aoSelecionar: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .selectable(selected = selecionada, onClick = aoSelecionar, role = Role.RadioButton)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selecionada, onClick = null)
        Text(rotulo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
    }
}

private fun TemaPreferido.rotulo() =
    when (this) {
        TemaPreferido.SISTEMA -> Res.string.tema_sistema
        TemaPreferido.CLARO -> Res.string.tema_claro
        TemaPreferido.ESCURO -> Res.string.tema_escuro
    }
