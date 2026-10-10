package br.app.fediaria.ui.terco

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.ofensiva.EstadoOfensiva
import br.app.fediaria.data.db.OrigemRegistro
import br.app.fediaria.data.oracao.DiasRezadosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

data class HistoricoUiState(
    val carregando: Boolean = true,
    val hoje: LocalDate? = null,
    /** Primeiro dia do mês exibido no calendário. */
    val mes: LocalDate? = null,
    val ofensiva: EstadoOfensiva = EstadoOfensiva.VAZIO,
    val dias: Map<LocalDate, OrigemRegistro> = emptyMap(),
) {
    val podeAvancarMes: Boolean
        get() = mes != null && hoje != null && mes < LocalDate(hoje.year, hoje.month, 1)
}

class HistoricoViewModel(
    dias: DiasRezadosRepository,
) : ViewModel() {
    /** Deslocamento em meses a partir do mês atual (0 = mês de hoje, -1 = mês passado...). */
    private val deslocamento = MutableStateFlow(0)

    val estado: StateFlow<HistoricoUiState> =
        combine(dias.historico, deslocamento) { historico, deslocamento ->
            val mesAtual = LocalDate(historico.hoje.year, historico.hoje.month, 1)
            HistoricoUiState(
                carregando = false,
                hoje = historico.hoje,
                mes = mesAtual.plus(deslocamento, DateTimeUnit.MONTH),
                ofensiva = historico.ofensiva,
                dias = historico.dias,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoricoUiState())

    fun mesAnterior() = deslocamento.update { it - 1 }

    fun mesSeguinte() = deslocamento.update { minOf(0, it + 1) }
}
