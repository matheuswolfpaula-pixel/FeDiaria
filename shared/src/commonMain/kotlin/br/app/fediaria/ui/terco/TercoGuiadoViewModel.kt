package br.app.fediaria.ui.terco

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.core.terco.PassoTerco
import br.app.fediaria.core.terco.SequenciaTerco
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.ProgressoTerco
import br.app.fediaria.data.config.ProgressoTercoRepository
import br.app.fediaria.data.conteudo.ConteudoLatim
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.conteudo.ConteudoTerco
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

data class TercoGuiadoUiState(
    val carregando: Boolean = true,
    val conteudo: ConteudoTerco? = null,
    /** Presente quando o terço é rezado em latim. */
    val latim: ConteudoLatim? = null,
    val pronunciaLatim: Boolean = true,
    val passos: List<PassoTerco> = emptyList(),
    val indice: Int = 0,
    val vibracao: Boolean = true,
    val concluido: Boolean = false,
    val ofensivaFinal: Int = 0,
) {
    val passo: PassoTerco? get() = passos.getOrNull(indice)
    val ultimo: Boolean get() = indice == passos.lastIndex

    /** Índice do primeiro anúncio de mistério: antes dele é introdução, depois do último é encerramento. */
    val inicioDasDezenas: Int get() = passos.indexOfFirst { it is PassoTerco.Misterio }
}

class TercoGuiadoViewModel(
    conjuntoNome: String,
    private val conteudo: ConteudoRepository,
    private val dias: DiasRezadosRepository,
    private val progressos: ProgressoTercoRepository,
    private val configuracoes: ConfiguracoesRepository,
    private val lembretes: Lembretes,
    private val relogio: Relogio,
) : ViewModel() {
    private val conjunto = ConjuntoMisterios.valueOf(conjuntoNome)
    private val _estado = MutableStateFlow(TercoGuiadoUiState())
    val estado: StateFlow<TercoGuiadoUiState> = _estado.asStateFlow()
    private var iniciadoEm: Instant = relogio.agora()
    private var concluindo = false

    init {
        viewModelScope.launch {
            val config = configuracoes.terco.first()
            val passos = SequenciaTerco.montar(conjunto, config.opcoes)
            val salvo =
                progressos.atual()?.takeIf {
                    it.conjunto == conjunto && it.valido(relogio.agora()) && it.indicePasso in passos.indices
                }
            iniciadoEm = salvo?.iniciadoEm ?: relogio.agora()
            _estado.value =
                TercoGuiadoUiState(
                    carregando = false,
                    conteudo = conteudo.terco(),
                    latim = if (config.latim) conteudo.latim() else null,
                    pronunciaLatim = config.pronunciaLatim,
                    passos = passos,
                    indice = salvo?.indicePasso ?: 0,
                    vibracao = config.vibracao,
                )
            salvarProgresso()
        }
    }

    fun avancar() {
        val atual = _estado.value
        if (atual.carregando || atual.concluido) return
        if (atual.ultimo) {
            concluir()
        } else {
            _estado.update { it.copy(indice = it.indice + 1) }
            salvarProgresso()
        }
    }

    fun voltar() {
        if (_estado.value.indice == 0 || _estado.value.concluido) return
        _estado.update { it.copy(indice = it.indice - 1) }
        salvarProgresso()
    }

    private fun salvarProgresso() {
        val indice = _estado.value.indice
        viewModelScope.launch { progressos.salvar(ProgressoTerco(conjunto, indice, iniciadoEm)) }
    }

    private fun concluir() {
        if (concluindo) return
        concluindo = true
        viewModelScope.launch {
            dias.registrarTercoGuiado()
            progressos.limpar()
            lembretes.aoRegistrarOracao()
            val ofensiva = dias.estadoAtual()
            _estado.update { it.copy(concluido = true, ofensivaFinal = ofensiva.atual) }
        }
    }
}
