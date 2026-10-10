package br.app.fediaria.data.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.app.fediaria.core.terco.ConjuntoMisterios
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/** Terço guiado interrompido no meio, para retomar de onde parou. */
data class ProgressoTerco(
    val conjunto: ConjuntoMisterios,
    val indicePasso: Int,
    val iniciadoEm: Instant,
) {
    /** Um terço começado há mais de [VALIDADE] é considerado abandonado. */
    fun valido(agora: Instant): Boolean = agora - iniciadoEm in kotlin.time.Duration.ZERO..VALIDADE

    companion object {
        val VALIDADE = 6.hours
    }
}

class ProgressoTercoRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val progresso: Flow<ProgressoTerco?> =
        dataStore.data.map { prefs ->
            val conjunto = prefs[CHAVE_CONJUNTO]?.let { v -> ConjuntoMisterios.entries.firstOrNull { it.name == v } }
            val indice = prefs[CHAVE_INDICE]
            val inicio = prefs[CHAVE_INICIO]
            if (conjunto == null || indice == null || inicio == null) {
                null
            } else {
                ProgressoTerco(conjunto, indice, Instant.fromEpochMilliseconds(inicio))
            }
        }

    suspend fun atual(): ProgressoTerco? = progresso.first()

    suspend fun salvar(progresso: ProgressoTerco) {
        dataStore.edit {
            it[CHAVE_CONJUNTO] = progresso.conjunto.name
            it[CHAVE_INDICE] = progresso.indicePasso
            it[CHAVE_INICIO] = progresso.iniciadoEm.toEpochMilliseconds()
        }
    }

    suspend fun limpar() {
        dataStore.edit {
            it.remove(CHAVE_CONJUNTO)
            it.remove(CHAVE_INDICE)
            it.remove(CHAVE_INICIO)
        }
    }

    private companion object {
        val CHAVE_CONJUNTO = stringPreferencesKey("progresso_conjunto")
        val CHAVE_INDICE = intPreferencesKey("progresso_indice")
        val CHAVE_INICIO = longPreferencesKey("progresso_inicio")
    }
}
