package br.app.fediaria.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** Uma novena que o usuário começou (ou agendou). [novenaId] aponta para o conteúdo em JSON. */
@Entity(tableName = "novena_ativa", indices = [Index("novena_id")])
data class NovenaAtivaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "novena_id") val novenaId: String,
    val inicio: LocalDate,
    @ColumnInfo(name = "lembrete_ativo") val lembreteAtivo: Boolean,
    /** Horário do lembrete em minutos desde 00:00. */
    @ColumnInfo(name = "lembrete_minutos") val lembreteMinutos: Int,
    @ColumnInfo(name = "criada_em") val criadaEm: Instant,
)

/** Um dia (1–9) de uma novena, rezado numa data local. */
@Entity(
    tableName = "novena_dia",
    primaryKeys = ["novena_ativa_id", "dia"],
    foreignKeys = [
        ForeignKey(
            entity = NovenaAtivaEntity::class,
            parentColumns = ["id"],
            childColumns = ["novena_ativa_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class NovenaDiaEntity(
    @ColumnInfo(name = "novena_ativa_id") val novenaAtivaId: Long,
    val dia: Int,
    @ColumnInfo(name = "rezado_em") val rezadoEm: LocalDate,
)

@Dao
interface NovenaDao {
    @Insert
    suspend fun inserir(novena: NovenaAtivaEntity): Long

    @Query("SELECT * FROM novena_ativa ORDER BY inicio")
    fun observarNovenas(): Flow<List<NovenaAtivaEntity>>

    @Query("SELECT * FROM novena_dia")
    fun observarDias(): Flow<List<NovenaDiaEntity>>

    @Query("SELECT * FROM novena_ativa")
    suspend fun todasAsNovenas(): List<NovenaAtivaEntity>

    @Query("SELECT * FROM novena_dia")
    suspend fun todosOsDias(): List<NovenaDiaEntity>

    @Query("SELECT * FROM novena_ativa WHERE id = :id")
    suspend fun buscar(id: Long): NovenaAtivaEntity?

    @Query("SELECT * FROM novena_dia WHERE novena_ativa_id = :id")
    suspend fun diasDe(id: Long): List<NovenaDiaEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun marcarDia(dia: NovenaDiaEntity)

    @Query("DELETE FROM novena_dia WHERE novena_ativa_id = :id AND rezado_em = :data")
    suspend fun desmarcarData(
        id: Long,
        data: LocalDate,
    )

    @Query("UPDATE novena_ativa SET lembrete_ativo = :ativo, lembrete_minutos = :minutos WHERE id = :id")
    suspend fun definirLembrete(
        id: Long,
        ativo: Boolean,
        minutos: Int,
    )

    @Query("DELETE FROM novena_ativa WHERE id = :id")
    suspend fun remover(id: Long)
}
