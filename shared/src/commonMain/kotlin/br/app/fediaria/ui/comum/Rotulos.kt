package br.app.fediaria.ui.comum

import androidx.compose.runtime.Composable
import br.app.fediaria.core.terco.ConjuntoMisterios
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.conjunto_curto_dolorosos
import br.app.fediaria.shared.resources.conjunto_curto_gloriosos
import br.app.fediaria.shared.resources.conjunto_curto_gozosos
import br.app.fediaria.shared.resources.conjunto_curto_luminosos
import br.app.fediaria.shared.resources.conjunto_dolorosos
import br.app.fediaria.shared.resources.conjunto_gloriosos
import br.app.fediaria.shared.resources.conjunto_gozosos
import br.app.fediaria.shared.resources.conjunto_luminosos
import br.app.fediaria.shared.resources.data_extenso
import br.app.fediaria.shared.resources.dias_semana
import br.app.fediaria.shared.resources.meses
import br.app.fediaria.shared.resources.misterio_doloroso
import br.app.fediaria.shared.resources.misterio_glorioso
import br.app.fediaria.shared.resources.misterio_gozoso
import br.app.fediaria.shared.resources.misterio_luminoso
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

val ConjuntoMisterios.nome: StringResource
    get() =
        when (this) {
            ConjuntoMisterios.GOZOSOS -> Res.string.conjunto_gozosos
            ConjuntoMisterios.LUMINOSOS -> Res.string.conjunto_luminosos
            ConjuntoMisterios.DOLOROSOS -> Res.string.conjunto_dolorosos
            ConjuntoMisterios.GLORIOSOS -> Res.string.conjunto_gloriosos
        }

val ConjuntoMisterios.nomeCurto: StringResource
    get() =
        when (this) {
            ConjuntoMisterios.GOZOSOS -> Res.string.conjunto_curto_gozosos
            ConjuntoMisterios.LUMINOSOS -> Res.string.conjunto_curto_luminosos
            ConjuntoMisterios.DOLOROSOS -> Res.string.conjunto_curto_dolorosos
            ConjuntoMisterios.GLORIOSOS -> Res.string.conjunto_curto_gloriosos
        }

/** Formato "%dº Mistério Gozoso" etc. */
val ConjuntoMisterios.ordinalDoMisterio: StringResource
    get() =
        when (this) {
            ConjuntoMisterios.GOZOSOS -> Res.string.misterio_gozoso
            ConjuntoMisterios.LUMINOSOS -> Res.string.misterio_luminoso
            ConjuntoMisterios.DOLOROSOS -> Res.string.misterio_doloroso
            ConjuntoMisterios.GLORIOSOS -> Res.string.misterio_glorioso
        }

@Composable
fun nomeDoDiaDaSemana(data: LocalDate): String =
    stringArrayResource(Res.array.dias_semana)[
        data.dayOfWeek.isoDayNumber -
            1,
    ]

@Composable
fun nomeDoMes(data: LocalDate): String = stringArrayResource(Res.array.meses)[data.month.number - 1]

/** "sábado, 10 de outubro". */
@Composable
fun dataPorExtenso(data: LocalDate): String =
    stringResource(Res.string.data_extenso, nomeDoDiaDaSemana(data), data.day, nomeDoMes(data))

/** "07:05". */
fun LocalTime.formatado(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
