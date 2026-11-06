package com.solplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.R
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary
import java.time.format.DateTimeFormatter

@Composable
fun DoseCard(
    doseWithMedication: DoseWithMedication,
    onConfirm: (occurrenceId: Long) -> Unit,
    onTakeNow: (occurrenceId: Long) -> Unit,
    onPostpone: (occurrenceId: Long) -> Unit,
    onUncertain: (occurrenceId: Long) -> Unit,
    onCorrectStatus: (occurrenceId: Long, newStatus: DoseStatus, reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val occ = doseWithMedication.occurrence
    val med = doseWithMedication.medication
    val isOral = med.type == MedicationType.ORAL

    var showActionDialog by remember { mutableStateOf(false) }
    var showCorrectionDialog by remember { mutableStateOf(false) }

    val formattedTime = med.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    // Cores de círculo do ícone
    val circleBg = if (isOral) Color(0xFFFFF1D6) else Color(0xFFE0F2FE)
    val circleIconTint = if (isOral) Color(0xFFE65100) else Color(0xFF0284C7)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = SolCardBorder, shape = RoundedCornerShape(20.dp))
            .clickable {
                if (occ.status == DoseStatus.CONFIRMED) {
                    showCorrectionDialog = true
                } else {
                    showActionDialog = true
                }
            }
            .semantics(mergeDescendants = true) {
                contentDescription = "${med.name}, às $formattedTime. Situação: ${occ.status.label}. Toque para responder ou alterar."
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SolCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Ícone circular pastel do remédio
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(circleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = if (isOral) R.drawable.ic_pill else R.drawable.ic_eye_drop),
                        contentDescription = null,
                        tint = circleIconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Informações do Medicamento e Status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = med.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark,
                        modifier = Modifier.fillMaxWidth(),
                        softWrap = true
                    )

                    if (!isOral) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ambos os olhos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SolTextSecondary,
                            fontSize = 15.sp,
                            softWrap = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = SolTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SolTextSecondary,
                                fontSize = 15.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = occ.status)

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Botão destacado no cartão para confirmar posteriormente na tela inicial
            if (occ.status != DoseStatus.CONFIRMED) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { onConfirm(occ.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 52.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                    colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOral) "Confirmar que tomei" else "Confirmar que apliquei",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        softWrap = true,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    if (showActionDialog) {
        NotYetOptionsDialog(
            medicationName = med.name,
            medicationType = med.type,
            onTakeNow = {
                showActionDialog = false
                onTakeNow(occ.id)
            },
            onRemindLater = {
                showActionDialog = false
                onPostpone(occ.id)
            },
            onUncertain = {
                showActionDialog = false
                onUncertain(occ.id)
            },
            onConfirmTaken = {
                showActionDialog = false
                onConfirm(occ.id)
            },
            onDismiss = { showActionDialog = false }
        )
    }

    if (showCorrectionDialog) {
        CorrectionDialog(
            medicationName = med.name,
            currentStatus = occ.status,
            onConfirmCorrection = { newStatus, reason ->
                showCorrectionDialog = false
                onCorrectStatus(occ.id, newStatus, reason)
            },
            onDismiss = { showCorrectionDialog = false }
        )
    }
}
