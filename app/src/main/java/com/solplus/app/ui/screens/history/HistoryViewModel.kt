package com.solplus.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solplus.app.data.local.entity.AuditLogEntity
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _selectedFilterMedicationId = MutableStateFlow<Long?>(null)
    val selectedFilterMedicationId: StateFlow<Long?> = _selectedFilterMedicationId.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Doses, 1: Auditoria
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val medications: StateFlow<List<MedicationEntity>> = repository.getAllActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyDoses: StateFlow<List<DoseWithMedication>> = combine(
        repository.getAllHistory(),
        _selectedFilterMedicationId
    ) { allDoses, filterId ->
        if (filterId == null) {
            allDoses
        } else {
            allDoses.filter { it.medication.id == filterId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectFilter(medicationId: Long?) {
        _selectedFilterMedicationId.value = medicationId
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }
}
