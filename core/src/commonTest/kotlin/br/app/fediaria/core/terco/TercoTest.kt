package br.app.fediaria.core.terco

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class TercoTest {
    @Test
    fun esquemaPadraoSegueRosariumVirginisMariae() {
        // 05/10/2026 é segunda-feira.
        val semana = (5..11).map { EsquemaMisterios.PADRAO.conjuntoDoDia(LocalDate(2026, 10, it)) }
        assertEquals(
            listOf(
                ConjuntoMisterios.GOZOSOS,
                ConjuntoMisterios.DOLOROSOS,
                ConjuntoMisterios.GLORIOSOS,
                ConjuntoMisterios.LUMINOSOS,
                ConjuntoMisterios.DOLOROSOS,
                ConjuntoMisterios.GOZOSOS,
                ConjuntoMisterios.GLORIOSOS,
            ),
            semana,
        )
    }

    @Test
    fun esquemaTradicionalNaoTemLuminosos() {
        val semana = (5..11).map { EsquemaMisterios.TRADICIONAL.conjuntoDoDia(LocalDate(2026, 10, it)) }
        assertEquals(false, ConjuntoMisterios.LUMINOSOS in semana)
        assertEquals(ConjuntoMisterios.GOZOSOS, semana[3]) // quinta
    }

    @Test
    fun tercoBasicoTem79Passos() {
        val passos = SequenciaTerco.montar(ConjuntoMisterios.GOZOSOS)
        assertEquals(79, passos.size)
        assertEquals(53, passos.count { it is PassoTerco.Oracao && it.oracao == OracaoTerco.AVE_MARIA })
        assertEquals(5, passos.count { it is PassoTerco.Misterio })
        assertEquals(PassoTerco.Oracao(OracaoTerco.PELO_SINAL), passos.first())
        assertEquals(PassoTerco.Oracao(OracaoTerco.SINAL_DA_CRUZ), passos.last())
    }

    @Test
    fun oracoesOpcionaisEntramNoLugarCerto() {
        val passos =
            SequenciaTerco.montar(
                ConjuntoMisterios.GLORIOSOS,
                OpcoesTerco(oferecimento = true, agradecimento = true),
            )
        assertEquals(81, passos.size)
        assertEquals(PassoTerco.Oracao(OracaoTerco.OFERECIMENTO), passos[1])
        // O Agradecimento termina pedindo uma Salve-Rainha, por isso vem antes dela.
        assertEquals(PassoTerco.Oracao(OracaoTerco.AGRADECIMENTO), passos[passos.size - 3])
        assertEquals(PassoTerco.Oracao(OracaoTerco.SALVE_RAINHA), passos[passos.size - 2])
    }

    @Test
    fun cadaDezenaTemMisterioPaiNossoDezAvesGloriaEOMeuJesus() {
        val passos = SequenciaTerco.montar(ConjuntoMisterios.DOLOROSOS)
        val terceira =
            passos.filter {
                (it is PassoTerco.Misterio && it.numero == 3) ||
                    (it as? PassoTerco.Oracao)?.dezena == 3
            }
        assertEquals(PassoTerco.Misterio(ConjuntoMisterios.DOLOROSOS, 3), terceira.first())
        assertEquals((1..10).toList(), terceira.mapNotNull { (it as? PassoTerco.Oracao)?.conta })
        assertEquals(OracaoTerco.O_MEU_JESUS, (terceira.last() as PassoTerco.Oracao).oracao)
        assertEquals(14, terceira.size)
    }

    @Test
    fun avesIniciaisTemAsTresIntencoes() {
        val iniciais =
            SequenciaTerco
                .montar(ConjuntoMisterios.GOZOSOS)
                .filterIsInstance<PassoTerco.Oracao>()
                .filter { it.intencao != null }
        assertEquals(Intencao.entries, iniciais.map { it.intencao })
    }
}
