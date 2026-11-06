package com.solplus.app.ui.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.domain.model.DoseStatus
import com.solplus.app.ui.components.DoseCard
import com.solplus.app.ui.components.MorningPromptDialog
import com.solplus.app.ui.components.NightPromptDialog
import com.solplus.app.ui.components.SolBrandHeader
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolSunGold
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val doses by viewModel.todayDoses.collectAsState()
    val isMorningDialogVisible by viewModel.isMorningDialogVisible.collectAsState()
    val isNightDialogVisible by viewModel.isNightDialogVisible.collectAsState()

    val confirmedCount = doses.count { it.occurrence.status == DoseStatus.CONFIRMED }
    val totalCount = 3

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Bom dia! ☀️"
        in 12..17 -> "Boa tarde! ☀️"
        else -> "Boa noite! 🌙"
    }

    val ptLocale = Locale("pt", "BR")
    val today = LocalDate.now()
    val formattedDate = today.format(
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", ptLocale)
    ).replaceFirstChar { it.uppercase() }

    Scaffold(
        topBar = {
            SolBrandHeader()
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

                // Saudação "Bom dia! ☀️" e "Hoje é [Data]"
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = greeting,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hoje é",
                        fontSize = 18.sp,
                        color = SolTextSecondary
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra / Card de Progresso: "X de 3 cuidados confirmados" (estilo do mockup)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFDCE7DF), RoundedCornerShape(16.dp))
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Progresso de hoje: $confirmedCount de $totalCount cuidados confirmados."
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF5EE))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Círculo Verde com Checkmark
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "$confirmedCount de $totalCount cuidados confirmados",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Barra de Progresso Suave
                        LinearProgressIndicator(
                            progress = { confirmedCount.toFloat() / totalCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2E7D32),
                            trackColor = Color(0xFFCEE5D6)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Os 3 Cartões Diários de Cuidados da Tela 1
            items(doses, key = { it.occurrence.id }) { doseWithMedication ->
                DoseCard(
                    doseWithMedication = doseWithMedication,
                    onConfirm = { viewModel.confirmDose(it) },
                    onTakeNow = { viewModel.takeNow(it) },
                    onPostpone = { viewModel.postponeDose(it, isMorning = doseWithMedication.medication.period == "MANHÃ") },
                    onUncertain = { viewModel.markUncertain(it) },
                    onCorrectStatus = { occId, newStatus, reason ->
                        viewModel.correctDoseStatus(occId, newStatus, reason)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Modal de Alerta Agrupado Matinal (08:00)
    if (isMorningDialogVisible) {
        val bpDose = viewModel.getMorningPressureDose(doses)
        val eyeDose = viewModel.getMorningEyeDose(doses)
        MorningPromptDialog(
            bpDose = bpDose,
            eyeDose = eyeDose,
            onConfirmDose = { viewModel.confirmDose(it) },
            onPostponeDose = { viewModel.postponeDose(it, isMorning = true) },
            onUncertainDose = { viewModel.markUncertain(it) },
            onTakeNowDose = { viewModel.takeNow(it) },
            onDismiss = { viewModel.hideMorningPrompt() }
        )
    }

    // Modal de Alerta Noturno (20:00)
    if (isNightDialogVisible) {
        val nightEyeDose = viewModel.getNightEyeDose(doses)
        nightEyeDose?.let { item ->
            NightPromptDialog(
                nightEyeDose = item,
                onConfirmDose = { viewModel.confirmDose(it) },
                onPostponeDose = { viewModel.postponeDose(it, isMorning = false) },
                onUncertainDose = { viewModel.markUncertain(it) },
                onTakeNowDose = { viewModel.takeNow(it) },
                onDismiss = { viewModel.hideNightPrompt() }
            )
        }
    }
}
