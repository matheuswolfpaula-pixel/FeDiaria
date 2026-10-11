package br.app.fediaria.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.quiz.Licao
import br.app.fediaria.core.quiz.MontadorDeLicoes
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.aprendizado.AprendizadoRepository
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.Trilha
import br.app.fediaria.data.tempo.fluxoDoDia
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class LicaoUi(
    val licao: Licao,
    val feita: Boolean,
)

data class NivelUi(
    val nivel: Int,
    val aberto: Boolean,
    val licoes: List<LicaoUi>,
)

data class TrilhaUi(
    val trilha: Trilha,
    val niveis: List<NivelUi>,
)

data class QuizUiState(
    val carregando: Boolean = true,
    val xpTotal: Int = 0,
    val licoesFeitas: Int = 0,
    val revisaoPendente: Int = 0,
    val trilhas: List<TrilhaUi> = emptyList(),
)

class QuizViewModel(
    quiz: AprendizadoRepository,
    conteudo: ConteudoRepository,
    relogio: Relogio,
) : ViewModel() {
    val estado: StateFlow<QuizUiState> =
        combine(
            flow { emit(conteudo.quiz()) },
            quiz.estadosSrs,
            quiz.licoesConcluidas,
            relogio.fluxoDoDia(),
        ) { catalogo, srs, concluidas, hoje ->
            val licoes = MontadorDeLicoes.licoes(catalogo.perguntas.map { it.chave() })
            val feitas = concluidas.map { it.licaoId }.toSet()
            QuizUiState(
                carregando = false,
                xpTotal = concluidas.sumOf { it.xp },
                // Só as lições do quiz (a revisão e os exercícios de latim também ficam na mesma tabela).
                licoesFeitas = licoes.count { it.id in feitas },
                revisaoPendente =
                    MontadorDeLicoes.revisao(srs, hoje, catalogo.perguntas.map { it.id }.toSet()).size,
                trilhas =
                    catalogo.trilhas.map { trilha ->
                        TrilhaUi(
                            trilha = trilha,
                            niveis =
                                licoes
                                    .filter { it.trilha == trilha.id }
                                    .groupBy { it.nivel }
                                    .entries
                                    .sortedBy { it.key }
                                    .map { (nivel, doNivel) ->
                                        NivelUi(
                                            nivel = nivel,
                                            aberto = MontadorDeLicoes.nivelAberto(trilha.id, nivel, licoes, feitas),
                                            licoes = doNivel.map { LicaoUi(it, it.id in feitas) },
                                        )
                                    },
                        )
                    },
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QuizUiState())
}
