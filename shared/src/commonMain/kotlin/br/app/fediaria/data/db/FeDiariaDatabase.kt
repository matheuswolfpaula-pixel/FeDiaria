package br.app.fediaria.data.db

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Banco local do progresso do usuário. Conteúdo religioso NÃO fica aqui (vem dos JSON).
 * Ao mudar o schema: suba `version`, gere o JSON em `shared/schemas` e declare a migração
 * (automática quando possível) com teste em `androidApp/src/androidTest/.../MigracoesTest.kt`.
 *
 * v1: dia_rezado · v2: novena_ativa, novena_dia
 */
@Database(
    entities = [DiaRezadoEntity::class, NovenaAtivaEntity::class, NovenaDiaEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
@TypeConverters(Conversores::class)
@ConstructedBy(FeDiariaDatabaseConstructor::class)
abstract class FeDiariaDatabase : RoomDatabase() {
    abstract fun diaRezadoDao(): DiaRezadoDao

    abstract fun novenaDao(): NovenaDao

    companion object {
        const val NOME_ARQUIVO = "fediaria.db"
    }
}

// O Room gera a implementação (actual) deste objeto em cada plataforma.
@Suppress("KotlinNoActualForExpect")
expect object FeDiariaDatabaseConstructor : RoomDatabaseConstructor<FeDiariaDatabase> {
    override fun initialize(): FeDiariaDatabase
}

fun construirBanco(builder: RoomDatabase.Builder<FeDiariaDatabase>): FeDiariaDatabase =
    builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
