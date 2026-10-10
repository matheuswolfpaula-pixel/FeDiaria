package br.app.fediaria

import android.app.Application
import br.app.fediaria.di.modulosFeDiaria
import br.app.fediaria.lembretes.AgendadorDeLembretes
import br.app.fediaria.lembretes.Notificacoes
import br.app.fediaria.lembretes.VigiaDeLembretesWorker
import br.app.fediaria.plataforma.AcoesDoSistema
import br.app.fediaria.plataforma.Lembretes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FeDiariaApplication : Application() {
    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FeDiariaApplication)
            modules(modulosFeDiaria() + moduloAndroid)
        }
        escopo.launch {
            get<Notificacoes>().criarCanal()
            get<AgendadorDeLembretes>().reagendar()
        }
        VigiaDeLembretesWorker.programar(this)
    }
}

private val moduloAndroid =
    module {
        single { Notificacoes(androidContext()) }
        single { AgendadorDeLembretes(androidContext(), get(), get(), get(), get()) } bind Lembretes::class
        single<AcoesDoSistema> { AcoesDoSistemaAndroid(androidContext()) }
    }
