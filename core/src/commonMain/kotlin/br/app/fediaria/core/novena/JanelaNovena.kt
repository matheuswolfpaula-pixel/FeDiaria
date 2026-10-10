package br.app.fediaria.core.novena

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** Quando a novena é tradicionalmente começada (o primeiro dos 9 dias). */
sealed interface JanelaNovena {
    fun inicioNoAno(ano: Int): LocalDate

    /** Começa sempre na mesma data (ex.: Natal, 16/12). */
    data class Fixa(
        val mes: Int,
        val dia: Int,
    ) : JanelaNovena {
        override fun inicioNoAno(ano: Int) = LocalDate(ano, mes, dia)
    }

    /** Começa [diasAposPascoa] dias depois do Domingo de Páscoa (ex.: Espírito Santo, +40). */
    data class RelativaAPascoa(
        val diasAposPascoa: Int,
    ) : JanelaNovena {
        override fun inicioNoAno(ano: Int) = domingoDePascoa(ano).plus(diasAposPascoa, DateTimeUnit.DAY)
    }

    /**
     * Início da janela em curso ou da próxima: a deste ano enquanto ela não terminou
     * (até o 9º dia), senão a do ano seguinte.
     */
    fun proximoInicio(hoje: LocalDate): LocalDate {
        val desteAno = inicioNoAno(hoje.year)
        val fimDesteAno = desteAno.plus(CalculadoraNovena.DIAS - 1, DateTimeUnit.DAY)
        return if (hoje <= fimDesteAno) desteAno else inicioNoAno(hoje.year + 1)
    }
}
