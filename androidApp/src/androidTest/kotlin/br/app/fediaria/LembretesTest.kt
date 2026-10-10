package br.app.fediaria

import android.Manifest
import android.app.NotificationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import br.app.fediaria.core.lembretes.TipoLembrete
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.db.NovenaDao
import br.app.fediaria.data.db.NovenaDiaEntity
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.lembretes.AgendadorDeLembretes
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext

@RunWith(AndroidJUnit4::class)
class LembretesTest {
    @get:Rule
    val permissao: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    private val koin = GlobalContext.get()
    private val agendador = koin.get<AgendadorDeLembretes>()
    private val configuracoes = koin.get<ConfiguracoesRepository>()
    private val dias = koin.get<DiasRezadosRepository>()
    private val relogio = koin.get<Relogio>()
    private val novenas = koin.get<NovenasRepository>()
    private val notificacoes =
        InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(NotificationManager::class.java)

    @Before
    fun preparar() {
        limparEstadoDoApp()
        notificacoes.cancelAll()
    }

    @After
    fun limpar() {
        limparEstadoDoApp()
        runBlocking { agendador.reagendar() }
        notificacoes.cancelAll()
    }

    /** O sistema publica e cancela notificações de forma assíncrona: espera até [esperado] (ou 3 s). */
    private fun assertNotificacoes(esperado: Int) {
        val limite = System.currentTimeMillis() + 3_000
        while (notificacoes.activeNotifications.size != esperado && System.currentTimeMillis() < limite) {
            Thread.sleep(50)
        }
        assertEquals(esperado, notificacoes.activeNotifications.size)
    }

    private fun configurar(config: ConfigLembretes) =
        runBlocking {
            configuracoes.definirLembretes(config)
            agendador.reagendar()
        }

    @Test
    fun agendaECancelaConformeConfiguracao() {
        configurar(ConfigLembretes(ativo = true, segundoAtivo = true))
        assertTrue(agendador.agendado(TipoLembrete.PRINCIPAL))
        assertTrue(agendador.agendado(TipoLembrete.SEGUNDO))

        configurar(ConfigLembretes(ativo = true, segundoAtivo = false))
        assertTrue(agendador.agendado(TipoLembrete.PRINCIPAL))
        assertFalse(agendador.agendado(TipoLembrete.SEGUNDO))

        configurar(ConfigLembretes(ativo = false))
        assertFalse(agendador.agendado(TipoLembrete.PRINCIPAL))
        assertFalse(agendador.agendado(TipoLembrete.SEGUNDO))
    }

    @Test
    fun notificaSoSeOTercoDeHojeNaoFoiRezado() =
        runBlocking {
            configurar(ConfigLembretes(ativo = true))
            val hoje = relogio.hoje()

            agendador.aoDisparar(TipoLembrete.PRINCIPAL, hoje)
            assertNotificacoes(1)

            // Rezou: a notificação some e um novo disparo não notifica.
            dias.marcarManualmente(hoje)
            agendador.aoRegistrarOracao()
            assertNotificacoes(0)
            agendador.aoDisparar(TipoLembrete.SEGUNDO, hoje)
            assertNotificacoes(0)
        }

    @Test
    fun disparoAtrasadoDeOntemEhDescartado() =
        runBlocking {
            configurar(ConfigLembretes(ativo = true))
            agendador.aoDisparar(TipoLembrete.PRINCIPAL, relogio.hoje().minus(1, DateTimeUnit.DAY))
            assertNotificacoes(0)
            // E o próximo disparo continua agendado.
            assertTrue(agendador.agendado(TipoLembrete.PRINCIPAL))
        }

    @Test
    fun novenaComLembreteEhAgendadaEParaAoConcluir() =
        runBlocking {
            val hoje = relogio.hoje()
            val id =
                novenas.iniciar(
                    "sao_jose",
                    hoje.minus(9, DateTimeUnit.DAY),
                    lembreteAtivo = true,
                    LocalTime(20, 0),
                )
            agendador.reagendar()
            assertTrue(agendador.novenaAgendada(id))

            // Notifica enquanto o dia de hoje está pendente.
            agendador.aoDispararNovena(id, hoje)
            assertNotificacoes(1)

            // Conclui os 9 dias (um por data) e reagenda: alarme cancelado.
            val dao = koin.get<NovenaDao>()
            (1..9).forEach { dia -> dao.marcarDia(NovenaDiaEntity(id, dia, hoje.minus(9 - dia, DateTimeUnit.DAY))) }
            agendador.aoRezarNovena(id)
            agendador.reagendar()
            assertFalse(agendador.novenaAgendada(id))
            assertNotificacoes(0)
        }

    @Test
    fun novenaSemLembreteOuRemovidaNaoTemAlarme() =
        runBlocking {
            val semLembrete = novenas.iniciar("natal", relogio.hoje(), lembreteAtivo = false, LocalTime(20, 0))
            val comLembrete = novenas.iniciar("aparecida", relogio.hoje(), lembreteAtivo = true, LocalTime(20, 0))
            agendador.reagendar()
            assertFalse(agendador.novenaAgendada(semLembrete))
            assertTrue(agendador.novenaAgendada(comLembrete))

            agendador.cancelarNovena(comLembrete)
            novenas.remover(comLembrete)
            agendador.reagendar()
            assertFalse(agendador.novenaAgendada(comLembrete))
        }

    @Test
    fun comLembretesDesligadosNaoNotifica() =
        runBlocking {
            configurar(ConfigLembretes(ativo = false))
            agendador.aoDisparar(TipoLembrete.PRINCIPAL, relogio.hoje())
            assertNotificacoes(0)
        }
}
