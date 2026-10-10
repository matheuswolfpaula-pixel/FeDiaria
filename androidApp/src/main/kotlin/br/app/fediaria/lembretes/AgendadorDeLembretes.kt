package br.app.fediaria.lembretes

import br.app.fediaria.core.lembretes.CalculadoraLembretes
import br.app.fediaria.core.lembretes.TipoLembrete
import br.app.fediaria.core.novena.SituacaoNovena
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.novena.NovenaDoUsuario
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.plataforma.Lembretes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

/**
 * Regras dos lembretes. Cada alarme ([Alarmes]) chega dentro de 10 min após o horário; com o aparelho
 * em repouso profundo (Doze), quando ele "acorda" (por exemplo, ao ser pego). No disparo, apenas
 * DECIDE se notifica (só se a oração de hoje ainda não foi feita) e agenda o próximo.
 */
class AgendadorDeLembretes(
    private val alarmes: Alarmes,
    private val configuracoes: ConfiguracoesRepository,
    private val dias: DiasRezadosRepository,
    private val novenas: NovenasRepository,
    private val conteudo: ConteudoRepository,
    private val notificacoes: Notificacoes,
    private val relogio: Relogio,
) : Lembretes {
    override suspend fun reagendar() {
        val config = configuracoes.lembretesAtuais()
        TipoLembrete.DO_TERCO.forEach { tipo ->
            if (ativo(tipo, config)) {
                val horario = if (tipo == TipoLembrete.PRINCIPAL) config.horario else config.horarioSegundo
                alarmes.agendarTerco(
                    CalculadoraLembretes.proximoDisparo(
                        tipo,
                        relogio.agora(),
                        horario,
                        TimeZone.currentSystemDefault(),
                    ),
                )
            } else {
                alarmes.cancelarTerco(tipo)
            }
        }
        novenas.novenasAtuais().forEach { reagendarNovena(it) }
    }

    private fun reagendarNovena(novena: NovenaDoUsuario) {
        if (novena.lembreteAtivo && novena.estado.situacao != SituacaoNovena.CONCLUIDA) {
            val disparo =
                CalculadoraLembretes.proximoDisparo(
                    tipo = TipoLembrete.NOVENA,
                    agora = relogio.agora(),
                    horario = novena.horarioLembrete,
                    fuso = TimeZone.currentSystemDefault(),
                    aPartirDe = novena.inicio,
                )
            alarmes.agendarNovena(novena.id, disparo)
        } else {
            cancelarNovena(novena.id)
        }
    }

    /** Chamado pelo [LembreteReceiver] quando um alarme do terço toca. */
    suspend fun aoDisparar(
        tipo: TipoLembrete,
        dataAlvo: LocalDate,
    ) {
        if (ativo(tipo, configuracoes.lembretesAtuais())) {
            val ofensiva = dias.estadoAtual()
            val hoje = relogio.hoje()
            if (CalculadoraLembretes.deveNotificar(dataAlvo, hoje, ofensiva.rezadoHoje)) {
                notificacoes.mostrarLembrete(tipo, ofensiva, hoje)
            }
        }
        reagendar()
    }

    /** Chamado pelo [LembreteReceiver] quando o alarme de uma novena toca. */
    suspend fun aoDispararNovena(
        id: Long,
        dataAlvo: LocalDate,
    ) {
        val novena = novenas.buscar(id)
        val dia = novena?.estado?.diaAtual
        if (dia != null && deveLembrar(novena, dataAlvo)) {
            conteudo.novenas().novena(novena.novenaId)?.let { notificacoes.mostrarLembreteNovena(id, it.nome, dia) }
        }
        reagendar()
    }

    private fun deveLembrar(
        novena: NovenaDoUsuario,
        dataAlvo: LocalDate,
    ) = novena.lembreteAtivo && novena.estado.pendenteHoje && dataAlvo == relogio.hoje()

    override fun aoRegistrarOracao() = notificacoes.recolherLembrete()

    override fun aoRezarNovena(id: Long) = notificacoes.recolherLembreteNovena(id)

    override fun cancelarNovena(id: Long) {
        alarmes.cancelarNovena(id)
        notificacoes.recolherLembreteNovena(id)
    }

    override fun notificacoesPermitidas(): Boolean = notificacoes.permitidas()

    /** Diagnóstico e testes. */
    fun agendado(tipo: TipoLembrete): Boolean = alarmes.tercoAgendado(tipo)

    /** Diagnóstico e testes. */
    fun novenaAgendada(id: Long): Boolean = alarmes.novenaAgendada(id)

    private fun ativo(
        tipo: TipoLembrete,
        config: ConfigLembretes,
    ) = config.ativo && (tipo == TipoLembrete.PRINCIPAL || config.segundoAtivo)
}
