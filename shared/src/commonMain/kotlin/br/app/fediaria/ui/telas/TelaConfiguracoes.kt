package br.app.fediaria.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.terco.EsquemaMisterios
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfigTerco
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.TemaPreferido
import br.app.fediaria.plataforma.Lembretes
import br.app.fediaria.plataforma.lembrarPedidoDePermissaoDeNotificacao
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_liberar_notificacoes
import br.app.fediaria.shared.resources.config_agradecimento
import br.app.fediaria.shared.resources.config_ajuda_lembretes
import br.app.fediaria.shared.resources.config_esquema
import br.app.fediaria.shared.resources.config_horario
import br.app.fediaria.shared.resources.config_horario_segundo
import br.app.fediaria.shared.resources.config_lembrete_diario
import br.app.fediaria.shared.resources.config_oferecimento
import br.app.fediaria.shared.resources.config_segundo_lembrete
import br.app.fediaria.shared.resources.config_segundo_lembrete_desc
import br.app.fediaria.shared.resources.config_vibracao
import br.app.fediaria.shared.resources.configuracoes_lembretes
import br.app.fediaria.shared.resources.configuracoes_tema
import br.app.fediaria.shared.resources.configuracoes_terco
import br.app.fediaria.shared.resources.esquema_padrao
import br.app.fediaria.shared.resources.esquema_padrao_desc
import br.app.fediaria.shared.resources.esquema_tradicional
import br.app.fediaria.shared.resources.esquema_tradicional_desc
import br.app.fediaria.shared.resources.lembrete_bloqueado
import br.app.fediaria.shared.resources.lembrete_negado
import br.app.fediaria.shared.resources.tema_claro
import br.app.fediaria.shared.resources.tema_escuro
import br.app.fediaria.shared.resources.tema_sistema
import br.app.fediaria.ui.comum.DialogoDeHorario
import br.app.fediaria.ui.comum.LinhaComInterruptor
import br.app.fediaria.ui.comum.TituloDeSecao
import br.app.fediaria.ui.comum.formatado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

data class ConfiguracoesUiState(
    val tema: TemaPreferido = TemaPreferido.SISTEMA,
    val lembretes: ConfigLembretes = ConfigLembretes(),
    val terco: ConfigTerco = ConfigTerco(),
    val notificacoesPermitidas: Boolean = true,
    val permissaoNegada: Boolean = false,
)

class ConfiguracoesViewModel(
    private val configuracoes: ConfiguracoesRepository,
    private val lembretes: Lembretes,
) : ViewModel() {
    private val permitidas = MutableStateFlow(lembretes.notificacoesPermitidas())
    private val negada = MutableStateFlow(false)

    val estado: StateFlow<ConfiguracoesUiState> =
        combine(configuracoes.tema, configuracoes.lembretes, configuracoes.terco, permitidas, negada) { t, l, c, p, n ->
            ConfiguracoesUiState(t, l, c, p, n)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConfiguracoesUiState())

    fun definirTema(tema: TemaPreferido) = viewModelScope.launch { configuracoes.definirTema(tema) }

    fun atualizarPermissao() {
        permitidas.value = lembretes.notificacoesPermitidas()
    }

    /** Ligar exige permissão: a tela pede antes e chama [aoResponderPermissao]. */
    fun desativarLembretes() = alterarLembretes { it.copy(ativo = false) }

    fun aoResponderPermissao(concedida: Boolean) {
        negada.value = !concedida
        atualizarPermissao()
        if (concedida) alterarLembretes { it.copy(ativo = true) }
    }

    fun definirHorario(horario: LocalTime) = alterarLembretes { it.copy(horario = horario) }

    fun definirSegundoAtivo(ativo: Boolean) = alterarLembretes { it.copy(segundoAtivo = ativo) }

    fun definirHorarioSegundo(horario: LocalTime) = alterarLembretes { it.copy(horarioSegundo = horario) }

    fun alterarTerco(alteracao: (ConfigTerco) -> ConfigTerco) =
        viewModelScope.launch { configuracoes.definirTerco(alteracao(estado.value.terco)) }

    private fun alterarLembretes(alteracao: (ConfigLembretes) -> ConfigLembretes) =
        viewModelScope.launch {
            configuracoes.definirLembretes(alteracao(configuracoes.lembretesAtuais()))
            lembretes.reagendar()
        }
}

private enum class HorarioEditado { PRINCIPAL, SEGUNDO }

