package br.app.fediaria.core.ofensiva

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Situação da ofensiva (sequência de dias com terço) num determinado "hoje".
 *
 * @property atual dias rezados na sequência em curso (dias protegidos por graça não somam).
 * @property recorde maior sequência já alcançada.
 * @property gracasDisponiveis dias de graça guardados (0 a [CalculadoraOfensiva.MAX_GRACAS]).
 * @property diasParaProximaGraca dias rezados que faltam para ganhar a próxima graça;
 *   `null` quando o limite de graças guardadas já foi atingido.
 * @property diasProtegidos dias sem terço que foram cobertos por uma graça.
 */
data class EstadoOfensiva(
    val atual: Int,
    val recorde: Int,
    val gracasDisponiveis: Int,
    val diasParaProximaGraca: Int?,
    val rezadoHoje: Boolean,
    val totalDiasRezados: Int,
    val diasProtegidos: Set<LocalDate>,
) {
    /** Há uma ofensiva em andamento e o terço de hoje ainda não foi rezado. */
    val emRisco: Boolean get() = atual > 0 && !rezadoHoje

    companion object {
        val VAZIO =
            EstadoOfensiva(
                atual = 0,
                recorde = 0,
                gracasDisponiveis = 0,
                diasParaProximaGraca = CalculadoraOfensiva.DIAS_PARA_GANHAR_GRACA,
                rezadoHoje = false,
                totalDiasRezados = 0,
                diasProtegidos = emptySet(),
            )
    }
}

/**
 * Regras da ofensiva. Tudo é DERIVADO do histórico de datas locais rezadas; nada é contador salvo.
 *
 * - Um dia conta quando há registro de terço naquela data local.
 * - Hoje sem terço ainda não quebra nada: fica pendente até a meia-noite.
 * - A cada [DIAS_PARA_GANHAR_GRACA] dias rezados na sequência ganha-se 1 dia de graça
 *   (máximo [MAX_GRACAS] guardados; acima disso não acumula).
 * - Um dia passado sem terço consome automaticamente uma graça, se houver, e a sequência segue.
 *   Sem graça, a sequência volta a zero (o recorde permanece).
 * - Registros com data posterior a hoje (ex.: volta de viagem para um fuso "atrasado")
 *   são ignorados até a data chegar.
 */
object CalculadoraOfensiva {
    const val DIAS_PARA_GANHAR_GRACA = 7
    const val MAX_GRACAS = 2

    fun calcular(
        diasRezados: Set<LocalDate>,
        hoje: LocalDate,
    ): EstadoOfensiva {
        val validos = diasRezados.filterTo(HashSet()) { it <= hoje }
        val primeiro = validos.minOrNull() ?: return EstadoOfensiva.VAZIO

        var atual = 0
        var recorde = 0
        var gracas = 0
        var rumoAGraca = 0
        val protegidos = mutableSetOf<LocalDate>()

        var dia = primeiro
        while (dia <= hoje) {
            when {
                dia in validos -> {
                    atual++
                    recorde = maxOf(recorde, atual)
                    rumoAGraca++
                    if (rumoAGraca == DIAS_PARA_GANHAR_GRACA) {
                        rumoAGraca = 0
                        gracas = minOf(MAX_GRACAS, gracas + 1)
                    }
                }

                dia == hoje -> {
                    // Hoje ainda sem terço: pendente, não quebra nem consome graça.
                }

                atual > 0 && gracas > 0 -> {
                    gracas--
                    protegidos += dia
                }

                else -> {
                    atual = 0
                    rumoAGraca = 0
                    gracas = 0
                }
            }
            dia = dia.plus(1, DateTimeUnit.DAY)
        }

        return EstadoOfensiva(
            atual = atual,
            recorde = recorde,
            gracasDisponiveis = gracas,
            diasParaProximaGraca = if (gracas >= MAX_GRACAS) null else DIAS_PARA_GANHAR_GRACA - rumoAGraca,
            rezadoHoje = hoje in validos,
            totalDiasRezados = validos.size,
            diasProtegidos = protegidos,
        )
    }

    /** Marcação manual (sistema de confiança): hoje, ou ontem para quem esqueceu de marcar. */
    fun podeMarcarManualmente(
        data: LocalDate,
        hoje: LocalDate,
    ): Boolean = data == hoje || data == hoje.minus(1, DateTimeUnit.DAY)
}
