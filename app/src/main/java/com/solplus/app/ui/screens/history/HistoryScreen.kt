package com.solplus.app.ui.screens.history

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.ui.components.SolNavigationHeader
import com.solplus.app.ui.components.StatusBadge
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val historyDoses by viewModel.historyDoses.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val selectedFilterId by viewModel.selectedFilterMedicationId.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val ptLocale = Locale("pt", "BR")
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", ptLocale)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", ptLocale)

    Scaffold(
        topBar = {
            SolNavigationHeader(
                title = "Histórico",
                icon = Icons.Default.History
            )
        },
        containerColor = SolCreamBg
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Abas: Doses Registradas vs Trilha de Correções
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SolCardBg,
                contentColor = SolDarkGreen,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SolDarkGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Text(
                            text = "Doses Registradas",
                            fontSize = 16.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) SolDarkGreen else SolTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Text(
                            text = "Trilha de Correções",
                            fontSize = 16.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) SolDarkGreen else SolTextSecondary
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // Filtros de Medicamento
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilterId == null,
                            onClick = { viewModel.selectFilter(null) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SolDarkGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(medications) { med ->
                        FilterChip(
                            selected = selectedFilterId == med.id,
                            onClick = { viewModel.selectFilter(med.id) },
                            label = { Text("${med.name} (${med.period})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SolDarkGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Lista de Doses
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (historyDoses.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhum histórico encontrado para o filtro selecionado.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = SolTextSecondary,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    } else {
                        items(historyDoses, key = { it.occurrence.id }) { item ->
                            val occ = item.occurrence
                            val med = item.medication

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.dp, SolCardBorder, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SolCardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = occ.scheduledDate.format(dateFormatter),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SolTextDark
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = med.scheduledTime.format(timeFormatter) + " (${med.period})",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SolDarkGreen
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = med.name,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SolTextDark
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatusBadge(status = occ.status)

                                        if (occ.confirmedAt != null) {
                                            Text(
                                                text = "Confirmado às " + occ.confirmedAt.format(timeFormatter),
                                                fontSize = 14.sp,
                                                color = SolDarkGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            } else {
                // Aba de Auditoria
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (auditLogs.isEmpty()) {
                        item {
                            Text(
                                text = "Nenhuma alteração manual ou auditoria registrada.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = SolTextSecondary,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    } else {
                        items(auditLogs, key = { it.id }) { log ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, SolCardBorder, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SolCardBg)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.timestamp.format(dateFormatter) + " às " + log.timestamp.format(timeFormatter),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SolTextSecondary
                                        )

                                        Text(
                                            text = "Novo: ${log.newStatus.label}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SolDarkGreen
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (log.previousStatus != null) {
                                        Text(
                                            text = "Estado anterior: ${log.previousStatus.label}",
                                            fontSize = 14.sp,
                                            color = SolTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Motivo: ${log.reason}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SolTextDark
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