@Composable
fun TelaConfiguracoes(
    aoAbrirAjudaLembretes: () -> Unit,
    aoLiberarNotificacoes: () -> Unit,
    viewModel: ConfiguracoesViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.atualizarPermissao()
        onPauseOrDispose { }
    }
    val pedirPermissao = lembrarPedidoDePermissaoDeNotificacao(viewModel::aoResponderPermissao)
    var editando by remember { mutableStateOf<HorarioEditado?>(null) }
    val lembretes = estado.lembretes

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
                .testTag("tela_configuracoes"),
    ) {
        TituloDeSecao(stringResource(Res.string.configuracoes_lembretes))
        LinhaComInterruptor(
            titulo = stringResource(Res.string.config_lembrete_diario),
            marcado = lembretes.ativo,
            aoMudar = { ligar -> if (ligar) pedirPermissao() else viewModel.desativarLembretes() },
            modifier = Modifier.testTag("interruptor_lembrete"),
        )
        if (estado.permissaoNegada || (lembretes.ativo && !estado.notificacoesPermitidas)) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(
                    stringResource(if (lembretes.ativo) Res.string.lembrete_bloqueado else Res.string.lembrete_negado),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                OutlinedButton(
                    onClick = aoLiberarNotificacoes,
                ) { Text(stringResource(Res.string.acao_liberar_notificacoes)) }
            }
        }
        LinhaDeValor(
            titulo = stringResource(Res.string.config_horario),
            valor = lembretes.horario.formatado(),
            habilitado = lembretes.ativo,
            aoClicar = { editando = HorarioEditado.PRINCIPAL },
        )
        LinhaComInterruptor(
            titulo = stringResource(Res.string.config_segundo_lembrete),
            descricao = stringResource(Res.string.config_segundo_lembrete_desc),
            marcado = lembretes.segundoAtivo,
            habilitado = lembretes.ativo,
            aoMudar = viewModel::definirSegundoAtivo,
        )
        LinhaDeValor(
            titulo = stringResource(Res.string.config_horario_segundo),
            valor = lembretes.horarioSegundo.formatado(),
            habilitado = lembretes.ativo && lembretes.segundoAtivo,
            aoClicar = { editando = HorarioEditado.SEGUNDO },
        )
        LinhaDeValor(
            titulo = stringResource(Res.string.config_ajuda_lembretes),
            valor = "",
            aoClicar = aoAbrirAjudaLembretes,
        )

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        TituloDeSecao(stringResource(Res.string.configuracoes_terco))
        Text(
            stringResource(Res.string.config_esquema),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Column(Modifier.selectableGroup()) {
            EsquemaMisterios.entries.forEach { esquema ->
                OpcaoUnica(
                    rotulo =
                        stringResource(
                            if (esquema ==
                                EsquemaMisterios.PADRAO
                            ) {
                                Res.string.esquema_padrao
                            } else {
                                Res.string.esquema_tradicional
                            },
                        ),
                    descricao =
                        stringResource(
                            if (esquema == EsquemaMisterios.PADRAO) {
                                Res.string.esquema_padrao_desc
                            } else {
                                Res.string.esquema_tradicional_desc
                            },
                        ),
                    selecionada = estado.terco.esquema == esquema,
                    aoSelecionar = { viewModel.alterarTerco { it.copy(esquema = esquema) } },
                )
            }
        }
        LinhaComInterruptor(
            titulo = stringResource(Res.string.config_oferecimento),
            marcado = estado.terco.oferecimento,
            aoMudar = { v -> viewModel.alterarTerco { it.copy(oferecimento = v) } },
        )
        LinhaComInterruptor(
            titulo = stringResource(Res.string.config_agradecimento),
            marcado = estado.terco.agradecimento,
            aoMudar = { v -> viewModel.alterarTerco { it.copy(agradecimento = v) } },
        )
        LinhaComInterruptor(
            titulo = stringResource(Res.string.config_vibracao),
            marcado = estado.terco.vibracao,
            aoMudar = { v -> viewModel.alterarTerco { it.copy(vibracao = v) } },
        )

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        TituloDeSecao(stringResource(Res.string.configuracoes_tema))
        Column(Modifier.selectableGroup()) {
            TemaPreferido.entries.forEach { opcao ->
                OpcaoUnica(
                    rotulo = stringResource(opcao.rotulo()),
                    selecionada = opcao == estado.tema,
                    aoSelecionar = { viewModel.definirTema(opcao) },
                )
            }
        }
    }

    when (editando) {
        HorarioEditado.PRINCIPAL -> {
            DialogoDeHorario(
                inicial = lembretes.horario,
                aoConfirmar = {
                    viewModel.definirHorario(it)
                    editando = null
                },
                aoCancelar = { editando = null },
            )
        }

        HorarioEditado.SEGUNDO -> {
            DialogoDeHorario(
                inicial = lembretes.horarioSegundo,
                aoConfirmar = {
                    viewModel.definirHorarioSegundo(it)
                    editando = null
                },
                aoCancelar = { editando = null },
            )
        }

        null -> {
            // Nenhum diálogo aberto.
        }
    }
}

@Composable
private fun LinhaDeValor(
    titulo: String,
    valor: String,
    aoClicar: () -> Unit,
    habilitado: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(enabled = habilitado, onClick = aoClicar)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val cor = if (habilitado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        Text(titulo, style = MaterialTheme.typography.bodyLarge, color = cor, modifier = Modifier.weight(1f))
        Text(
            valor,
            style = MaterialTheme.typography.titleMedium,
            color = if (habilitado) MaterialTheme.colorScheme.primary else cor,
        )
    }
}

@Composable
private fun OpcaoUnica(
    rotulo: String,
    selecionada: Boolean,
    aoSelecionar: () -> Unit,
    descricao: String? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .selectable(selected = selecionada, onClick = aoSelecionar, role = Role.RadioButton)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selecionada, onClick = null)
        Column(Modifier.padding(start = 16.dp)) {
            Text(rotulo, style = MaterialTheme.typography.bodyLarge)
            if (descricao != null) {
                Text(
                    descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun TemaPreferido.rotulo() =
    when (this) {
        TemaPreferido.SISTEMA -> Res.string.tema_sistema
        TemaPreferido.CLARO -> Res.string.tema_claro
        TemaPreferido.ESCURO -> Res.string.tema_escuro
    }
