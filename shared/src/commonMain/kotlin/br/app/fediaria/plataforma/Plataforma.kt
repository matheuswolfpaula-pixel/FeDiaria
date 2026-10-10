package br.app.fediaria.plataforma

/**
 * Lembretes do sistema operacional. No Android: AlarmManager + notificações (módulo androidApp).
 * Os ViewModels só falam com esta interface.
 */
interface Lembretes {
    /** Recalcula e reagenda todos os lembretes a partir das configurações atuais. */
    suspend fun reagendar()

    /** Chamado quando o terço de hoje foi registrado: recolhe notificações já exibidas. */
    fun aoRegistrarOracao()

    /** As notificações estão liberadas pelo sistema (permissão e canal)? */
    fun notificacoesPermitidas(): Boolean
}

/** Atalhos para telas do sistema, usados na ajuda "Lembretes não chegam?". */
interface AcoesDoSistema {
    /** Fabricante do aparelho, em minúsculas (ex.: "samsung", "xiaomi"). */
    val fabricante: String

    fun abrirConfiguracoesDeNotificacao()

    fun abrirConfiguracoesDeBateria()

    fun abrirNoNavegador(url: String)
}

/** Implementações vazias, para plataformas ainda sem suporte (iOS) e para pré-visualizações. */
object LembretesInativos : Lembretes {
    override suspend fun reagendar() = Unit

    override fun aoRegistrarOracao() = Unit

    override fun notificacoesPermitidas(): Boolean = false
}
