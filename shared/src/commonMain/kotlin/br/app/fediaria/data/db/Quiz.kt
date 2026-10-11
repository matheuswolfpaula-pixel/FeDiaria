package br.app.fediaria.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** Estado de Leitner de uma pergunta do quiz (e, na Fase 5, de um flashcard de latim). */
@Entity(tableName = "srs_item")
data class SrsItemEntity(
    @PrimaryKey @ColumnInfo(name = "item_id") val itemId: String,
    val caixa: Int,
    @ColumnInfo(name = "proxima_revisao") val proximaRevisao: LocalDate,
    val acertos: Int,
    val erros: Int,
)

/** Uma lição terminada. A revisão (`revisao`) pode aparecer várias vezes. */
@Entity(tableName = "licao_concluida", indices = [Index("licao_id")])
data class LicaoConcluidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "licao_id") val licaoId: String,
    @ColumnInfo(name = "concluida_em") val concluidaEm: Instant,
    val acertos: Int,
    val total: Int,
    val xp: Int,
)

@Dao
interface QuizDao {
    @Query("SELECT * FROM srs_item")
    fun observarSrs(): Flow<List<SrsItemEntity>>

    @Query("SELECT * FROM srs_item WHERE item_id = :id")
    suspend fun srs(id: String): SrsItemEntity?

    @Upsert
    suspend fun salvarSrs(item: SrsItemEntity)

    @Query("SELECT * FROM licao_concluida ORDER BY concluida_em")
    fun observarLicoes(): Flow<List<LicaoConcluidaEntity>>

    @Insert
    suspend fun concluirLicao(licao: LicaoConcluidaEntity)

    @Query("DELETE FROM srs_item")
    suspend fun limparSrs()

    @Query("DELETE FROM licao_concluida")
    suspend fun limparLicoes()
}
