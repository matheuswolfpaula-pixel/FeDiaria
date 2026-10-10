package br.app.fediaria.data.db

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/** Datas como texto ISO (`2026-10-10`): legível no banco e ordenável. Instantes em milissegundos UTC. */
class Conversores {
    @TypeConverter
    fun dataParaTexto(data: LocalDate): String = data.toString()

    @TypeConverter
    fun textoParaData(texto: String): LocalDate = LocalDate.parse(texto)

    @TypeConverter
    fun instanteParaMillis(instante: Instant): Long = instante.toEpochMilliseconds()

    @TypeConverter
    fun millisParaInstante(millis: Long): Instant = Instant.fromEpochMilliseconds(millis)
}
