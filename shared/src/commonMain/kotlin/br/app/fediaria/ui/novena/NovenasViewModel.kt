package br.app.fediaria.ui.novena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.Novena
import br.app.fediaria.data.novena.NovenaDoUsuario
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.data.tempo.fluxoDoDia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

data class NovenaComConteudo(
    val usuario: NovenaDoUsuario,
    val novena: Novena,
)

data class ItemDoCatalogo(
    val novena: Novena,
    val proximoInicio: LocalDate,
    /** Id da instância em andamento/agendada desta novena, se houver. */
    val idEmAndamento: Long?,
)

data class NovenasUiState(
    val carregando: Boolean = true,
    val emAndamento: List<NovenaComConteudo> = emptyList(),
    val catalogo: List<ItemDoCatalogo> = emptyList(),
    val concluidas: List<NovenaComConteudo> = emptyList(),
)

class NovenasViewModel(
    novenas: NovenasRepository,
    conteudo: ConteudoRepository,
    relogio: Relogio,
) : ViewModel() {
    val estado: StateFlow<NovenasUiState> =
        combine(novenas.novenas, flow { emit(conteudo.novenas()) }, relogio.fluxoDoDia()) { doUsuario, catalogo, hoje ->
            val comConteudo =
                doUsuario.mapNotNull { usuario ->
                    catalogo.novena(usuario.novenaId)?.let { NovenaComConteudo(usuario, it) }
                }
            val (concluidas, ativas) = comConteudo.partition { it.usuario.estado.situacao == SituacaoNovena.CONCLUIDA }
            NovenasUiState(
                carregando = false,
                // Primeiro as que pedem oração hoje, depois as já rezadas hoje, por fim as agendadas.
                emAndamento =
                    ativas.sortedWith(
                        compareBy<NovenaComConteudo>(
                            { !it.usuario.estado.pendenteHoje },
                            { it.usuario.estado.situacao == SituacaoNovena.AGENDADA },
                            { it.usuario.inicio },
                        ),
                    ),
                catalogo =
                    catalogo.novenas
                        .map { novena ->
                            ItemDoCatalogo(
                                novena = novena,
                                proximoInicio = novena.janela.paraCore().proximoInicio(hoje),
                                idEmAndamento = ativas.firstOrNull { it.novena.id == novena.id }?.usuario?.id,
                            )
                        }.sortedBy { it.proximoInicio },
                concluidas = concluidas.sortedByDescending { it.usuario.estado.previsaoTermino },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NovenasUiState())
}
