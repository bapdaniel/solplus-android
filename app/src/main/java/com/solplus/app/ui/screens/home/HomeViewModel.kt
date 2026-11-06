package com.solplus.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solplus.app.core.alarm.AlarmScheduler
import com.solplus.app.core.util.DefaultTimeProvider
import com.solplus.app.core.util.TimeProvider
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.data.repository.MedicationRepository
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeViewModel(
    private val repository: MedicationRepository,
    private val alarmScheduler: AlarmScheduler,
    private val timeProvider: TimeProvider = DefaultTimeProvider
) : ViewModel() {

    private val currentDate = MutableStateFlow(timeProvider.nowLocalDate())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todayDoses: StateFlow<List<DoseWithMedication>> = currentDate
        .flatMapLatest { repository.getDosesForDate(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isMorningDialogVisible = MutableStateFlow(false)
    val isMorningDialogVisible: StateFlow<Boolean> = _isMorningDialogVisible.asStateFlow()

    private val _isNightDialogVisible = MutableStateFlow(false)
    val isNightDialogVisible: StateFlow<Boolean> = _isNightDialogVisible.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                val today = timeProvider.nowLocalDate()
                if (currentDate.value != today) currentDate.value = today
                repository.ensureOccurrencesForDate(today)
                delay(60_000)
            }
        }
    }

    fun showMorningPrompt() {
        _isMorningDialogVisible.value = true
    }

    fun hideMorningPrompt() {
        _isMorningDialogVisible.value = false
    }

    fun showNightPrompt() {
        _isNightDialogVisible.value = true
    }

    fun hideNightPrompt() {
        _isNightDialogVisible.value = false
    }

    fun confirmDose(occurrenceId: Long) {
        viewModelScope.launch {
            repository.confirmDose(occurrenceId, timeProvider.nowLocalDateTime())
        }
    }

    fun postponeDose(occurrenceId: Long, isMorning: Boolean = true) {
        viewModelScope.launch {
            repository.postponeDose(occurrenceId, 15)
            alarmScheduler.schedulePostpone(15, isMorning)
        }
    }

    fun markUncertain(occurrenceId: Long) {
        viewModelScope.launch {
            repository.markUncertain(occurrenceId)
        }
    }

    fun takeNow(occurrenceId: Long) {
        // Regra de segurança médica: 'Vou tomar agora' NÃO confirma automaticamente.
        // A ocorrência permanece pendente aguardando a confirmação explícita da usuária.
    }

    fun correctDoseStatus(occurrenceId: Long, newStatus: DoseStatus, reason: String) {
        viewModelScope.launch {
            repository.correctDoseStatus(occurrenceId, newStatus, reason)
        }
    }

    fun getMorningPressureDose(doses: List<DoseWithMedication>): DoseWithMedication? {
        return doses.firstOrNull { it.medication.type == MedicationType.ORAL && it.medication.scheduledTime == LocalTime.of(8, 0) }
    }

    fun getMorningEyeDose(doses: List<DoseWithMedication>): DoseWithMedication? {
        return doses.firstOrNull { it.medication.type == MedicationType.EYE_DROP && it.medication.scheduledTime == LocalTime.of(8, 0) }
    }

    fun getNightEyeDose(doses: List<DoseWithMedication>): DoseWithMedication? {
        return doses.firstOrNull { it.medication.type == MedicationType.EYE_DROP && it.medication.scheduledTime == LocalTime.of(20, 0) }
    }
}
