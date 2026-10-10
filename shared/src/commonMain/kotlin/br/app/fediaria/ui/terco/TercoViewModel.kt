package br.app.fediaria.ui.terco

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.ofensiva.EstadoOfensiva
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.core.terco.SequenciaTerco
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.ProgressoTerco
import br.app.fediaria.data.config.ProgressoTercoRepository
import br.app.fediaria.data.db.OrigemRegistro
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

data class PermissaoUi(
    val notificacoesPermitidas: Boolean = true,
    val negadaAgora: Boolean = false,
)

data class TercoUiState(
    val carregando: Boolean = true,
    val hoje: LocalDate? = null,
    val ofensiva: EstadoOfensiva = EstadoOfensiva.VAZIO,
    val conjuntoDoDia: ConjuntoMisterios = ConjuntoMisterios.GOZOSOS,
    val hojeFoiManual: Boolean = false,
    val ontem: LocalDate? = null,
    val ontemRezado: Boolean = true,
    val progresso: ProgressoTerco? = null,
    val totalPassosProgresso: Int = 0,
    val lembretes: ConfigLembretes = ConfigLembretes(),
    val permissao: PermissaoUi = PermissaoUi(),
)

class TercoViewModel(
    private val dias: DiasRezadosRepository,
    private val configuracoes: ConfiguracoesRepository,
    private val progressos: ProgressoTercoRepository,
    private val lembretes: Lembretes,
    private val relogio: Relogio,
) : ViewModel() {
    private val permissao = MutableStateFlow(PermissaoUi(lembretes.notificacoesPermitidas()))

    val estado: StateFlow<TercoUiState> =
        combine(
            dias.historico,
            configuracoes.terco,
            progressos.progresso,
            configuracoes.lembretes,
            permissao,
        ) { historico, configTerco, progresso, configLembretes, permissao ->
            val ontem = historico.hoje.minus(1, DateTimeUnit.DAY)
            val progressoValido = progresso?.takeIf { it.valido(relogio.agora()) }
            TercoUiState(
                carregando = false,
                hoje = historico.hoje,
                ofensiva = historico.ofensiva,
                conjuntoDoDia = configTerco.esquema.conjuntoDoDia(historico.hoje),
                hojeFoiManual = historico.dias[historico.hoje] == OrigemRegistro.MANUAL,
                ontem = ontem,
                ontemRezado = ontem in historico.dias,
                progresso = progressoValido,
                totalPassosProgresso =
                    progressoValido?.let { SequenciaTerco.montar(it.conjunto, configTerco.opcoes).size } ?: 0,
                lembretes = configLembretes,
                permissao = permissao,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TercoUiState())

    fun marcarHoje() =
        viewModelScope.launch {
            dias.marcarManualmente(relogio.hoje())
            lembretes.aoRegistrarOracao()
        }

    fun marcarOntem() =
        viewModelScope.launch {
            estado.value.ontem?.let { dias.marcarManualmente(it) }
        }

    fun desfazerHoje() = viewModelScope.launch { dias.desfazerMarcacaoManual(relogio.hoje()) }

    fun recomecar() = viewModelScope.launch { progressos.limpar() }

    /** Volta para o app (ex.: depois de mexer nas configurações do sistema). */
    fun atualizarPermissao() = permissao.update { it.copy(notificacoesPermitidas = lembretes.notificacoesPermitidas()) }

    fun aoResponderPermissao(concedida: Boolean) =
        viewModelScope.launch {
            if (concedida) {
                configuracoes.definirLembretes(configuracoes.lembretesAtuais().copy(ativo = true))
                lembretes.reagendar()
            }
            permissao.value = PermissaoUi(lembretes.notificacoesPermitidas(), negadaAgora = !concedida)
        }
}
