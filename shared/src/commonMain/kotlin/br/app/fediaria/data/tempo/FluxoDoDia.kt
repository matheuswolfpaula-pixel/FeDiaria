package br.app.fediaria.data.tempo

import br.app.fediaria.core.tempo.Relogio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.seconds

/** "Hoje" que muda sozinho na virada do dia (e após troca de fuso), enquanto alguém observa. */
fun Relogio.fluxoDoDia(): Flow<LocalDate> =
    flow {
        while (true) {
            emit(hoje())
            delay(30.seconds)
        }
    }.distinctUntilChanged()
