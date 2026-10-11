package br.app.fediaria

import br.app.fediaria.data.config.ConfigLembretes
import br.app.fediaria.data.config.ConfigTerco
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.ProgressoTercoRepository
import br.app.fediaria.data.db.DiaRezadoDao
import br.app.fediaria.data.db.NovenaDao
import br.app.fediaria.data.db.QuizDao
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

/** Zera progresso e preferências do app instalado, para cada teste começar do mesmo ponto. */
fun limparEstadoDoApp() =
    runBlocking {
        val koin = GlobalContext.get()
        val dao = koin.get<DiaRezadoDao>()
        dao.todasAsDatas().forEach { dao.remover(it) }
        koin.get<NovenaDao>().apply { todasAsNovenas().forEach { remover(it.id) } }
        koin.get<QuizDao>().apply {
            limparSrs()
            limparLicoes()
        }
        koin.get<ProgressoTercoRepository>().limpar()
        koin.get<ConfiguracoesRepository>().apply {
            definirTerco(ConfigTerco())
            definirLembretes(ConfigLembretes())
        }
    }
