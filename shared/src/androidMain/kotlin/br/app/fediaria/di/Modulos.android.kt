package br.app.fediaria.di

import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import br.app.fediaria.data.config.ARQUIVO_PREFERENCIAS
import br.app.fediaria.data.config.criarDataStore
import br.app.fediaria.data.db.FeDiariaDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val modulosDaPlataforma: Module =
    module {
        single {
            val contexto = androidContext()
            Room
                .databaseBuilder<FeDiariaDatabase>(
                    context = contexto,
                    name = contexto.getDatabasePath(FeDiariaDatabase.NOME_ARQUIVO).absolutePath,
                ).setDriver(AndroidSQLiteDriver())
        }
        single {
            criarDataStore { androidContext().filesDir.resolve(ARQUIVO_PREFERENCIAS).absolutePath }
        }
    }
