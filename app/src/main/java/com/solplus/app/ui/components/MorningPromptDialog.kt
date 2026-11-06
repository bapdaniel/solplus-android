package com.solplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.R
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.ui.theme.SolBtnPrimaryGreen
import com.solplus.app.ui.theme.SolBtnSecondaryBg
import com.solplus.app.ui.theme.SolBtnSecondaryText
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolInfoBoxBg
import com.solplus.app.ui.theme.SolInfoBoxBorder
import com.solplus.app.ui.theme.SolSunGold
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary

@Composable
fun MorningPromptDialog(
    bpDose: DoseWithMedication?,
    eyeDose: DoseWithMedication?,
    onConfirmDose: (occurrenceId: Long) -> Unit,
    onPostponeDose: (occurrenceId: Long) -> Unit,
    onUncertainDose: (occurrenceId: Long) -> Unit,
    onTakeNowDose: (occurrenceId: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var activeSubDialogMed by remember { mutableStateOf<DoseWithMedication?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pílula Superior "08:00 – Lembrete da manhã" (como na Tela 2)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3DC))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "08:00 – Lembrete da manhã",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Bom dia! ☀️",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
                Text(
                    text = "Hora de cuidar da sua saúde.",
                    fontSize = 17.sp,
                    color = SolTextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Cartão 1: Remédio da Pressão
                bpDose?.let { item ->
                    MorningPromptCard(
                        question = "Você já tomou seu remédio da pressão?",
                        isOral = true,
                        isResolved = item.occurrence.status == DoseStatus.CONFIRMED,
                        buttonYesText = "✓ SIM, JÁ TOMEI",
                        buttonNoText = "✕ NÃO, AINDA NÃO",
                        onYesClick = { onConfirmDose(item.occurrence.id) },
                        onNoClick = { activeSubDialogMed = item }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cartão 2: Colírio nos Dois Olhos
                eyeDose?.let { item ->
                    MorningPromptCard(
                        question = "Você já aplicou o colírio nos dois olhos?",
                        isOral = false,
                        isResolved = item.occurrence.status == DoseStatus.CONFIRMED,
                        buttonYesText = "✓ SIM, JÁ APLIQUEI",
                        buttonNoText = "✕ NÃO, AINDA NÃO",
                        onYesClick = { onConfirmDose(item.occurrence.id) },
                        onNoClick = { activeSubDialogMed = item }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rodapé Informativo (Tela 2 do mockup)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SolInfoBoxBg)
                        .border(1.dp, SolInfoBoxBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF137333),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cada resposta será registrada no seu calendário.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF137333)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Fechar Lembrete",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {},
        containerColor = SolCreamBg,
        shape = RoundedCornerShape(24.dp)
    )

    activeSubDialogMed?.let { item ->
        NotYetOptionsDialog(
            medicationName = item.medication.name,
            medicationType = item.medication.type,
            onTakeNow = {
                onTakeNowDose(item.occurrence.id)
                activeSubDialogMed = null
            },
            onRemindLater = {
                onPostponeDose(item.occurrence.id)
                activeSubDialogMed = null
            },
            onUncertain = {
                onUncertainDose(item.occurrence.id)
                activeSubDialogMed = null
            },
            onConfirmTaken = {
                onConfirmDose(item.occurrence.id)
                activeSubDialogMed = null
            },
            onDismiss = { activeSubDialogMed = null }
        )
    }
}

@Composable
fun NightPromptDialog(
    nightEyeDose: DoseWithMedication,
    onConfirmDose: (occurrenceId: Long) -> Unit,
    onPostponeDose: (occurrenceId: Long) -> Unit,
    onUncertainDose: (occurrenceId: Long) -> Unit,
    onTakeNowDose: (occurrenceId: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var showSubDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0F2FE))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF0369A1),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "20:00 – Lembrete da noite",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Boa noite! 🌙",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark
                )
                Text(
                    text = "Hora do seu cuidado noturno.",
                    fontSize = 17.sp,
                    color = SolTextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                MorningPromptCard(
                    question = "Você já aplicou o colírio nos dois olhos esta noite?",
                    isOral = false,
                    isResolved = nightEyeDose.occurrence.status == DoseStatus.CONFIRMED,
                    buttonYesText = "✓ SIM, JÁ APLIQUEI",
                    buttonNoText = "✕ NÃO, AINDA NÃO",
                    onYesClick = { onConfirmDose(nightEyeDose.occurrence.id) },
                    onNoClick = { showSubDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SolInfoBoxBg)
                        .border(1.dp, SolInfoBoxBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF137333),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cada resposta será registrada no seu calendário.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF137333)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Fechar Lembrete",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {},
        containerColor = SolCreamBg,
        shape = RoundedCornerShape(24.dp)
    )

    if (showSubDialog) {
        NotYetOptionsDialog(
            medicationName = nightEyeDose.medication.name,
            medicationType = nightEyeDose.medication.type,
            onTakeNow = {
                onTakeNowDose(nightEyeDose.occurrence.id)
                showSubDialog = false
            },
            onRemindLater = {
                onPostponeDose(nightEyeDose.occurrence.id)
                showSubDialog = false
            },
            onUncertain = {
                onUncertainDose(nightEyeDose.occurrence.id)
                showSubDialog = false
            },
            onConfirmTaken = {
                onConfirmDose(nightEyeDose.occurrence.id)
                showSubDialog = false
            },
            onDismiss = { showSubDialog = false }
        )
    }
}

@Composable
private fun MorningPromptCard(
    question: String,
    isOral: Boolean,
    isResolved: Boolean,
    buttonYesText: String,
    buttonNoText: String,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, SolCardBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SolCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone circular pastel (amarelo para cápsula, ciano/verde para colírio)
                val circleBg = if (isOral) Color(0xFFFFF1D6) else Color(0xFFE0F2FE)
                val iconTint = if (isOral) Color(0xFFE65100) else Color(0xFF0284C7)

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(circleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = if (isOral) R.drawable.ic_pill else R.drawable.ic_eye_drop),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = question,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SolTextDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isResolved) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE6F4EA))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF137333),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirmado com sucesso!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF137333)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Botão "SIM, JÁ TOMEI / APLIQUEI" (Verde Escuro)
                    Button(
                        onClick = onYesClick,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 52.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = buttonYesText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            softWrap = true,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Botão "NÃO, AINDA NÃO" (Creme Neutro com Alto Contraste)
                    Button(
                        onClick = onNoClick,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 52.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SolBtnSecondaryBg,
                            contentColor = SolBtnSecondaryText
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = buttonNoText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SolBtnSecondaryText,
                            softWrap = true,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
