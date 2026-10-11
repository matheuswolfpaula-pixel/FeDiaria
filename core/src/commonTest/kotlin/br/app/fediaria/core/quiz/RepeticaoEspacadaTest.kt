package br.app.fediaria.core.quiz

import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RepeticaoEspacadaTest {
    private val hoje = LocalDate(2026, 10, 10)

    @Test
    fun itemNovoQueAcertaVaiParaACaixaDoisETresDias() {
        val e = RepeticaoEspacada.responder(null, acertou = true, hoje)
        assertEquals(EstadoSrs(caixa = 2, proximaRevisao = LocalDate(2026, 10, 13), acertos = 1, erros = 0), e)
    }

    @Test
    fun itemNovoQueErraVaiParaACaixaUmEAmanha() {
        val e = RepeticaoEspacada.responder(null, acertou = false, hoje)
        assertEquals(EstadoSrs(caixa = 1, proximaRevisao = LocalDate(2026, 10, 11), acertos = 0, erros = 1), e)
    }

    @Test
    fun acertosSucessivosSobemAteATrintaDiasENaoPassamDaUltimaCaixa() {
        var e: EstadoSrs? = null
        val intervalos = mutableListOf<Int>()
        repeat(6) {
            val novo = RepeticaoEspacada.responder(e, acertou = true, hoje)
            intervalos += hoje.daysUntil(novo.proximaRevisao)
            e = novo
        }
        assertEquals(listOf(3, 7, 14, 30, 30, 30), intervalos)
        assertEquals(RepeticaoEspacada.CAIXA_MAXIMA, e?.caixa)
        assertEquals(6, e?.acertos)
    }

    @Test
    fun erroEmQualquerCaixaVoltaParaAPrimeiraEMantemOHistorico() {
        val alta = EstadoSrs(caixa = 5, proximaRevisao = hoje, acertos = 4, erros = 0)
        val e = RepeticaoEspacada.responder(alta, acertou = false, hoje)
        assertEquals(1, e.caixa)
        assertEquals(LocalDate(2026, 10, 11), e.proximaRevisao)
        assertEquals(4, e.acertos)
        assertEquals(1, e.erros)
    }

    @Test
    fun intervaloAtravessaAViradaDoAno() {
        val e = RepeticaoEspacada.responder(EstadoSrs(4, hoje, 3, 0), acertou = true, LocalDate(2026, 12, 20))
        assertEquals(LocalDate(2027, 1, 19), e.proximaRevisao)
    }

    @Test
    fun vencidoNoDiaMarcadoEDepoisMasNaoAntes() {
        val e = EstadoSrs(2, LocalDate(2026, 10, 13), 1, 0)
        assertFalse(RepeticaoEspacada.vencido(e, LocalDate(2026, 10, 12)))
        assertTrue(RepeticaoEspacada.vencido(e, LocalDate(2026, 10, 13)))
        assertTrue(RepeticaoEspacada.vencido(e, LocalDate(2026, 11, 1)))
    }
}
