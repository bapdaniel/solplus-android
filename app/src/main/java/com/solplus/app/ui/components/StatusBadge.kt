package com.solplus.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.solplus.app.ui.theme.SolBadgeConfirmedBg
import com.solplus.app.ui.theme.SolBadgeConfirmedText
import com.solplus.app.ui.theme.SolBadgePendingBg
import com.solplus.app.ui.theme.SolBadgePendingText
import com.solplus.app.ui.theme.SolBadgePostponedBg
import com.solplus.app.ui.theme.SolBadgePostponedText
import com.solplus.app.ui.theme.SolBadgeUncertainBg
import com.solplus.app.ui.theme.SolBadgeUncertainText

@Composable
fun StatusBadge(
    status: DoseStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        DoseStatus.SCHEDULED -> Triple(SolBadgePendingBg, SolBadgePendingText, "Programado")
        DoseStatus.PENDING -> Triple(SolBadgePendingBg, SolBadgePendingText, "Pendente")
        DoseStatus.CONFIRMED -> Triple(SolBadgeConfirmedBg, SolBadgeConfirmedText, "Confirmado")
        DoseStatus.POSTPONED -> Triple(SolBadgePostponedBg, SolBadgePostponedText, "Adiado")
        DoseStatus.UNCERTAIN -> Triple(SolBadgeUncertainBg, SolBadgeUncertainText, "Incerto")
        DoseStatus.NOT_CONFIRMED -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Não confirmado")
        DoseStatus.NOT_TAKEN -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), "Não realizado")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Situação: $label. ${status.description}"
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (status == DoseStatus.CONFIRMED) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
