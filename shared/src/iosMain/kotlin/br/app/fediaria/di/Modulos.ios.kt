package br.app.fediaria.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.app.fediaria.data.config.ARQUIVO_PREFERENCIAS
import br.app.fediaria.data.config.criarDataStore
import br.app.fediaria.data.db.FeDiariaDatabase
import br.app.fediaria.plataforma.AcoesDoSistema
import br.app.fediaria.plataforma.Lembretes
import br.app.fediaria.plataforma.LembretesInativos
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// Ainda não compilado (exige macOS); validado no CI macOS a partir da Fase 7.
actual val modulosDaPlataforma: Module =
    module {
        single {
            Room
                .databaseBuilder<FeDiariaDatabase>(name = "${diretorioDocumentos()}/${FeDiariaDatabase.NOME_ARQUIVO}")
                .setDriver(BundledSQLiteDriver())
        }
        single { criarDataStore { "${diretorioDocumentos()}/$ARQUIVO_PREFERENCIAS" } }
        single<Lembretes> { LembretesInativos }
        single<AcoesDoSistema> { AcoesDoSistemaIos }
    }

@OptIn(ExperimentalForeignApi::class)
private fun diretorioDocumentos(): String {
    val url =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(url?.path)
}

private object AcoesDoSistemaIos : AcoesDoSistema {
    override val fabricante: String = "apple"

    override fun abrirConfiguracoesDeNotificacao() = Unit

    override fun abrirConfiguracoesDeBateria() = Unit

    override fun abrirNoNavegador(url: String) = Unit
}
