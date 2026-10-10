package br.app.fediaria.data.oracao

import br.app.fediaria.core.ofensiva.CalculadoraOfensiva
import br.app.fediaria.core.ofensiva.EstadoOfensiva
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.db.DiaRezadoDao
import br.app.fediaria.data.db.DiaRezadoEntity
import br.app.fediaria.data.db.OrigemRegistro
import br.app.fediaria.data.tempo.fluxoDoDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

data class HistoricoOracao(
    val hoje: LocalDate,
    val ofensiva: EstadoOfensiva,
    val dias: Map<LocalDate, OrigemRegistro>,
)

class DiasRezadosRepository(
    private val dao: DiaRezadoDao,
    private val relogio: Relogio,
) {
    val hoje: Flow<LocalDate> = relogio.fluxoDoDia()

    val historico: Flow<HistoricoOracao> =
        combine(dao.observarTodos().map { lista -> lista.associate { it.data to it.origem } }, hoje) { dias, hoje ->
            HistoricoOracao(hoje, CalculadoraOfensiva.calcular(dias.keys, hoje), dias)
        }

    suspend fun estadoAtual(): EstadoOfensiva = CalculadoraOfensiva.calcular(dao.todasAsDatas().toSet(), relogio.hoje())

    suspend fun rezadoHoje(): Boolean = dao.buscar(relogio.hoje()) != null

    /** Terço guiado concluído agora: conta para a data local de hoje. */
    suspend fun registrarTercoGuiado() {
        dao.inserir(DiaRezadoEntity(relogio.hoje(), OrigemRegistro.GUIADO, relogio.agora()))
    }

    /** Marcação manual de hoje ou ontem. Retorna false se a data não é permitida. */
    suspend fun marcarManualmente(data: LocalDate): Boolean {
        if (!CalculadoraOfensiva.podeMarcarManualmente(data, relogio.hoje())) return false
        dao.inserir(DiaRezadoEntity(data, OrigemRegistro.MANUAL, relogio.agora()))
        return true
    }

    /** Desfaz apenas marcações manuais recentes (hoje/ontem); o terço guiado concluído não se desfaz. */
    suspend fun desfazerMarcacaoManual(data: LocalDate) {
        if (!CalculadoraOfensiva.podeMarcarManualmente(data, relogio.hoje())) return
        if (dao.buscar(data)?.origem == OrigemRegistro.MANUAL) dao.remover(data)
    }
}
