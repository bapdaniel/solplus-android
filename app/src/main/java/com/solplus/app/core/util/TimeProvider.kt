package com.solplus.app.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Provedor de relógio injetável para desacoplar a lógica temporal
 * do relógio global do sistema operacional.
 */
interface TimeProvider {
    fun nowLocalDate(): LocalDate = LocalDate.now()
    fun nowLocalTime(): LocalTime = LocalTime.now()
    fun nowLocalDateTime(): LocalDateTime = LocalDateTime.now()
    fun currentTimeMillis(): Long = System.currentTimeMillis()
}

/**
 * Provedor padrão utilizando o relógio real do dispositivo.
 */
object DefaultTimeProvider : TimeProvider

/**
 * Provedor simulado para testes unitários e modo de laboratório.
 * Permite avançar ou fixar datas/horas sem alterar o relógio do aparelho.
 */
class SettableTimeProvider(
    private var currentDateTime: LocalDateTime = LocalDateTime.now()
) : TimeProvider {

    fun setDateTime(dateTime: LocalDateTime) {
        currentDateTime = dateTime
    }

    fun advanceMinutes(minutes: Long) {
        currentDateTime = currentDateTime.plusMinutes(minutes)
    }

    fun advanceDays(days: Long) {
        currentDateTime = currentDateTime.plusDays(days)
    }

    override fun nowLocalDate(): LocalDate = currentDateTime.toLocalDate()
    override fun nowLocalTime(): LocalTime = currentDateTime.toLocalTime()
    override fun nowLocalDateTime(): LocalDateTime = currentDateTime
    override fun currentTimeMillis(): Long =
        currentDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
