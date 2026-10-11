package br.app.fediaria

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.app.fediaria.data.db.FeDiariaDatabase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Garante que quem atualiza o app não perde o histórico. Um teste por versão nova do banco. */
@RunWith(AndroidJUnit4::class)
class MigracoesTest {
    private val instrumentacao = InstrumentationRegistry.getInstrumentation()
    private val arquivo = instrumentacao.targetContext.getDatabasePath("migracao-teste.db")

    @get:Rule
    val ajudante =
        MigrationTestHelper(
            instrumentation = instrumentacao,
            file = arquivo,
            driver = AndroidSQLiteDriver(),
            databaseClass = FeDiariaDatabase::class,
        )

    // O ajudante não apaga o arquivo entre os testes; cada um começa do zero.
    @Before
    fun apagarBancoAnterior() {
        arquivo.delete()
    }

    @Test
    fun migra1Para2MantendoOsDiasRezados() {
        ajudante.createDatabase(1).apply {
            execSQL("INSERT INTO dia_rezado (data, origem, registrado_em) VALUES ('2026-10-10', 'GUIADO', 0)")
            close()
        }

        val conexao = ajudante.runMigrationsAndValidate(2)
        conexao.prepare("SELECT data, origem FROM dia_rezado").use { consulta ->
            consulta.step()
            assertEquals("2026-10-10", consulta.getText(0))
            assertEquals("GUIADO", consulta.getText(1))
        }
        conexao.prepare("SELECT COUNT(*) FROM novena_ativa").use { consulta ->
            consulta.step()
            assertEquals(0L, consulta.getLong(0))
        }
        conexao.close()
    }

    @Test
    fun migra2Para3MantendoAsNovenas() {
        ajudante.createDatabase(2).apply {
            execSQL(
                "INSERT INTO novena_ativa (id, novena_id, inicio, lembrete_ativo, lembrete_minutos, criada_em) " +
                    "VALUES (1, 'natal', '2026-12-16', 1, 1200, 0)",
            )
            execSQL("INSERT INTO novena_dia (novena_ativa_id, dia, rezado_em) VALUES (1, 1, '2026-12-16')")
            close()
        }

        val conexao = ajudante.runMigrationsAndValidate(3)
        conexao.prepare("SELECT novena_id FROM novena_ativa").use { consulta ->
            consulta.step()
            assertEquals("natal", consulta.getText(0))
        }
        conexao.prepare("SELECT COUNT(*) FROM novena_dia").use { consulta ->
            consulta.step()
            assertEquals(1L, consulta.getLong(0))
        }
        conexao.prepare("SELECT COUNT(*) FROM srs_item").use { consulta ->
            consulta.step()
            assertEquals(0L, consulta.getLong(0))
        }
        conexao.close()
    }
}
