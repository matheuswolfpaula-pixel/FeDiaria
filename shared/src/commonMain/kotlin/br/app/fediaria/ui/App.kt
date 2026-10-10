package br.app.fediaria.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.app.fediaria.data.config.TemaPreferido
import br.app.fediaria.ui.navegacao.NavegacaoPrincipal
import br.app.fediaria.ui.tema.FeDiariaTema
import org.koin.compose.viewmodel.koinViewModel

/** Ponto de entrada da UI, compartilhado entre plataformas. */
@Composable
fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    val tema by viewModel.tema.collectAsStateWithLifecycle()
    val escuro =
        when (tema) {
            TemaPreferido.SISTEMA -> isSystemInDarkTheme()
            TemaPreferido.CLARO -> false
            TemaPreferido.ESCURO -> true
        }
    FeDiariaTema(escuro = escuro) {
        NavegacaoPrincipal()
    }
}
