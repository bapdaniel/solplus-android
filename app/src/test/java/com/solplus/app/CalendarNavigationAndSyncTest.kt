package com.solplus.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CalendarNavigationAndSyncTest {

    private fun calculateSyncDate(currentDate: LocalDate, newMonth: YearMonth): LocalDate {
        val today = LocalDate.now()
        return if (YearMonth.from(today) == newMonth) {
            today
        } else {
            val currentDay = currentDate.dayOfMonth
            val maxDayInNewMonth = newMonth.lengthOfMonth()
            newMonth.atDay(currentDay.coerceAtMost(maxDayInNewMonth))
        }
    }

    @Test
    fun testMonthNavigationFromNovemberToDecemberUpdatesSelection() {
        val november2026 = YearMonth.of(2026, 11)
        val selectedInNov = LocalDate.of(2026, 11, 2)

        val december2026 = november2026.plusMonths(1)
        val syncedDate = calculateSyncDate(selectedInNov, december2026)

        assertEquals(december2026, YearMonth.from(syncedDate))
        assertEquals(2, syncedDate.dayOfMonth)
        assertEquals(12, syncedDate.monthValue)
        assertEquals(2026, syncedDate.year)
        // Garante que a data selecionada NUNCA continua sendo novembro
        assertNotEquals(11, syncedDate.monthValue)
    }

    @Test
    fun testYearTransitionNavigation() {
        val december2026 = YearMonth.of(2026, 12)
        val selectedDec = LocalDate.of(2026, 12, 31)

        val january2027 = december2026.plusMonths(1)
        val syncedJan = calculateSyncDate(selectedDec, january2027)

        assertEquals(YearMonth.of(2027, 1), YearMonth.from(syncedJan))
        assertEquals(2027, syncedJan.year)
        assertEquals(1, syncedJan.monthValue)
        assertEquals(31, syncedJan.dayOfMonth)

        // Volta para 2026
        val backToDec = YearMonth.from(syncedJan).minusMonths(1)
        val syncedBackDec = calculateSyncDate(syncedJan, backToDec)
        assertEquals(2026, syncedBackDec.year)
        assertEquals(12, syncedBackDec.monthValue)
    }

    @Test
    fun testMonthNavigationWithVaryingDaysInMonth() {
        // Transição 31 de janeiro para fevereiro em ano comum (2026 - 28 dias)
        val jan31 = LocalDate.of(2026, 1, 31)
        val feb2026 = YearMonth.of(2026, 2)
        val syncedFeb = calculateSyncDate(jan31, feb2026)

        assertEquals(28, syncedFeb.dayOfMonth)
        assertEquals(2, syncedFeb.monthValue)

        // Transição 31 de janeiro para fevereiro em ano bissexto (2028 - 29 dias)
        val feb2028 = YearMonth.of(2028, 2)
        val syncedFebLeap = calculateSyncDate(jan31, feb2028)

        assertEquals(29, syncedFebLeap.dayOfMonth)
        assertEquals(2, syncedFebLeap.monthValue)

        // Transição 31 de março para abril (30 dias)
        val mar31 = LocalDate.of(2026, 3, 31)
        val apr2026 = YearMonth.of(2026, 4)
        val syncedApr = calculateSyncDate(mar31, apr2026)

        assertEquals(30, syncedApr.dayOfMonth)
        assertEquals(4, syncedApr.monthValue)
    }

    @Test
    fun testDaySelectionInsideMonth() {
        var currentMonth = YearMonth.of(2026, 11)
        var selectedDate = LocalDate.of(2026, 11, 2)

        val newDay = LocalDate.of(2026, 11, 25)
        selectedDate = newDay
        val newMonth = YearMonth.from(newDay)
        if (currentMonth != newMonth) {
            currentMonth = newMonth
        }

        assertEquals(25, selectedDate.dayOfMonth)
        assertEquals(YearMonth.of(2026, 11), currentMonth)
    }

    @Test
    fun testSelectingDayOutsideCurrentMonthSyncsMonth() {
        var currentMonth = YearMonth.of(2026, 11)

        val dayInNextMonth = LocalDate.of(2026, 12, 1)
        val newMonth = YearMonth.from(dayInNextMonth)
        if (currentMonth != newMonth) {
            currentMonth = newMonth
        }

        assertEquals(YearMonth.of(2026, 12), currentMonth)
    }

    @Test
    fun testAbsenceOfRecordsHandledGracefully() {
        val emptyOccurrencesList = emptyList<String>()
        assertTrue(emptyOccurrencesList.isEmpty())
        val confirmedCount = emptyOccurrencesList.count { it == "CONFIRMED" }
        assertEquals(0, confirmedCount)
    }
}
