package br.app.fediaria.plataforma

import androidx.compose.runtime.Composable

// Lembretes no iOS ainda não implementados (pós-MVP).
@Composable
actual fun lembrarPedidoDePermissaoDeNotificacao(aoResponder: (Boolean) -> Unit): () -> Unit = { aoResponder(false) }
