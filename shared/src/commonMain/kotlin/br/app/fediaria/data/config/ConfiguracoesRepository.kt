package br.app.fediaria.data.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

enum class TemaPreferido { SISTEMA, CLARO, ESCURO }

/** Preferências do usuário (DataStore). Cada plataforma informa onde fica o arquivo. */
class ConfiguracoesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val tema: Flow<TemaPreferido> =
        dataStore.data.map { prefs ->
            prefs[CHAVE_TEMA]?.let { valor -> TemaPreferido.entries.firstOrNull { it.name == valor } }
                ?: TemaPreferido.SISTEMA
        }

    suspend fun definirTema(tema: TemaPreferido) {
        dataStore.edit { it[CHAVE_TEMA] = tema.name }
    }

    private companion object {
        val CHAVE_TEMA = stringPreferencesKey("tema")
    }
}

const val ARQUIVO_PREFERENCIAS = "fediaria.preferences_pb"

fun criarDataStore(caminhoArquivo: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { caminhoArquivo().toPath() })
