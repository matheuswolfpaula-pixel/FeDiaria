package br.app.fediaria.data.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.app.fediaria.core.terco.EsquemaMisterios
import br.app.fediaria.core.terco.OpcoesTerco
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import okio.Path.Companion.toPath

enum class TemaPreferido { SISTEMA, CLARO, ESCURO }

private val HORARIO_PADRAO = LocalTime(20, 0)
private val HORARIO_SEGUNDO_PADRAO = LocalTime(21, 30)
private const val MINUTOS_POR_HORA = 60
private const val MINUTOS_POR_DIA = 24 * MINUTOS_POR_HORA

data class ConfigLembretes(
    val ativo: Boolean = false,
    val horario: LocalTime = HORARIO_PADRAO,
    val segundoAtivo: Boolean = true,
    val horarioSegundo: LocalTime = HORARIO_SEGUNDO_PADRAO,
)

data class ConfigTerco(
    val esquema: EsquemaMisterios = EsquemaMisterios.PADRAO,
    val oferecimento: Boolean = false,
    val agradecimento: Boolean = false,
    val vibracao: Boolean = true,
    /** Terço guiado com as orações em latim (as que não têm versão latina ficam em português). */
    val latim: Boolean = false,
    /** No terço em latim, mostrar a pronúncia sob cada frase. */
    val pronunciaLatim: Boolean = true,
) {
    val opcoes: OpcoesTerco get() = OpcoesTerco(oferecimento = oferecimento, agradecimento = agradecimento)
}

/** Preferências do usuário (DataStore). Cada plataforma informa onde fica o arquivo. */
class ConfiguracoesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val tema: Flow<TemaPreferido> =
        dataStore.data
            .map { prefs -> prefs[CHAVE_TEMA].paraEnum(TemaPreferido.SISTEMA) }
            .distinctUntilChanged()

    val lembretes: Flow<ConfigLembretes> =
        dataStore.data
            .map { prefs ->
                val padrao = ConfigLembretes()
                ConfigLembretes(
                    ativo = prefs[CHAVE_LEMBRETE_ATIVO] ?: padrao.ativo,
                    horario = prefs[CHAVE_LEMBRETE_MINUTOS]?.paraHorario() ?: padrao.horario,
                    segundoAtivo = prefs[CHAVE_SEGUNDO_ATIVO] ?: padrao.segundoAtivo,
                    horarioSegundo = prefs[CHAVE_SEGUNDO_MINUTOS]?.paraHorario() ?: padrao.horarioSegundo,
                )
            }.distinctUntilChanged()

    val terco: Flow<ConfigTerco> =
        dataStore.data
            .map { prefs ->
                val padrao = ConfigTerco()
                ConfigTerco(
                    esquema = prefs[CHAVE_ESQUEMA].paraEnum(padrao.esquema),
                    oferecimento = prefs[CHAVE_OFERECIMENTO] ?: padrao.oferecimento,
                    agradecimento = prefs[CHAVE_AGRADECIMENTO] ?: padrao.agradecimento,
                    vibracao = prefs[CHAVE_VIBRACAO] ?: padrao.vibracao,
                    latim = prefs[CHAVE_LATIM] ?: padrao.latim,
                    pronunciaLatim = prefs[CHAVE_PRONUNCIA_LATIM] ?: padrao.pronunciaLatim,
                )
            }.distinctUntilChanged()

    suspend fun lembretesAtuais(): ConfigLembretes = lembretes.first()

    suspend fun definirTema(tema: TemaPreferido) {
        dataStore.edit { it[CHAVE_TEMA] = tema.name }
    }

    suspend fun definirLembretes(config: ConfigLembretes) {
        dataStore.edit {
            it[CHAVE_LEMBRETE_ATIVO] = config.ativo
            it[CHAVE_LEMBRETE_MINUTOS] = config.horario.paraMinutos()
            it[CHAVE_SEGUNDO_ATIVO] = config.segundoAtivo
            it[CHAVE_SEGUNDO_MINUTOS] = config.horarioSegundo.paraMinutos()
        }
    }

    suspend fun definirTerco(config: ConfigTerco) {
        dataStore.edit {
            it[CHAVE_ESQUEMA] = config.esquema.name
            it[CHAVE_OFERECIMENTO] = config.oferecimento
            it[CHAVE_AGRADECIMENTO] = config.agradecimento
            it[CHAVE_VIBRACAO] = config.vibracao
            it[CHAVE_LATIM] = config.latim
            it[CHAVE_PRONUNCIA_LATIM] = config.pronunciaLatim
        }
    }

    private companion object {
        val CHAVE_TEMA = stringPreferencesKey("tema")
        val CHAVE_LEMBRETE_ATIVO = booleanPreferencesKey("lembrete_ativo")
        val CHAVE_LEMBRETE_MINUTOS = intPreferencesKey("lembrete_minutos")
        val CHAVE_SEGUNDO_ATIVO = booleanPreferencesKey("segundo_lembrete_ativo")
        val CHAVE_SEGUNDO_MINUTOS = intPreferencesKey("segundo_lembrete_minutos")
        val CHAVE_ESQUEMA = stringPreferencesKey("esquema_misterios")
        val CHAVE_OFERECIMENTO = booleanPreferencesKey("terco_oferecimento")
        val CHAVE_AGRADECIMENTO = booleanPreferencesKey("terco_agradecimento")
        val CHAVE_VIBRACAO = booleanPreferencesKey("terco_vibracao")
        val CHAVE_LATIM = booleanPreferencesKey("terco_latim")
        val CHAVE_PRONUNCIA_LATIM = booleanPreferencesKey("terco_pronuncia_latim")
    }
}

private inline fun <reified E : Enum<E>> String?.paraEnum(padrao: E): E =
    this?.let { valor -> enumValues<E>().firstOrNull { it.name == valor } } ?: padrao

private fun LocalTime.paraMinutos(): Int = hour * MINUTOS_POR_HORA + minute

private fun Int.paraHorario(): LocalTime? =
    if (this in 0 until MINUTOS_POR_DIA) LocalTime(this / MINUTOS_POR_HORA, this % MINUTOS_POR_HORA) else null

const val ARQUIVO_PREFERENCIAS = "fediaria.preferences_pb"

fun criarDataStore(caminhoArquivo: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { caminhoArquivo().toPath() })
