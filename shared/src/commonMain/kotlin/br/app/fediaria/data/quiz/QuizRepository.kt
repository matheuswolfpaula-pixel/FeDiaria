package br.app.fediaria.data.quiz

import br.app.fediaria.core.quiz.EstadoSrs
import br.app.fediaria.core.quiz.Pontuacao
import br.app.fediaria.core.quiz.RepeticaoEspacada
import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.db.LicaoConcluidaEntity
import br.app.fediaria.data.db.QuizDao
import br.app.fediaria.data.db.SrsItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Progresso do usuário no quiz: estado de Leitner por pergunta e lições concluídas. */
class QuizRepository(
    private val dao: QuizDao,
    private val relogio: Relogio,
) {
    val estadosSrs: Flow<Map<String, EstadoSrs>> =
        dao.observarSrs().map { itens -> itens.associate { it.itemId to it.paraCore() } }

    val licoesConcluidas: Flow<List<LicaoConcluidaEntity>> = dao.observarLicoes()

    /** Registra a resposta na hora (sair no meio da lição não perde o que já foi respondido). */
    suspend fun responder(
        perguntaId: String,
        acertou: Boolean,
    ): EstadoSrs {
        val novo = RepeticaoEspacada.responder(dao.srs(perguntaId)?.paraCore(), acertou, relogio.hoje())
        dao.salvarSrs(
            SrsItemEntity(perguntaId, novo.caixa, novo.proximaRevisao, novo.acertos, novo.erros),
        )
        return novo
    }

    suspend fun concluirLicao(
        licaoId: String,
        acertos: Int,
        total: Int,
    ): Int {
        val xp = Pontuacao.xp(acertos)
        dao.concluirLicao(
            LicaoConcluidaEntity(
                licaoId = licaoId,
                concluidaEm = relogio.agora(),
                acertos = acertos,
                total = total,
                xp = xp,
            ),
        )
        return xp
    }

    private fun SrsItemEntity.paraCore() = EstadoSrs(caixa, proximaRevisao, acertos, erros)
}
