package br.app.fediaria.data.conteudo

import br.app.fediaria.shared.resources.Res
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Lê o conteúdo empacotado no app (somente leitura) e guarda em memória. */
class ConteudoRepository {
    private val mutex = Mutex()
    private var terco: ConteudoTerco? = null
    private var novenas: ConteudoNovenas? = null
    private var quiz: ConteudoQuiz? = null
    private var latim: ConteudoLatim? = null

    suspend fun terco(): ConteudoTerco =
        mutex.withLock {
            terco ?: ConteudoTerco.deJson(ler("terco.json")).also { terco = it }
        }

    suspend fun novenas(): ConteudoNovenas =
        mutex.withLock {
            novenas ?: ConteudoNovenas.deJson(ler("novenas.json")).also { novenas = it }
        }

    suspend fun quiz(): ConteudoQuiz =
        mutex.withLock {
            quiz ?: ConteudoQuiz.deJson(ler("quiz.json")).also { quiz = it }
        }

    suspend fun latim(): ConteudoLatim =
        mutex.withLock {
            latim ?: ConteudoLatim.deJson(ler("latim.json")).also { latim = it }
        }

    private suspend fun ler(arquivo: String) = Res.readBytes("files/conteudo/$arquivo").decodeToString()
}
