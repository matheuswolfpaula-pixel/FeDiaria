package br.app.fediaria

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import br.app.fediaria.ui.App

class MainActivity : ComponentActivity() {
    /** Novena a abrir quando o app é aberto pela notificação dela. */
    private var novenaAAbrir by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) lerNovena(intent)
        setContent {
            App(novenaAAbrir = novenaAAbrir, aoAbrirNovena = { novenaAAbrir = null })
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lerNovena(intent)
    }

    private fun lerNovena(intent: Intent) {
        intent.getLongExtra(EXTRA_NOVENA_ID, -1).takeIf { it >= 0 }?.let { novenaAAbrir = it }
    }

    companion object {
        const val EXTRA_NOVENA_ID = "abrir_novena"
    }
}
