package com.solplus.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.data.local.entity.MedicationEntity
import com.solplus.app.ui.components.SolNavigationHeader
import com.solplus.app.ui.theme.SolBtnPrimaryGreen
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToDiagnostics: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLab: () -> Unit = {}
) {
    val fontScale by viewModel.fontScale.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val backupJson by viewModel.backupJsonString.collectAsState()
    val opMessage by viewModel.operationMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(opMessage) {
        opMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    var editingMedication by remember { mutableStateOf<MedicationEntity?>(null) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreInputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SolNavigationHeader(
                title = "Ajustes",
                icon = Icons.Default.Settings
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SolCreamBg
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Card: Diagnóstico Xiaomi 14 / HyperOS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, SolDarkGreen, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF5EE))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SolDarkGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Diagnóstico Xiaomi 14",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolTextDark
                                )
                                Text(
                                    text = "Alarmes, bateria e autostart",
                                    fontSize = 14.sp,
                                    color = SolTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToDiagnostics,
                            colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Abrir",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Card de Acesso ao Laboratório SOL+ (Exclusivo para builds Debug)
            if (com.solplus.app.BuildConfig.DEBUG) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = SolDarkGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Laboratório SOL+",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SolTextDark
                                    )
                                    Text(
                                        text = "Ambiente de Testes (Debug)",
                                        fontSize = 14.sp,
                                        color = SolTextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = onNavigateToLab,
                                colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Abrir",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Seção 1: Acessibilidade
            item {
                Text(
                    text = "Acessibilidade",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SolCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = SolDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tamanho dos Textos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val scales = listOf(
                                "Padrão" to 1.0f,
                                "Grande" to 1.2f,
                                "Extra" to 1.4f
                            )
                            scales.forEach { (label, scaleVal) ->
                                val isSelected = kotlin.math.abs(fontScale - scaleVal) < 0.05f
                                Button(
                                    onClick = { viewModel.setFontScale(scaleVal) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) SolDarkGreen else Color(0xFFEFECE6),
                                        contentColor = if (isSelected) Color.White else SolTextDark
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Seção 2: Validação da Receita Médica
            item {
                Text(
                    text = "Prescrição dos Medicamentos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
            }

            items(medications, key = { it.id }) { med ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, SolCardBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${med.name} (${med.period})",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dose: ${med.dosage}",
                                fontSize = 15.sp,
                                color = SolTextSecondary
                            )
                            Text(
                                text = "Instrução: ${med.instructions}",
                                fontSize = 15.sp,
                                color = SolTextSecondary
                            )
                        }

                        IconButton(
                            onClick = { editingMedication = med },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE8F5E9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar ${med.name}",
                                tint = SolDarkGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Seção 3: Backup Local Seguro
            item {
                Text(
                    text = "Segurança dos Registros",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SolCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Privacidade 100% Offline",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SolTextDark
                        )
                        Text(
                            text = "Seus dados nunca saem do celular. Exporte o backup local a qualquer momento com total segurança.",
                            fontSize = 14.sp,
                            color = SolTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.generateBackup() },
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backup,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Criar Backup", fontWeight = FontWeight.Bold, softWrap = true)
                            }

                            OutlinedButton(
                                onClick = { /* Desativado temporariamente por segurança */ },
                                enabled = false,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 48.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SolTextDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restaurar", fontWeight = FontWeight.Bold, softWrap = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3CD))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "A restauração externa foi temporariamente desativada para proteger o histórico de saúde contra sobrescrita ou perda acidental de dados.",
                                fontSize = 12.sp,
                                color = Color(0xFF856404),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal de Edição de Medicamento
    editingMedication?.let { med ->
        var editName by remember { mutableStateOf(med.name) }
        var editDosage by remember { mutableStateOf(med.dosage) }
        var editInstructions by remember { mutableStateOf(med.instructions) }

        AlertDialog(
            onDismissRequest = { editingMedication = null },
            title = {
                Text(
                    text = "Ajustar Prescrição Médica",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nome do medicamento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editDosage,
                        onValueChange = { editDosage = it },
                        label = { Text("Dose prescrita / gotas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editInstructions,
                        onValueChange = { editInstructions = it },
                        label = { Text("Instruções de uso") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMedication(
                            med.copy(
                                name = editName,
                                dosage = editDosage,
                                instructions = editInstructions
                            )
                        )
                        editingMedication = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen)
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingMedication = null }) {
                    Text("Cancelar", color = SolTextDark)
                }
            },
            containerColor = SolCardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Modal de Exibição de Backup Gerado
    backupJson?.let { json ->
        AlertDialog(
            onDismissRequest = { viewModel.clearBackupString() },
            title = {
                Text(
                    text = "Backup Gerado com Sucesso",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Os registros foram compilados localmente com sucesso. Guarde este código para restauração:",
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFEFECE6))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = json.take(300) + "... [Total: ${json.length} caracteres]",
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearBackupString() },
                    colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen)
                ) {
                    Text("Concluir", color = Color.White)
                }
            },
            containerColor = SolCardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text(
                    text = "Restaurar Backup Local",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Cole o código JSON do backup para recuperar os registros:",
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreInputText,
                        onValueChange = { restoreInputText = it },
                        placeholder = { Text("Cole o JSON do backup aqui...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreInputText.isNotBlank()) {
                            viewModel.restoreBackup(restoreInputText)
                            showRestoreDialog = false
                            restoreInputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen)
                ) {
                    Text("Restaurar Agora", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancelar", color = SolTextDark)
                }
            },
            containerColor = SolCardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
