package br.app.fediaria.core.terco

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DayOfWeek.FRIDAY
import kotlinx.datetime.DayOfWeek.MONDAY
import kotlinx.datetime.DayOfWeek.SATURDAY
import kotlinx.datetime.DayOfWeek.SUNDAY
import kotlinx.datetime.DayOfWeek.THURSDAY
import kotlinx.datetime.DayOfWeek.TUESDAY
import kotlinx.datetime.DayOfWeek.WEDNESDAY
import kotlinx.datetime.LocalDate

enum class ConjuntoMisterios { GOZOSOS, LUMINOSOS, DOLOROSOS, GLORIOSOS }

/** Distribuição dos mistérios pelos dias da semana. */
enum class EsquemaMisterios(
    private val porDia: Map<DayOfWeek, ConjuntoMisterios>,
) {
    /** Rosarium Virginis Mariae (2002): Luminosos às quintas. */
    PADRAO(
        mapOf(
            MONDAY to ConjuntoMisterios.GOZOSOS,
            TUESDAY to ConjuntoMisterios.DOLOROSOS,
            WEDNESDAY to ConjuntoMisterios.GLORIOSOS,
            THURSDAY to ConjuntoMisterios.LUMINOSOS,
            FRIDAY to ConjuntoMisterios.DOLOROSOS,
            SATURDAY to ConjuntoMisterios.GOZOSOS,
            SUNDAY to ConjuntoMisterios.GLORIOSOS,
        ),
    ),

    /** Esquema anterior a 2002, sem os Luminosos. */
    TRADICIONAL(
        mapOf(
            MONDAY to ConjuntoMisterios.GOZOSOS,
            TUESDAY to ConjuntoMisterios.DOLOROSOS,
            WEDNESDAY to ConjuntoMisterios.GLORIOSOS,
            THURSDAY to ConjuntoMisterios.GOZOSOS,
            FRIDAY to ConjuntoMisterios.DOLOROSOS,
            SATURDAY to ConjuntoMisterios.GLORIOSOS,
            SUNDAY to ConjuntoMisterios.GLORIOSOS,
        ),
    ),
    ;

    fun conjuntoDoDia(data: LocalDate): ConjuntoMisterios = porDia.getValue(data.dayOfWeek)
}
