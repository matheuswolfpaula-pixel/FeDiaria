package br.app.fediaria.data.db

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ConversoresTest {
    private val conversores = Conversores()

    @Test
    fun dataViraTextoIsoEVolta() {
        val data = LocalDate(2026, 2, 28)
        assertEquals("2026-02-28", conversores.dataParaTexto(data))
        assertEquals(data, conversores.textoParaData("2026-02-28"))
    }

    @Test
    fun textoIsoOrdenaComoAsDatas() {
        val datas = listOf(LocalDate(2026, 12, 31), LocalDate(2027, 1, 1), LocalDate(2026, 9, 9))
        assertEquals(datas.sorted(), datas.sortedBy { conversores.dataParaTexto(it) })
    }

    @Test
    fun instanteSobreviveAIdaEVolta() {
        val instante = Instant.parse("2026-10-10T21:15:30.123Z")
        assertEquals(instante, conversores.millisParaInstante(conversores.instanteParaMillis(instante)))
    }
}
