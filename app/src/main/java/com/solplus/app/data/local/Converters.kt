package com.solplus.app.data.local

import androidx.room.TypeConverter
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.format(dateFormatter)

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it, dateFormatter) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.format(timeFormatter)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it, timeFormatter) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.format(dateTimeFormatter)

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it, dateTimeFormatter) }

    @TypeConverter
    fun fromDoseStatus(value: DoseStatus?): String? = value?.name

    @TypeConverter
    fun toDoseStatus(value: String?): DoseStatus? = value?.let { DoseStatus.valueOf(it) }

    @TypeConverter
    fun fromMedicationType(value: MedicationType?): String? = value?.name

    @TypeConverter
    fun toMedicationType(value: String?): MedicationType? = value?.let { MedicationType.valueOf(it) }
}
