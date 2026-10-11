package br.app.fediaria.ui.latim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.latim.Exercicio
import br.app.fediaria.core.latim.GeradorDeExercicios
import br.app.fediaria.core.quiz.EstadoSrs
import br.app.fediaria.core.quiz.MontadorDeLicoes
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.aprendizado.AprendizadoRepository
import br.app.fediaria.data.conteudo.ConteudoLatim
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.FraseConteudo
import br.app.fediaria.data.conteudo.OracaoLatim
import br.app.fediaria.data.tempo.fluxoDoDia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Uma frase conta como memorizada quando o flashcard chegou à 3ª caixa (revisão em 7 dias ou mais). */
private const val CAIXA_MEMORIZADA = 3

private fun memorizadas(
    oracao: OracaoLatim,
    srs: Map<String, EstadoSrs>,
) = oracao.frases.count { (srs[it.id]?.caixa ?: 0) >= CAIXA_MEMORIZADA }

data class OracaoComProgresso(
    val oracao: OracaoLatim,
    val memorizadas: Int,
)

data class LatimUiState(
    val carregando: Boolean = true,
    val conteudo: ConteudoLatim? = null,
    val oracoes: List<OracaoComProgresso> = emptyList(),
    val flashcardsPendentes: Int = 0,
)

class LatimViewModel(
    aprendizado: AprendizadoRepository,
    conteudo: ConteudoRepository,
    relogio: Relogio,
) : ViewModel() {
    val estado: StateFlow<LatimUiState> =
        combine(flow { emit(conteudo.latim()) }, aprendizado.estadosSrs, relogio.fluxoDoDia()) { latim, srs, hoje ->
            LatimUiState(
                carregando = false,
                conteudo = latim,
                oracoes = latim.oracoes.map { OracaoComProgresso(it, memorizadas(it, srs)) },
                flashcardsPendentes = MontadorDeLicoes.revisao(srs, hoje, latim.idsDasFrases).size,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LatimUiState())
}

data class OracaoLatimUiState(
    val oracao: OracaoLatim? = null,
    val memorizadas: Int = 0,
)

/** Menu, leitura frase a frase e "rezar em latim" de uma oração. */
class OracaoLatimViewModel(
    id: String,
    aprendizado: AprendizadoRepository,
    conteudo: ConteudoRepository,
) : ViewModel() {
    val estado: StateFlow<OracaoLatimUiState> =
        combine(flow { emit(conteudo.latim().oracao(id)) }, aprendizado.estadosSrs) { oracao, srs ->
            OracaoLatimUiState(oracao, oracao?.let { memorizadas(it, srs) } ?: 0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OracaoLatimUiState())
}

/** Resposta em andamento do exercício atual. */
data class RespostaExercicio(
    /** Lacuna: palavra escolhida. */
    val escolhida: String? = null,
    /** Ordenar: posições (em `embaralhadas`) tocadas, na ordem. */
    val montadas: List<Int> = emptyList(),
    /** Associar: frase em latim selecionada esperando a tradução. */
    val latimSelecionado: Int? = null,
    /** Associar: pares já ligados (índices de `pares`). */
    val ligados: Set<Int> = emptySet(),
    val tentativasErradas: Int = 0,
    val ultimaTentativaErrada: Boolean = false,
    /** A resposta foi conferida (ou o associar foi completado). */
    val concluida: Boolean = false,
    val acertou: Boolean = false,
)

data class ExerciciosUiState(
    val carregando: Boolean = true,
    val oracao: OracaoLatim? = null,
    val exercicios: List<Exercicio> = emptyList(),
    val indice: Int = 0,
    val resposta: RespostaExercicio = RespostaExercicio(),
    val acertos: Int = 0,
    val finalizada: Boolean = false,
    val xpGanho: Int = 0,
) {
    val atual: Exercicio? get() = exercicios.getOrNull(indice)
    val ultimo: Boolean get() = indice == exercicios.lastIndex

    fun frase(id: String): FraseConteudo? = oracao?.frases?.firstOrNull { it.id == id }
}

class ExerciciosLatimViewModel(
    private val id: String,
    private val aprendizado: AprendizadoRepository,
    conteudo: ConteudoRepository,
    relogio: Relogio,
) : ViewModel() {
    private val _estado = MutableStateFlow(ExerciciosUiState())
    val estado: StateFlow<ExerciciosUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val oracao = conteudo.latim().oracao(id)
            val exercicios =
                oracao
                    ?.let { o ->
                        GeradorDeExercicios.licao(
                            o.frases.map { it.paraCore() },
                            Random(GeradorDeExercicios.semente(id, relogio.hoje())),
                        )
                    }.orEmpty()
            _estado.update { it.copy(carregando = false, oracao = oracao, exercicios = exercicios) }
        }
    }

    fun escolherPalavra(palavra: String) {
        val lacuna = _estado.value.atual as? Exercicio.Lacuna ?: return
        if (_estado.value.resposta.concluida) return
        concluir(RespostaExercicio(escolhida = palavra), acertou = palavra == lacuna.correta)
    }

    fun tocarPalavra(posicao: Int) =
        _estado.update {
            val r = it.resposta
            if (it.atual !is Exercicio.Ordenar || r.concluida ||
                posicao in r.montadas
            ) {
                it
            } else {
                it.copy(resposta = r.copy(montadas = r.montadas + posicao))
            }
        }

    fun desfazerPalavra(indiceNaResposta: Int) =
        _estado.update {
            val r = it.resposta
            if (r.concluida) {
                it
            } else {
                it.copy(
                    resposta =
                        r.copy(
                            montadas =
                                r.montadas.filterIndexed { i, _ ->
                                    i !=
                                        indiceNaResposta
                                },
                        ),
                )
            }
        }

    fun verificarOrdem() {
        val atual = _estado.value
        val ordenar = atual.atual as? Exercicio.Ordenar ?: return
        val r = atual.resposta
        if (r.concluida || r.montadas.size != ordenar.embaralhadas.size) return
        concluir(r, acertou = ordenar.confere(r.montadas.map { ordenar.embaralhadas[it] }))
    }

    fun selecionarLatim(par: Int) =
        _estado.update {
            val r = it.resposta
            if (it.atual !is Exercicio.Associar || r.concluida ||
                par in r.ligados
            ) {
                it
            } else {
                it.copy(resposta = r.copy(latimSelecionado = par, ultimaTentativaErrada = false))
            }
        }

    /** [posicao] = posição da tradução na lista exibida. */
    fun selecionarTraducao(posicao: Int) {
        val atual = _estado.value
        val associar = atual.atual as? Exercicio.Associar ?: return
        val r = atual.resposta
        val latim = r.latimSelecionado
        if (latim == null || r.concluida) return
        val par = associar.ordemTraducoes[posicao]
        val nova =
            if (par == latim) {
                r.copy(ligados = r.ligados + par, latimSelecionado = null, ultimaTentativaErrada = false)
            } else {
                r.copy(
                    latimSelecionado = null,
                    tentativasErradas = r.tentativasErradas + 1,
                    ultimaTentativaErrada = true,
                )
            }
        if (nova.ligados.size == associar.pares.size) {
            concluir(nova, acertou = nova.tentativasErradas == 0)
        } else {
            _estado.update { it.copy(resposta = nova) }
        }
    }

    private fun concluir(
        resposta: RespostaExercicio,
        acertou: Boolean,
    ) = _estado.update {
        it.copy(
            resposta = resposta.copy(concluida = true, acertou = acertou),
            acertos =
                it.acertos + if (acertou) 1 else 0,
        )
    }

    fun continuar() {
        val atual = _estado.value
        if (!atual.resposta.concluida || atual.finalizada) return
        if (!atual.ultimo) {
            _estado.update { it.copy(indice = it.indice + 1, resposta = RespostaExercicio()) }
            return
        }
        viewModelScope.launch {
            val xp = aprendizado.concluirLicao("latim.$id", atual.acertos, atual.exercicios.size)
            _estado.update { it.copy(finalizada = true, xpGanho = xp) }
        }
    }
}

