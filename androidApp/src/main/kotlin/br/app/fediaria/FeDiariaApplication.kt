package br.app.fediaria

import android.app.Application
import br.app.fediaria.di.modulosFeDiaria
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FeDiariaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FeDiariaApplication)
            modules(modulosFeDiaria())
        }
    }
}
