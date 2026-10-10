package br.app.fediaria.di

import br.app.fediaria.core.tempo.Relogio
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.ProgressoTercoRepository
import br.app.fediaria.data.conteudo.ConteudoRepository
import br.app.fediaria.data.db.FeDiariaDatabase
import br.app.fediaria.data.db.construirBanco
import br.app.fediaria.data.novena.NovenasRepository
import br.app.fediaria.data.oracao.DiasRezadosRepository
import br.app.fediaria.ui.AppViewModel
import br.app.fediaria.ui.novena.DetalheNovenaViewModel
import br.app.fediaria.ui.novena.NovenaAtivaViewModel
import br.app.fediaria.ui.novena.NovenasViewModel
import br.app.fediaria.ui.telas.ConfiguracoesViewModel
import br.app.fediaria.ui.terco.HistoricoViewModel
import br.app.fediaria.ui.terco.TercoGuiadoViewModel
import br.app.fediaria.ui.terco.TercoViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Fornece o builder do Room, o DataStore e, quando a plataforma não tem implementação própria
 * registrada por fora, `Lembretes` e `AcoesDoSistema`.
 */
expect val modulosDaPlataforma: Module

private val modulosDeDados =
    module {
        single { Relogio() }
        single { construirBanco(get()) }
        single { get<FeDiariaDatabase>().diaRezadoDao() }
        single { ConfiguracoesRepository(get()) }
        single { ProgressoTercoRepository(get()) }
        single { ConteudoRepository() }
        single { DiasRezadosRepository(get(), get()) }
        single { get<FeDiariaDatabase>().novenaDao() }
        single { NovenasRepository(get(), get()) }
    }

private val modulosDeTelas =
    module {
        viewModelOf(::AppViewModel)
        viewModelOf(::ConfiguracoesViewModel)
        viewModelOf(::TercoViewModel)
        viewModelOf(::HistoricoViewModel)
        viewModel { parametros -> TercoGuiadoViewModel(parametros.get(), get(), get(), get(), get(), get(), get()) }
        viewModelOf(::NovenasViewModel)
        viewModel { parametros -> DetalheNovenaViewModel(parametros.get(), get(), get(), get(), get(), get()) }
        viewModel { parametros -> NovenaAtivaViewModel(parametros.get(), get(), get(), get()) }
    }

fun modulosFeDiaria(): List<Module> = listOf(modulosDaPlataforma, modulosDeDados, modulosDeTelas)
