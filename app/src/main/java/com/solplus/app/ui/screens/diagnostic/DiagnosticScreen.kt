package com.solplus.app.ui.screens.diagnostic

import android.content.ActivityNotFoundException
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solplus.app.ui.components.SolNavigationHeader
import com.solplus.app.ui.theme.SolCardBg
import com.solplus.app.ui.theme.SolCardBorder
import com.solplus.app.ui.theme.SolCreamBg
import com.solplus.app.ui.theme.SolDarkGreen
import com.solplus.app.ui.theme.SolSunGold
import com.solplus.app.ui.theme.SolTextDark
import com.solplus.app.ui.theme.SolTextSecondary
import kotlinx.coroutines.launch

@Composable
fun DiagnosticScreen(
    viewModel: DiagnosticViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    Scaffold(
        topBar = {
            SolNavigationHeader(
                title = "Diagnóstico Xiaomi 14",
                icon = Icons.Default.Shield
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

                // Card "Testar alerta agora"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, SolSunGold, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Teste Imediato de Alerta",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SolTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toque abaixo para testar o som, vibração e banner do alerta no topo da tela do Xiaomi 14.",
                            fontSize = 15.sp,
                            color = SolTextDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.testAlertNow()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Alerta de teste emitido! Verifique a barra de notificações.")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Testar Alerta Agora",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Status das Permissões do Android
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Verificações do Sistema",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )

                    IconButton(onClick = { viewModel.refreshStatus() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar status",
                            tint = SolDarkGreen
                        )
                    }
                }
            }

            // 1. Notificações
            item {
                DiagnosticItemCard(
                    title = "Notificações do Aplicativo",
                    description = if (state.notificationsEnabled)
                        "Ativadas no sistema"
                    else
                        "Desativadas. O aplicativo não poderá emitir os lembretes sonoros.",
                    isOk = state.notificationsEnabled,
                    icon = Icons.Default.Notifications,
                    actionButtonText = if (!state.notificationsEnabled) "Ativar Notificações" else "Configurar",
                    onActionClick = {
                        try {
                            context.startActivity(viewModel.openNotificationSettings())
                        } catch (_: ActivityNotFoundException) {}
                    }
                )
            }

            // 2. Alarmes Exatos
            item {
                DiagnosticItemCard(
                    title = "Alarmes Exatos (AlarmManager)",
                    description = if (state.exactAlarmsEnabled)
                        "Permissão concedida para disparo pontual às 08:00 e 20:00"
                    else
                        "Necessário permitir para garantir horários pontuais sem atrasos.",
                    isOk = state.exactAlarmsEnabled,
                    icon = Icons.Default.Alarm,
                    actionButtonText = if (!state.exactAlarmsEnabled) "Permitir Alarmes" else null,
                    onActionClick = {
                        viewModel.openExactAlarmSettings()?.let { intent ->
                            try {
                                context.startActivity(intent)
                            } catch (_: ActivityNotFoundException) {}
                        }
                    }
                )
            }

            // 3. Otimização de Bateria
            item {
                DiagnosticItemCard(
                    title = "Economia de Bateria (Modo Ocioso)",
                    description = if (state.batteryOptimizationIgnored)
                        "Aplicativo isento de suspensão em segundo plano"
                    else
                        "O Android pode adiar lembretes com a tela apagada caso a economia esteja ativa.",
                    isOk = state.batteryOptimizationIgnored,
                    icon = Icons.Default.BatteryAlert,
                    actionButtonText = if (!state.batteryOptimizationIgnored) "Desativar Economia" else null,
                    onActionClick = {
                        try {
                            context.startActivity(viewModel.openBatteryOptimizationSettings())
                        } catch (_: ActivityNotFoundException) {}
                    }
                )
            }

            // 4. Seção HyperOS / Xiaomi
            item {
                Text(
                    text = "Ajustes do Xiaomi / HyperOS",
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
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = SolDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instruções para o Xiaomi 14",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SolTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Para que o HyperOS não encerre o aplicativo com a tela bloqueada:",
                            fontSize = 15.sp,
                            color = SolTextDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Início Automático (Autostart): Marque SOL+ como permitido.\n" +
                                    "• Economia de Bateria: Selecione 'Sem restrições'.\n" +
                                    "• Tela de Bloqueio: Permita exibir notificações na tela de bloqueio.",
                            fontSize = 14.sp,
                            color = SolTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        context.startActivity(viewModel.openXiaomiAutostartSettings())
                                    } catch (_: ActivityNotFoundException) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Abra Segurança > Permissões > Início Automático")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Início Automático", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        context.startActivity(viewModel.openXiaomiBatteryRestrictions())
                                    } catch (_: ActivityNotFoundException) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Abra Configurações > Bateria > Gerenciar apps")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Bateria Xiaomi", fontWeight = FontWeight.Bold)
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
}

@Composable
private fun DiagnosticItemCard(
    title: String,
    description: String,
    isOk: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionButtonText: String?,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SolCardBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SolCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SolDarkGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = SolTextDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = if (isOk) "OK" else "Atenção",
                        tint = if (isOk) Color(0xFF137333) else Color(0xFFDC2626),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOk) "OK" else "Atenção",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOk) Color(0xFF137333) else Color(0xFFDC2626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = SolTextSecondary
            )

            if (actionButtonText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SolDarkGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = actionButtonText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
