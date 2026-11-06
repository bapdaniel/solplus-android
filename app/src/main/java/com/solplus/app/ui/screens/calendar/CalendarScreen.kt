package com.solplus.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.R
import com.solplus.app.data.local.entity.DoseWithMedication
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.domain.model.MedicationType
import com.solplus.app.ui.components.CorrectionDialog
import com.solplus.app.ui.components.SolNavigationHeader
import com.solplus.app.ui.components.StatusBadge
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val monthDoses by viewModel.monthDoses.collectAsState()
    val selectedDayDoses by viewModel.selectedDayDoses.collectAsState()

    val ptLocale = Locale("pt", "BR")
    val monthTitle = currentMonth.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", ptLocale))
        .replaceFirstChar { it.uppercase() }

    val daysInMonth = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Domingo

    var correctionTargetItem by remember { mutableStateOf<DoseWithMedication?>(null) }

    val confirmedCount = selectedDayDoses.count { it.occurrence.status == DoseStatus.CONFIRMED }
    val totalCount = 3

    val formattedSelectedDay = selectedDate.format(
        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ptLocale)
    )

    Scaffold(
        topBar = {
            SolNavigationHeader(
                title = "Calendário",
                icon = Icons.Default.CalendarMonth
            )
        },
        containerColor = SolCreamBg
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Card do Calendário (Estilo Tela 3 do mockup)
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
                        // Seletor de Mês (Pílula com setas e nome do mês)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3EFE6))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { viewModel.previousMonth() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Mês anterior",
                                        tint = SolDarkGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Text(
                                    text = monthTitle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolTextDark
                                )

                                IconButton(
                                    onClick = { viewModel.nextMonth() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Próximo mês",
                                        tint = SolDarkGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cabeçalho dos Dias da Semana (D, S, T, Q, Q, S, S)
                        val weekDays = listOf("D", "S", "T", "Q", "Q", "S", "S")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            weekDays.forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolTextDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grade dos Dias com Indicadores de Checkmark Verde
                        val totalSlots = firstDayOfWeek + daysInMonth.size
                        val totalRows = (totalSlots + 6) / 7

                        for (row in 0 until totalRows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val index = row * 7 + col
                                    val dayIndex = index - firstDayOfWeek

                                    if (dayIndex in daysInMonth.indices) {
                                        val date = daysInMonth[dayIndex]
                                        val isSelected = date == selectedDate

                                        // Busca as doses deste dia
                                        val dayDoses = monthDoses.filter { it.occurrence.scheduledDate == date }
                                        val hasConfirmed = dayDoses.any { it.occurrence.status == DoseStatus.CONFIRMED }
                                        val allConfirmed = dayDoses.isNotEmpty() && dayDoses.all { it.occurrence.status == DoseStatus.CONFIRMED }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) SolDarkGreen else Color.Transparent)
                                                .clickable { viewModel.selectDate(date) }
                                                .semantics {
                                                    contentDescription = "Dia ${date.dayOfMonth}. " +
                                                            if (allConfirmed) "Todos os cuidados confirmados."
                                                            else if (hasConfirmed) "Parcialmente confirmado."
                                                            else ""
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = date.dayOfMonth.toString(),
                                                    fontSize = 16.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else SolTextDark
                                                )

                                                // Ícone de Checkmark verde idêntico ao mockup
                                                if (!isSelected && hasConfirmed) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFC8E6C9)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = Color(0xFF2E7D32),
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                } else if (isSelected && hasConfirmed) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clip(CircleShape)
                                                            .background(Color.White),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = SolDarkGreen,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Seção Inferior: Detalhes dos Cuidados do Dia Selecionado (Tela 3)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = formattedSelectedDay,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )
                    Text(
                        text = "Cuidados do dia ($confirmedCount de $totalCount)",
                        fontSize = 16.sp,
                        color = SolTextSecondary
                    )
                }
            }

            // Lista dos 3 Cuidados do Dia Selecionado
            items(selectedDayDoses, key = { it.occurrence.id }) { item ->
                val med = item.medication
                val occ = item.occurrence
                val isOral = med.type == MedicationType.ORAL

                val circleBg = if (isOral) Color(0xFFFFF1D6) else Color(0xFFE0F2FE)
                val iconTint = if (isOral) Color(0xFFE65100) else Color(0xFF0284C7)
                val timeStr = med.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                val displayName = if (isOral) {
                    "${med.name} – $timeStr"
                } else {
                    "Colírio (ambos os olhos) – $timeStr"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SolCardBorder, RoundedCornerShape(16.dp))
                        .clickable { correctionTargetItem = item },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(circleBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isOral) R.drawable.ic_pill else R.drawable.ic_eye_drop),
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = occ.status)

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { correctionTargetItem = item },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Corrigir registro",
                                    tint = Color(0xFF9E9E9E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    correctionTargetItem?.let { item ->
        CorrectionDialog(
            medicationName = item.medication.name,
            currentStatus = item.occurrence.status,
            onConfirmCorrection = { newStatus, reason ->
                viewModel.correctDoseStatus(item.occurrence.id, newStatus, reason)
                correctionTargetItem = null
            },
            onDismiss = { correctionTargetItem = null }
        )
    }
}
