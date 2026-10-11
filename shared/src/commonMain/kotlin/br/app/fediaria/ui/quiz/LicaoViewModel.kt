package br.app.fediaria.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.quiz.MontadorDeLicoes
import br.app.fediaria.core.quiz.OrdemDasAlternativas
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.aprendizado.AprendizadoRepository
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.Pergunta
import br.app.fediaria.data.conteudo.TipoPergunta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerguntaExibida(
    val pergunta: Pergunta,
    /** Índices de [Pergunta.alternativas] na ordem em que aparecem na tela. */
    val ordem: List<Int>,
)

data class LicaoUiState(
    val carregando: Boolean = true,
    val ehRevisao: Boolean = false,
    val perguntas: List<PerguntaExibida> = emptyList(),
    val indice: Int = 0,
    /** Índice original da alternativa escolhida na pergunta atual (null = ainda não respondeu). */
    val escolhida: Int? = null,
    val acertos: Int = 0,
    val concluida: Boolean = false,
    val xpGanho: Int = 0,
) {
    val atual: PerguntaExibida? get() = perguntas.getOrNull(indice)
    val vazia: Boolean get() = !carregando && perguntas.isEmpty()
    val ultima: Boolean get() = indice == perguntas.lastIndex
}

class LicaoViewModel(
    private val licaoId: String,
    private val quiz: AprendizadoRepository,
    private val conteudo: ConteudoRepository,
    private val relogio: Relogio,
) : ViewModel() {
    private val _estado = MutableStateFlow(LicaoUiState(ehRevisao = licaoId == MontadorDeLicoes.ID_REVISAO))
    val estado: StateFlow<LicaoUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val catalogo = conteudo.quiz()
            val hoje = relogio.hoje()
            val ids =
                if (licaoId == MontadorDeLicoes.ID_REVISAO) {
                    MontadorDeLicoes.revisao(quiz.estadosSrs.first(), hoje, catalogo.perguntas.map { it.id }.toSet())
                } else {
                    MontadorDeLicoes
                        .licoes(catalogo.perguntas.map { it.chave() })
                        .firstOrNull { it.id == licaoId }
                        ?.perguntas
                        .orEmpty()
                }
            val perguntas =
                ids.mapNotNull(catalogo::pergunta).map { p ->
                    PerguntaExibida(
                        pergunta = p,
                        ordem =
                            OrdemDasAlternativas.ordem(
                                quantidade = p.alternativas.size,
                                semente = OrdemDasAlternativas.semente(p.id, hoje),
                                embaralhar = p.tipo != TipoPergunta.VERDADEIRO_FALSO,
                            ),
                    )
                }
            _estado.update { it.copy(carregando = false, perguntas = perguntas) }
        }
    }

    fun escolher(alternativa: Int) {
        val atual = _estado.value
        val pergunta = atual.atual?.pergunta ?: return
        if (atual.escolhida != null) return
        val acertou = alternativa == pergunta.correta
        _estado.update { it.copy(escolhida = alternativa, acertos = it.acertos + if (acertou) 1 else 0) }
        viewModelScope.launch { quiz.responder(pergunta.id, acertou) }
    }

    fun continuar() {
        val atual = _estado.value
        if (atual.escolhida == null || atual.concluida) return
        if (!atual.ultima) {
            _estado.update { it.copy(indice = it.indice + 1, escolhida = null) }
            return
        }
        viewModelScope.launch {
            val xp = quiz.concluirLicao(licaoId, atual.acertos, atual.perguntas.size)
            _estado.update { it.copy(concluida = true, xpGanho = xp) }
        }
    }
}
