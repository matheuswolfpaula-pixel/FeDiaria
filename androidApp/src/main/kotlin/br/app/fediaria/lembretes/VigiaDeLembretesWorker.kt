package br.app.fediaria.lembretes

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * "Cinto e suspensório": a cada ~12h confere e reagenda os alarmes, caso algum fabricante
 * agressivo os tenha descartado sem avisar.
 */
class VigiaDeLembretesWorker(
    contexto: Context,
    parametros: WorkerParameters,
) : CoroutineWorker(contexto, parametros),
    KoinComponent {
    private val agendador: AgendadorDeLembretes by inject()

    override suspend fun doWork(): Result {
        agendador.reagendar()
        return Result.success()
    }

    companion object {
        private const val NOME = "vigia_lembretes"
        private const val INTERVALO_HORAS = 12L

        fun programar(contexto: Context) {
            val pedido = PeriodicWorkRequestBuilder<VigiaDeLembretesWorker>(INTERVALO_HORAS, TimeUnit.HOURS).build()
            WorkManager.getInstance(contexto).enqueueUniquePeriodicWork(NOME, ExistingPeriodicWorkPolicy.KEEP, pedido)
        }
    }
}
