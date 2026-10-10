package br.app.fediaria

import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.app.fediaria.data.db.DiaRezadoEntity
import br.app.fediaria.data.db.FeDiariaDatabase
import br.app.fediaria.data.db.OrigemRegistro
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
class BancoDeDadosTest {
    private val banco =
        Room
            .inMemoryDatabaseBuilder<FeDiariaDatabase>(InstrumentationRegistry.getInstrumentation().targetContext)
            .setDriver(AndroidSQLiteDriver())
            .build()
    private val dao = banco.diaRezadoDao()

    @After
    fun fechar() = banco.close()

    @Test
    fun mesmoDiaRegistradoDuasVezesValeOPrimeiro() =
        runTest {
            val dia = LocalDate(2026, 10, 10)
            dao.inserir(DiaRezadoEntity(dia, OrigemRegistro.GUIADO, Instant.parse("2026-10-10T20:00:00Z")))
            dao.inserir(DiaRezadoEntity(dia, OrigemRegistro.MANUAL, Instant.parse("2026-10-10T22:00:00Z")))

            val todos = dao.observarTodos().first()
            assertEquals(1, todos.size)
            assertEquals(OrigemRegistro.GUIADO, todos.single().origem)
        }

    @Test
    fun diasVoltamEmOrdemCronologica() =
        runTest {
            val agora = Instant.parse("2026-10-10T20:00:00Z")
            listOf(LocalDate(2026, 10, 10), LocalDate(2025, 12, 31), LocalDate(2026, 1, 1)).forEach {
                dao.inserir(DiaRezadoEntity(it, OrigemRegistro.MANUAL, agora))
            }

            assertEquals(
                listOf(LocalDate(2025, 12, 31), LocalDate(2026, 1, 1), LocalDate(2026, 10, 10)),
                dao.observarTodos().first().map { it.data },
            )
        }
}
