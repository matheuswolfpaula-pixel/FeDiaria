package br.app.fediaria.core.novena

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PascoaTest {
    @Test
    fun conferePascoasConhecidas() {
        val conhecidas =
            mapOf(
                2000 to "04-23",
                2001 to "04-15",
                2002 to "03-31",
                2003 to "04-20",
                2004 to "04-11",
                2005 to "03-27",
                2006 to "04-16",
                2007 to "04-08",
                2008 to "03-23",
                2009 to "04-12",
                2010 to "04-04",
                2011 to "04-24",
                2012 to "04-08",
                2013 to "03-31",
                2014 to "04-20",
                2015 to "04-05",
                2016 to "03-27",
                2017 to "04-16",
                2018 to "04-01",
                2019 to "04-21",
                2020 to "04-12",
                2021 to "04-04",
                2022 to "04-17",
                2023 to "04-09",
                2024 to "03-31",
                2025 to "04-20",
                2026 to "04-05",
                2027 to "03-28",
                2028 to "04-16",
                2029 to "04-01",
                2030 to "04-21",
                // Extremos: Páscoa mais tardia (25/4) e mais cedo possível (22/3).
                2038 to "04-25",
                2285 to "03-22",
            )
        conhecidas.forEach { (ano, data) ->
            assertEquals(LocalDate.parse("$ano-$data"), domingoDePascoa(ano), "Páscoa de $ano")
        }
    }

    @Test
    fun concordaComOAlgoritmoIndependenteDeOudin() {
        for (ano in 1583..4099) {
            assertEquals(pascoaOudin(ano), domingoDePascoa(ano), "Páscoa de $ano")
        }
    }

    @Test
    fun ehSempreDomingoEntre22DeMarcoE25DeAbril() {
        for (ano in 2000..2100) {
            val pascoa = domingoDePascoa(ano)
            assertEquals(DayOfWeek.SUNDAY, pascoa.dayOfWeek, "$ano")
            assertTrue(pascoa in LocalDate(ano, 3, 22)..LocalDate(ano, 4, 25), "$ano")
        }
    }

    /** Algoritmo de Oudin (1940), formulação diferente da usada no app, só para conferência. */
    private fun pascoaOudin(ano: Int): LocalDate {
        val g = ano % 19
        val c = ano / 100
        val h = (c - c / 4 - (8 * c + 13) / 25 + 19 * g + 15) % 30
        val i = h - (h / 28) * (1 - (29 / (h + 1)) * ((21 - g) / 11))
        val j = (ano + ano / 4 + i + 2 - c + c / 4) % 7
        val l = i - j
        val mes = 3 + (l + 40) / 44
        val dia = l + 28 - 31 * (mes / 4)
        return LocalDate(ano, mes, dia)
    }
}
