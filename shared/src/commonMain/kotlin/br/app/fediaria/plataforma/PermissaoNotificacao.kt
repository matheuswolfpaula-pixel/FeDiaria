package br.app.fediaria.plataforma

import androidx.compose.runtime.Composable

/**
 * Devolve uma ação que pede ao sistema permissão para notificar (Android 13+) e chama
 * [aoResponder] com `true` se as notificações ficaram liberadas.
 */
@Composable
expect fun lembrarPedidoDePermissaoDeNotificacao(aoResponder: (Boolean) -> Unit): () -> Unit
