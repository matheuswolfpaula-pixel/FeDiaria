package br.app.fediaria.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.app.fediaria.data.config.ConfiguracoesRepository
import br.app.fediaria.data.config.TemaPreferido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    configuracoes: ConfiguracoesRepository,
) : ViewModel() {
    val tema: StateFlow<TemaPreferido> =
        configuracoes.tema.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TemaPreferido.SISTEMA)
}
