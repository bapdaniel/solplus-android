package com.solplus.app.ui.screens.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.core.testing.TestLabManager
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.ui.components.SolNavigationHeader
import com.solplus.app.ui.theme.SolBadgeConfirmedBg
import com.solplus.app.ui.theme.SolBadgeConfirmedText
import com.solplus.app.ui.theme.SolBadgePendingBg
import com.solplus.app.ui.theme.SolBadgePendingText
import com.solplus.app.ui.theme.SolBadgePostponedBg
import com.solplus.app.ui.theme.SolBadgePostponedText
import com.solplus.app.ui.theme.SolBtnPrimaryGreen
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
fun LabScreen(
    labManager: TestLabManager,
    onNavigateBack: () -> Unit
) {
    val events by labManager.events.collectAsState()
    val simulatedDoses by labManager.simulatedDoses.collectAsState()
    var permissionsMap by remember { mutableStateOf(labManager.checkPermissionsStatus()) }
    var nextAlarmFormatted by remember { mutableStateOf(labManager.getNextAlarmFormatted()) }

    Scaffold(
        topBar = {
            SolNavigationHeader(
                title = "Laboratório SOL+",
                onBackClick = onNavigateBack
            )
        },
        containerColor = SolCreamBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner de Isolamento de Dados
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SolInfoBoxBg)
                        .border(1.dp, SolInfoBoxBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🧪 DADOS DE TESTE ISOLADOS: As simulações realizadas neste painel operam em memória e NÃO modificam o histórico ou as doses reais da usuária no banco de dados.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SolDarkGreen,
                        lineHeight = 20.sp
                    )
                }
            }

            // Card 1: Diagnóstico de Permissões e AlarmManager Real
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = SolDarkGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Permissões e AlarmManager",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolTextDark
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    permissionsMap = labManager.checkPermissionsStatus()
                                    nextAlarmFormatted = labManager.getNextAlarmFormatted()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Atualizar", modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        permissionsMap.forEach { (perm, status) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = perm,
                                    fontSize = 14.sp,
                                    color = SolTextDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (status) SolBadgeConfirmedBg else Color(0xFFFFEBEE))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (status) "ATIVO" else "INATIVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (status) SolBadgeConfirmedText else Color(0xFFC62828)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Próximo alarme no SO: $nextAlarmFormatted",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SolTextSecondary
                        )
                    }
                }
            }

            // Card 2: Testes com Hardware Real (Alarme Real, Som e Vibração)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = SolDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Testes de Hardware & Notificação Real",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { labManager.scheduleRealTestAlarm(30) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SolBtnPrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agendar Alarme Real em 30 Segundos", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { labManager.testSoundAndVibration() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = SolDarkGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Testar Som e Vibração Agora", color = SolDarkGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Card 3: Validação das Regras de Negócio (Doses Simuladas)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Regras de Negócio (Doses)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                            TextButton(onClick = { labManager.resetSimulatedDoses() }) {
                                Text("Resetar Doses", color = SolDarkGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        simulatedDoses.forEach { dose ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(SolCreamBg, RoundedCornerShape(14.dp))
                                    .border(1.dp, SolCardBorder, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dose.medicationName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SolTextDark
                                        )
                                        Text(
                                            text = "Horário: ${dose.scheduledTime}",
                                            fontSize = 13.sp,
                                            color = SolTextSecondary
                                        )
                                    }

                                    val (badgeBg, badgeColor, badgeText) = when (dose.status) {
                                        DoseStatus.CONFIRMED -> Triple(SolBadgeConfirmedBg, SolBadgeConfirmedText, "CONFIRMADO")
                                        DoseStatus.POSTPONED -> Triple(SolBadgePostponedBg, SolBadgePostponedText, "ADIADO 15M")
                                        else -> Triple(SolBadgePendingBg, SolBadgePendingText, "PENDENTE")
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(badgeBg)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = badgeColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { labManager.simulateConfirm(dose.id) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("✓ SIM", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { labManager.simulateNotYet(dose.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("✕ NÃO", fontSize = 12.sp, color = SolTextDark, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { labManager.simulateTakeNow(dose.id) },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Tomar Agora", fontSize = 11.sp, color = SolTextDark)
                                    }

                                    OutlinedButton(
                                        onClick = { labManager.simulatePostpone(dose.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Adiar", fontSize = 11.sp, color = SolTextDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Card 4: Log em Tempo Real de Eventos de Teste
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SolCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Histórico de Ações do Laboratório",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SolTextDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (events.isEmpty()) {
                            Text(
                                text = "Nenhuma ação executada ainda nesta sessão.",
                                fontSize = 14.sp,
                                color = SolTextSecondary
                            )
                        } else {
                            events.take(10).forEach { event ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(SolCreamBg, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = event.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (event.isSuccess) SolDarkGreen else Color(0xFFC62828)
                                        )
                                        Text(
                                            text = event.timestamp,
                                            fontSize = 12.sp,
                                            color = SolTextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = event.description,
                                        fontSize = 13.sp,
                                        color = SolTextDark,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
