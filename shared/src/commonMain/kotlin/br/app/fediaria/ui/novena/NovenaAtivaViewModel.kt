package br.app.fediaria.ui.novena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.novena.CalculadoraNovena
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.ConteudoTerco
import br.app.fediaria.data.conteudo.Novena
import br.app.fediaria.data.novena.NovenaDoUsuario
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus

data class NovenaAtivaUiState(
    val carregando: Boolean = true,
    val usuario: NovenaDoUsuario? = null,
    val novena: Novena? = null,
    val terco: ConteudoTerco? = null,
    val removida: Boolean = false,
) {
    /** Dia cujo texto é exibido: o rezado hoje (para reler), senão o dia atual, ou o 9º se concluída. */
    val diaExibido: Int
        get() {
            val u = usuario ?: return 1
            if (u.estado.rezouHoje) return u.rezados.maxBy { it.value }.key
            return u.estado.diaAtual ?: CalculadoraNovena.DIAS
        }

    /** Retomou depois de dias sem rezar: a previsão passou do 9º dia contado do início. */
    val retomada: Boolean
        get() {
            val u = usuario ?: return false
            return u.estado.situacao == SituacaoNovena.EM_ANDAMENTO &&
                u.estado.previsaoTermino > u.inicio.plus(CalculadoraNovena.DIAS - 1, DateTimeUnit.DAY)
        }
}

class NovenaAtivaViewModel(
    private val id: Long,
    private val novenas: NovenasRepository,
    conteudo: ConteudoRepository,
    private val lembretes: Lembretes,
) : ViewModel() {
    private val removida = MutableStateFlow(false)

    val estado: StateFlow<NovenaAtivaUiState> =
        combine(
            novenas.novenas,
            flow { emit(conteudo.novenas() to conteudo.terco()) },
            removida,
        ) { lista, (catalogo, terco), removida ->
            val usuario = lista.firstOrNull { it.id == id }
            NovenaAtivaUiState(
                carregando = false,
                usuario = usuario,
                novena = usuario?.let { catalogo.novena(it.novenaId) },
                terco = terco,
                removida = removida || usuario == null,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NovenaAtivaUiState())

    fun rezarDiaAtual() =
        viewModelScope.launch {
            if (novenas.rezarDiaAtual(id)) {
                lembretes.aoRezarNovena(id)
                // Ao concluir, o reagendamento cancela o alarme desta novena.
                lembretes.reagendar()
            }
        }

    fun desfazerHoje() =
        viewModelScope.launch {
            novenas.desfazerHoje(id)
            lembretes.reagendar()
        }

    fun definirLembrete(
        ativo: Boolean,
        horario: LocalTime,
    ) = viewModelScope.launch {
        novenas.definirLembrete(id, ativo, horario)
        lembretes.reagendar()
    }

    fun remover() =
        viewModelScope.launch {
            lembretes.cancelarNovena(id)
            novenas.remover(id)
            removida.value = true
        }
}
