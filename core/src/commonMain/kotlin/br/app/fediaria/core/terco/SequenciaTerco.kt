package br.app.fediaria.core.terco

/** Orações usadas no terço. O texto de cada uma vem do conteúdo (JSON), pela chave [id]. */
enum class OracaoTerco(
    val id: String,
) {
    PELO_SINAL("pelo_sinal"),
    OFERECIMENTO("oferecimento"),
    CREDO("credo"),
    PAI_NOSSO("pai_nosso"),
    AVE_MARIA("ave_maria"),
    GLORIA("gloria"),
    O_MEU_JESUS("o_meu_jesus"),
    AGRADECIMENTO("agradecimento"),
    SALVE_RAINHA("salve_rainha"),
    SINAL_DA_CRUZ("sinal_da_cruz"),
}

/** Intenções tradicionais das três Ave-Marias iniciais. */
enum class Intencao { FE, ESPERANCA, CARIDADE }

sealed interface PassoTerco {
    /**
     * @property dezena 1 a 5 dentro das dezenas; `null` na introdução e no encerramento.
     * @property conta posição da Ave-Maria (1–3 na introdução, 1–10 na dezena).
     */
    data class Oracao(
        val oracao: OracaoTerco,
        val dezena: Int? = null,
        val conta: Int? = null,
        val intencao: Intencao? = null,
    ) : PassoTerco

    /** Anúncio do mistério a contemplar ([numero] de 1 a 5). */
    data class Misterio(
        val conjunto: ConjuntoMisterios,
        val numero: Int,
    ) : PassoTerco
}

data class OpcoesTerco(
    val oferecimento: Boolean = false,
    val agradecimento: Boolean = false,
)

/**
 * Monta o terço passo a passo:
 * Pelo sinal → (Oferecimento) → Credo → Pai-Nosso → 3 Ave-Marias (fé, esperança, caridade) → Glória →
 * 5 × [Mistério → Pai-Nosso → 10 Ave-Marias → Glória → Ó meu Jesus] →
 * (Agradecimento) → Salve-Rainha → Sinal da Cruz.
 */
object SequenciaTerco {
    const val DEZENAS = 5
    const val AVE_MARIAS_POR_DEZENA = 10

    fun montar(
        conjunto: ConjuntoMisterios,
        opcoes: OpcoesTerco = OpcoesTerco(),
    ): List<PassoTerco> =
        buildList {
            add(PassoTerco.Oracao(OracaoTerco.PELO_SINAL))
            if (opcoes.oferecimento) add(PassoTerco.Oracao(OracaoTerco.OFERECIMENTO))
            add(PassoTerco.Oracao(OracaoTerco.CREDO))
            add(PassoTerco.Oracao(OracaoTerco.PAI_NOSSO))
            Intencao.entries.forEachIndexed { i, intencao ->
                add(PassoTerco.Oracao(OracaoTerco.AVE_MARIA, conta = i + 1, intencao = intencao))
            }
            add(PassoTerco.Oracao(OracaoTerco.GLORIA))

            for (dezena in 1..DEZENAS) {
                add(PassoTerco.Misterio(conjunto, dezena))
                add(PassoTerco.Oracao(OracaoTerco.PAI_NOSSO, dezena = dezena))
                for (conta in 1..AVE_MARIAS_POR_DEZENA) {
                    add(PassoTerco.Oracao(OracaoTerco.AVE_MARIA, dezena = dezena, conta = conta))
                }
                add(PassoTerco.Oracao(OracaoTerco.GLORIA, dezena = dezena))
                add(PassoTerco.Oracao(OracaoTerco.O_MEU_JESUS, dezena = dezena))
            }

            if (opcoes.agradecimento) add(PassoTerco.Oracao(OracaoTerco.AGRADECIMENTO))
            add(PassoTerco.Oracao(OracaoTerco.SALVE_RAINHA))
            add(PassoTerco.Oracao(OracaoTerco.SINAL_DA_CRUZ))
        }
}
