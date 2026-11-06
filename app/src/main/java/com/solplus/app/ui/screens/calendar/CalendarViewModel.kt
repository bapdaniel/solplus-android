package com.solplus.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.data.repository.MedicationRepository
import com.solplus.app.domain.model.DoseStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val monthDoses: StateFlow<List<DoseWithMedication>> = _currentMonth.flatMapLatest { ym ->
        repository.getDosesForMonth(ym)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedDayDoses: StateFlow<List<DoseWithMedication>> = _selectedDate.flatMapLatest { date ->
        repository.getDosesForDate(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.ensureOccurrencesForDate(LocalDate.now())
        }
    }

    fun previousMonth() {
        val newMonth = _currentMonth.value.minusMonths(1)
        _currentMonth.value = newMonth
        syncSelectedDateWithMonth(newMonth)
    }

    fun nextMonth() {
        val newMonth = _currentMonth.value.plusMonths(1)
        _currentMonth.value = newMonth
        syncSelectedDateWithMonth(newMonth)
    }

    private fun syncSelectedDateWithMonth(newMonth: YearMonth) {
        val today = LocalDate.now()
        val newDate = if (YearMonth.from(today) == newMonth) {
            today
        } else {
            val currentDay = _selectedDate.value.dayOfMonth
            val maxDayInNewMonth = newMonth.lengthOfMonth()
            newMonth.atDay(currentDay.coerceAtMost(maxDayInNewMonth))
        }
        selectDate(newDate)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        val dateMonth = YearMonth.from(date)
        if (_currentMonth.value != dateMonth) {
            _currentMonth.value = dateMonth
        }
        viewModelScope.launch {
            repository.ensureOccurrencesForDate(date)
        }
    }

    fun confirmDose(occurrenceId: Long) {
        viewModelScope.launch {
            repository.confirmDose(occurrenceId)
        }
    }

    fun correctDoseStatus(occurrenceId: Long, newStatus: DoseStatus, reason: String) {
        viewModelScope.launch {
            repository.correctDoseStatus(occurrenceId, newStatus, reason)
        }
    }
}
