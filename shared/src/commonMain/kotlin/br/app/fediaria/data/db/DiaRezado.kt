package br.app.fediaria.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** Como o dia foi registrado: concluindo o terço guiado ou marcando manualmente. */
enum class OrigemRegistro { GUIADO, MANUAL }

/** Um dia (data LOCAL do aparelho) em que o terço foi rezado. A ofensiva é derivada desta tabela. */
@Entity(tableName = "dia_rezado")
data class DiaRezadoEntity(
    @PrimaryKey val data: LocalDate,
    val origem: OrigemRegistro,
    @ColumnInfo(name = "registrado_em") val registradoEm: Instant,
)

@Dao
interface DiaRezadoDao {
    /** Registrar o mesmo dia duas vezes não tem efeito: vale o primeiro registro. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(dia: DiaRezadoEntity)

    @Query("SELECT * FROM dia_rezado ORDER BY data")
    fun observarTodos(): Flow<List<DiaRezadoEntity>>

    @Query("DELETE FROM dia_rezado WHERE data = :data")
    suspend fun remover(data: LocalDate)
}