data class FlashcardsUiState(
    val carregando: Boolean = true,
    val cartas: List<FraseConteudo> = emptyList(),
    val indice: Int = 0,
    val virada: Boolean = false,
    val lembradas: Int = 0,
) {
    val atual: FraseConteudo? get() = cartas.getOrNull(indice)
    val terminou: Boolean get() = !carregando && indice >= cartas.size
}

/**
 * Flashcards (frente: latim; verso: pronúncia e tradução). [alvo] = id de uma oração (vencidas
 * dela + frases novas, até 10) ou "revisao" (vencidas de todas as orações).
 */
class FlashcardsViewModel(
    private val alvo: String,
    private val aprendizado: AprendizadoRepository,
    conteudo: ConteudoRepository,
    relogio: Relogio,
) : ViewModel() {
    private val _estado = MutableStateFlow(FlashcardsUiState())
    val estado: StateFlow<FlashcardsUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            val latim = conteudo.latim()
            val srs = aprendizado.estadosSrs.first()
            val hoje = relogio.hoje()
            val frasesPorId = latim.oracoes.flatMap { it.frases }.associateBy { it.id }
            val ids =
                if (alvo == MontadorDeLicoes.ID_REVISAO) {
                    MontadorDeLicoes.revisao(srs, hoje, latim.idsDasFrases)
                } else {
                    val daOracao =
                        latim
                            .oracao(alvo)
                            ?.frases
                            ?.map { it.id }
                            .orEmpty()
                    val vencidas = MontadorDeLicoes.revisao(srs, hoje, daOracao.toSet())
                    val novas = daOracao.filter { it !in srs }
                    (vencidas + novas).take(MontadorDeLicoes.MAXIMO_REVISAO)
                }
            _estado.update { it.copy(carregando = false, cartas = ids.mapNotNull(frasesPorId::get)) }
        }
    }

    fun virar() = _estado.update { if (it.atual == null) it else it.copy(virada = true) }

    fun responder(lembrei: Boolean) {
        val carta = _estado.value.atual ?: return
        if (!_estado.value.virada) return
        _estado.update {
            it.copy(
                indice = it.indice + 1,
                virada = false,
                lembradas =
                    it.lembradas + if (lembrei) 1 else 0,
            )
        }
        viewModelScope.launch { aprendizado.responder(carta.id, lembrei) }
    }
}
