package com.solplus.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solplus.app.core.util.BackupManager
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.data.preferences.AppPreferences
import com.solplus.app.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: MedicationRepository,
    private val preferences: AppPreferences,
    private val backupManager: BackupManager
) : ViewModel() {

    val fontScale: StateFlow<Float> = preferences.fontScale
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.15f)

    val medications: StateFlow<List<MedicationEntity>> = repository.getAllActiveMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _backupJsonString = MutableStateFlow<String?>(null)
    val backupJsonString: StateFlow<String?> = _backupJsonString.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

    fun setFontScale(scale: Float) {
        viewModelScope.launch {
            preferences.setFontScale(scale)
        }
    }

    fun updateMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            repository.updateMedication(medication)
            _operationMessage.value = "Informações de ${medication.name} atualizadas!"
        }
    }

    fun generateBackup() {
        viewModelScope.launch {
            val json = backupManager.createBackupJson()
            _backupJsonString.value = json
            _operationMessage.value = "Backup local gerado com sucesso!"
        }
    }

    fun restoreBackup(jsonString: String) {
        viewModelScope.launch {
            val success = backupManager.restoreBackupJson(jsonString)
            _operationMessage.value = if (success) {
                "Backup restaurado com sucesso!"
            } else {
                "Erro ao restaurar backup. Verifique os dados."
            }
        }
    }

    fun clearMessage() {
        _operationMessage.value = null
    }

    fun clearBackupString() {
        _backupJsonString.value = null
    }
}
