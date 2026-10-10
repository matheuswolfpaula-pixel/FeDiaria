package br.app.fediaria.di

import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.db.FeDiariaDatabase
import br.app.fediaria.data.db.construirBanco
import br.app.fediaria.ui.AppViewModel
import br.app.fediaria.ui.telas.ConfiguracoesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Fornece o builder do Room e o DataStore, que dependem de caminhos de cada plataforma. */
expect val modulosDaPlataforma: Module

private val modulosDeDados =
    module {
        single { Relogio() }
        single { construirBanco(get()) }
        single { get<FeDiariaDatabase>().diaRezadoDao() }
        single { ConfiguracoesRepository(get()) }
    }

private val modulosDeTelas =
    module {
        viewModelOf(::AppViewModel)
        viewModelOf(::ConfiguracoesViewModel)
    }

fun modulosFeDiaria(): List<Module> = listOf(modulosDaPlataforma, modulosDeDados, modulosDeTelas)
