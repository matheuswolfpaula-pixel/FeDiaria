package br.app.fediaria.core.lembretes

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class CalculadoraLembretesTest {
    private val saoPaulo = TimeZone.of("America/Sao_Paulo")
    private val vinteHoras = LocalTime(20, 0)

    private fun proximo(
        agora: String,
        horario: LocalTime = vinteHoras,
        fuso: TimeZone = saoPaulo,
    ) = CalculadoraLembretes.proximoDisparo(TipoLembrete.PRINCIPAL, Instant.parse(agora), horario, fuso)

    @Test
    fun antesDoHorarioDisparaHoje() {
        val disparo = proximo("2026-10-10T15:00:00Z") // 12:00 em SP
        assertEquals(Instant.parse("2026-10-10T23:00:00Z"), disparo.instante)
        assertEquals(LocalDate(2026, 10, 10), disparo.dataAlvo)
    }

    @Test
    fun depoisDoHorarioDisparaAmanha() {
        val disparo = proximo("2026-10-10T23:30:00Z") // 20:30 em SP
        assertEquals(Instant.parse("2026-10-11T23:00:00Z"), disparo.instante)
        assertEquals(LocalDate(2026, 10, 11), disparo.dataAlvo)
    }

    @Test
    fun exatamenteNoHorarioNaoRepeteHoje() {
        val disparo = proximo("2026-10-10T23:00:00Z")
        assertEquals(LocalDate(2026, 10, 11), disparo.dataAlvo)
    }

    @Test
    fun usaOFusoInformado() {
        val disparo = proximo("2026-10-10T15:00:00Z", fuso = TimeZone.of("Europe/Lisbon")) // 16:00 em Lisboa
        assertEquals(Instant.parse("2026-10-10T19:00:00Z"), disparo.instante)
    }

    @Test
    fun horarioInexistenteNoInicioDoHorarioDeVeraoVaiParaDepoisDoSalto() {
        // Em 04/11/2018 São Paulo pulou de 00:00 para 01:00; 00:30 não existiu.
        val disparo = proximo("2018-11-03T12:00:00Z", horario = LocalTime(0, 30))
        assertEquals(LocalDate(2018, 11, 4), disparo.dataAlvo)
        assertEquals(Instant.parse("2018-11-04T03:30:00Z"), disparo.instante) // 01:30 no horário de verão (UTC-2)
    }

    @Test
    fun fimDoHorarioDeVeraoNaoAtrasaODisparo() {
        // Lisboa volta 1h em 25/10/2026; 20:00 desse dia já está em UTC+0.
        val disparo = proximo("2026-10-25T08:00:00Z", fuso = TimeZone.of("Europe/Lisbon"))
        assertEquals(Instant.parse("2026-10-25T20:00:00Z"), disparo.instante)
    }

    @Test
    fun soNotificaNoDiaAlvoESeAindaNaoRezou() {
        val dia = LocalDate(2026, 10, 10)
        assertTrue(CalculadoraLembretes.deveNotificar(dataAlvo = dia, hoje = dia, rezadoHoje = false))
        assertFalse(CalculadoraLembretes.deveNotificar(dataAlvo = dia, hoje = dia, rezadoHoje = true))
        // Disparo atrasado que chegou depois da meia-noite.
        assertFalse(
            CalculadoraLembretes.deveNotificar(dataAlvo = dia, hoje = LocalDate(2026, 10, 11), rezadoHoje = false),
        )
    }

    @Test
    fun variacaoDeTextoMudaDeUmDiaParaOOutro() {
        val a = CalculadoraLembretes.indiceDeVariacao(LocalDate(2026, 10, 10), 3)
        val b = CalculadoraLembretes.indiceDeVariacao(LocalDate(2026, 10, 11), 3)
        assertTrue(a in 0..2 && b in 0..2 && a != b)
        assertEquals(0, CalculadoraLembretes.indiceDeVariacao(LocalDate(1960, 1, 1), 1))
    }

    @Test
    fun novenaAgendadaSoDisparaAPartirDoInicio() {
        val disparo =
            CalculadoraLembretes.proximoDisparo(
                TipoLembrete.NOVENA,
                Instant.parse("2026-09-30T15:00:00Z"),
                vinteHoras,
                saoPaulo,
                aPartirDe = LocalDate(2026, 10, 3),
            )
        assertEquals(LocalDate(2026, 10, 3), disparo.dataAlvo)
        assertEquals(Instant.parse("2026-10-03T23:00:00Z"), disparo.instante)

        // Início já passou: comporta-se como um lembrete diário comum.
        val diario =
            CalculadoraLembretes.proximoDisparo(
                TipoLembrete.NOVENA,
                Instant.parse("2026-10-05T15:00:00Z"),
                vinteHoras,
                saoPaulo,
                aPartirDe = LocalDate(2026, 10, 3),
            )
        assertEquals(LocalDate(2026, 10, 5), diario.dataAlvo)
    }
}
