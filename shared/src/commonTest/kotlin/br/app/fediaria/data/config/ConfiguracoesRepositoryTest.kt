package br.app.fediaria.data.config

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfiguracoesRepositoryTest {
    private val arquivo = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "fediaria-teste-${Random.nextLong()}.preferences_pb"

    @AfterTest
    fun limpar() {
        FileSystem.SYSTEM.delete(arquivo)
    }

    @Test
    fun temaPadraoEhSeguirOSistema() =
        runTest {
            val repo = ConfiguracoesRepository(criarDataStore { arquivo.toString() })
            repo.tema.test {
                assertEquals(TemaPreferido.SISTEMA, awaitItem())
            }
        }

    @Test
    fun temaEscolhidoEhPersistido() =
        runTest {
            val repo = ConfiguracoesRepository(criarDataStore { arquivo.toString() })
            repo.tema.test {
                assertEquals(TemaPreferido.SISTEMA, awaitItem())
                repo.definirTema(TemaPreferido.ESCURO)
                assertEquals(TemaPreferido.ESCURO, awaitItem())
            }
        }
}
