package br.app.fediaria.ui.novena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.Novena
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class DetalheNovenaUiState(
    val carregando: Boolean = true,
    val novena: Novena? = null,
    val hoje: LocalDate? = null,
    val proximoInicio: LocalDate? = null,
    val idEmAndamento: Long? = null,
    val lembreteAtivo: Boolean = true,
    val horario: LocalTime = ConfigLembretes().horario,
    /** Preenchido quando a novena acabou de ser iniciada: a tela navega para ela. */
    val iniciadaId: Long? = null,
)

class DetalheNovenaViewModel(
    private val novenaId: String,
    private val novenas: NovenasRepository,
    private val conteudo: ConteudoRepository,
    private val configuracoes: ConfiguracoesRepository,
    private val lembretes: Lembretes,
    private val relogio: Relogio,
) : ViewModel() {
    private val _estado = MutableStateFlow(DetalheNovenaUiState())
    val estado: StateFlow<DetalheNovenaUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val novena = conteudo.novenas().novena(novenaId)
            val hoje = relogio.hoje()
            val emAndamento =
                novenas.novenasAtuais().firstOrNull {
                    it.novenaId == novenaId && it.estado.situacao != SituacaoNovena.CONCLUIDA
                }
            _estado.value =
                DetalheNovenaUiState(
                    carregando = false,
                    novena = novena,
                    hoje = hoje,
                    proximoInicio = novena?.janela?.paraCore()?.proximoInicio(hoje),
                    idEmAndamento = emAndamento?.id,
                    // Por padrão, o mesmo horário escolhido para o terço.
                    horario = configuracoes.lembretesAtuais().horario,
                )
        }
    }

    fun definirLembrete(ativo: Boolean) = _estado.update { it.copy(lembreteAtivo = ativo) }

    fun definirHorario(horario: LocalTime) = _estado.update { it.copy(horario = horario) }

    /** [lembretePermitido]: resultado do pedido de permissão (feito pela tela antes, se o lembrete estiver ligado). */
    fun iniciar(
        inicio: LocalDate,
        lembretePermitido: Boolean,
    ) = viewModelScope.launch {
        val atual = _estado.value
        if (atual.iniciadaId != null || atual.idEmAndamento != null) return@launch
        val id = novenas.iniciar(novenaId, inicio, atual.lembreteAtivo && lembretePermitido, atual.horario)
        lembretes.reagendar()
        _estado.update { it.copy(iniciadaId = id) }
    }
}
