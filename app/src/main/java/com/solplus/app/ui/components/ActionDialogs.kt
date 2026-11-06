package com.solplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import com.solplus.app.ui.theme.SolCardSurface
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolGreen
import com.solplus.app.ui.theme.SolSunGold
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.StatusPostponedColor
import com.solplus.app.ui.theme.StatusUncertainColor

@Composable
fun NotYetOptionsDialog(
    medicationName: String,
    medicationType: MedicationType,
    onTakeNow: () -> Unit,
    onRemindLater: () -> Unit,
    onUncertain: () -> Unit,
    onConfirmTaken: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var showTakeNowConfirmation by remember { mutableStateOf(false) }

    val isOral = medicationType == MedicationType.ORAL
    val takeText = if (isOral) "Vou tomar agora" else "Vou aplicar agora"
    val uncertainText = if (isOral) "Não sei se já tomei" else "Não sei se já apliquei"

    if (showTakeNowConfirmation) {
        TakeNowConfirmationDialog(
            medicationName = medicationName,
            medicationType = medicationType,
            onConfirmTaken = {
                showTakeNowConfirmation = false
                onConfirmTaken?.invoke() ?: onTakeNow()
                onDismiss()
            },
            onNotTakenYet = {
                showTakeNowConfirmation = false
                onTakeNow()
                onDismiss()
            },
            onDismiss = {
                showTakeNowConfirmation = false
                onTakeNow()
                onDismiss()
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = medicationName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "O que você deseja fazer agora?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SolTextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Opção 1: Vou tomar agora (abre confirmação explícita antes de registrar)
                    LargeDialogOption(
                        title = takeText,
                        subtitle = "Lembre-se de confirmar aqui depois que utilizar",
                        icon = Icons.Default.PlayArrow,
                        containerColor = SolSunGold,
                        contentColor = SolTextDark,
                        onClick = {
                            showTakeNowConfirmation = true
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Opção 2: Lembrar novamente
                    LargeDialogOption(
                        title = "Lembrar novamente",
                        subtitle = "Emitir novo alarme em 15 minutos",
                        icon = Icons.Default.AccessTime,
                        containerColor = StatusPostponedColor,
                        contentColor = Color.White,
                        onClick = onRemindLater
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Opção 3: Incerteza
                    LargeDialogOption(
                        title = uncertainText,
                        subtitle = "Registrar dúvida com segurança sem duplicar",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        containerColor = StatusUncertainColor,
                        contentColor = Color.White,
                        onClick = onUncertain
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                ) {
                    Text(
                        text = "Fechar",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )
                }
            },
            containerColor = SolCardSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Diálogo de confirmação para o fluxo "Vou tomar agora".
 * Apresenta pergunta explícita e botões grandes de SIM e AINDA NÃO.
 */
@Composable
fun TakeNowConfirmationDialog(
    medicationName: String,
    medicationType: MedicationType,
    onConfirmTaken: () -> Unit,
    onNotTakenYet: () -> Unit,
    onDismiss: () -> Unit
) {
    val isOral = medicationType == MedicationType.ORAL
    val question = if (isOral) {
        "Você já tomou seu remédio da pressão?"
    } else {
        "Você já aplicou o colírio nos dois olhos?"
    }
    val btnYesText = if (isOral) "✓ SIM, JÁ TOMEI" else "✓ SIM, JÁ APLIQUEI"
    val btnNoText = if (isOral) "✕ AINDA NÃO TOMEI" else "✕ AINDA NÃO APLIQUEI"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = medicationName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SolTextDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Apenas confirme depois de tomar ou aplicar o medicamento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SolTextDark.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Botão 1: SIM, JÁ TOMEI / APLIQUEI
                Button(
                    onClick = onConfirmTaken,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 58.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = btnYesText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        softWrap = true,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botão 2: AINDA NÃO TOMEI / APLIQUEI
                OutlinedButton(
                    onClick = onNotTakenYet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 58.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = btnNoText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark,
                        softWrap = true,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        containerColor = SolCardSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun CorrectionDialog(
    medicationName: String,
    currentStatus: DoseStatus,
    onConfirmCorrection: (newStatus: DoseStatus, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var reasonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Corrigir registro",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SolTextDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "$medicationName (Estado atual: ${currentStatus.label})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SolTextDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Selecione o estado correto:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SolTextDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                val selectableStatuses = listOf(
                    DoseStatus.CONFIRMED,
                    DoseStatus.PENDING,
                    DoseStatus.UNCERTAIN,
                    DoseStatus.NOT_TAKEN
                )

                selectableStatuses.forEach { status ->
                    val isSelected = selectedStatus == status
                    OutlinedButton(
                        onClick = { selectedStatus = status },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .defaultMinSize(minHeight = 52.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) SolGreen.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SolGreen else SolTextDark,
                                softWrap = true
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SolGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Motivo da correção (opcional)") },
                    placeholder = { Text("Ex: apertei sem querer") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (reasonText.isBlank()) "Correção manual pelo usuário" else reasonText
                    onConfirmCorrection(selectedStatus, finalReason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SolGreen),
                modifier = Modifier.defaultMinSize(minHeight = 48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Salvar Correção",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.defaultMinSize(minHeight = 48.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SolTextDark
                )
            }
        },
        containerColor = SolCardSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LargeDialogOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 72.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    softWrap = true,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.95f),
                    softWrap = true,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
