package br.app.fediaria.data.novena

import br.app.fediaria.core.novena.CalculadoraNovena
import br.app.fediaria.core.novena.EstadoNovena
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.db.NovenaAtivaEntity
import br.app.fediaria.data.db.NovenaDao
import br.app.fediaria.data.db.NovenaDiaEntity
import br.app.fediaria.data.tempo.fluxoDoDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Uma novena do usuário com o progresso calculado para hoje. */
data class NovenaDoUsuario(
    val id: Long,
    val novenaId: String,
    val inicio: LocalDate,
    val lembreteAtivo: Boolean,
    val horarioLembrete: LocalTime,
    val rezados: Map<Int, LocalDate>,
    val estado: EstadoNovena,
)

class NovenasRepository(
    private val dao: NovenaDao,
    private val relogio: Relogio,
) {
    val novenas: Flow<List<NovenaDoUsuario>> =
        combine(dao.observarNovenas(), dao.observarDias(), relogio.fluxoDoDia()) { novenas, dias, hoje ->
            montar(novenas, dias, hoje)
        }

    suspend fun novenasAtuais(): List<NovenaDoUsuario> = montar(dao.todasAsNovenas(), dao.todosOsDias(), relogio.hoje())

    suspend fun buscar(id: Long): NovenaDoUsuario? {
        val novena = dao.buscar(id) ?: return null
        return montar(listOf(novena), dao.diasDe(id), relogio.hoje()).single()
    }

    suspend fun iniciar(
        novenaId: String,
        inicio: LocalDate,
        lembreteAtivo: Boolean,
        horario: LocalTime,
    ): Long =
        dao.inserir(
            NovenaAtivaEntity(
                novenaId = novenaId,
                inicio = inicio,
                lembreteAtivo = lembreteAtivo,
                lembreteMinutos = horario.hour * MINUTOS_POR_HORA + horario.minute,
                criadaEm = relogio.agora(),
            ),
        )

    /** Marca o dia atual da novena como rezado hoje, se as regras permitirem. */
    suspend fun rezarDiaAtual(id: Long): Boolean {
        val novena = buscar(id)
        val dia = novena?.estado?.diaAtual
        val pode = dia != null && CalculadoraNovena.podeRezar(dia, novena.estado)
        if (pode) dao.marcarDia(NovenaDiaEntity(id, dia, relogio.hoje()))
        return pode
    }

    /** Desfaz o dia marcado hoje (toque por engano). */
    suspend fun desfazerHoje(id: Long) = dao.desmarcarData(id, relogio.hoje())

    suspend fun definirLembrete(
        id: Long,
        ativo: Boolean,
        horario: LocalTime,
    ) = dao.definirLembrete(id, ativo, horario.hour * MINUTOS_POR_HORA + horario.minute)

    suspend fun remover(id: Long) = dao.remover(id)

    private fun montar(
        novenas: List<NovenaAtivaEntity>,
        dias: List<NovenaDiaEntity>,
        hoje: LocalDate,
    ): List<NovenaDoUsuario> {
        val diasPorNovena = dias.groupBy { it.novenaAtivaId }
        return novenas.map { novena ->
            val rezados = diasPorNovena[novena.id].orEmpty().associate { it.dia to it.rezadoEm }
            NovenaDoUsuario(
                id = novena.id,
                novenaId = novena.novenaId,
                inicio = novena.inicio,
                lembreteAtivo = novena.lembreteAtivo,
                horarioLembrete =
                    LocalTime(
                        novena.lembreteMinutos / MINUTOS_POR_HORA,
                        novena.lembreteMinutos % MINUTOS_POR_HORA,
                    ),
                rezados = rezados,
                estado = CalculadoraNovena.calcular(novena.inicio, rezados, hoje),
            )
        }
    }

    private companion object {
        const val MINUTOS_POR_HORA = 60
    }
}
