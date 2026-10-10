package br.app.fediaria.core.ofensiva

/**
 * Conquistas simples e serenas, todas derivadas da ofensiva (nada a salvar).
 * Os nomes exibidos ficam nos recursos de texto, pela chave [id].
 */
enum class Conquista(
    val id: String,
    private val medida: Medida,
    private val meta: Int,
) {
    PRIMEIRO_TERCO("primeiro_terco", Medida.TOTAL_DE_DIAS, 1),
    UMA_SEMANA("uma_semana", Medida.RECORDE, 7),
    TRINTA_TERCOS("trinta_tercos", Medida.TOTAL_DE_DIAS, 30),
    UM_MES("um_mes", Medida.RECORDE, 30),
    CEM_DIAS("cem_dias", Medida.RECORDE, 100),
    UM_ANO("um_ano", Medida.RECORDE, 365),
    ;

    fun alcancada(estado: EstadoOfensiva): Boolean =
        when (medida) {
            Medida.TOTAL_DE_DIAS -> estado.totalDiasRezados
            Medida.RECORDE -> estado.recorde
        } >= meta

    private enum class Medida { TOTAL_DE_DIAS, RECORDE }
}
