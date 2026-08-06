package com.nexus.inventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.inventory.domain.model.StockStatus
import com.nexus.inventory.ui.theme.DangerRed
import com.nexus.inventory.ui.theme.NeonGreen
import com.nexus.inventory.ui.theme.WarningYellow

@Composable
fun StockBadge(status: StockStatus, count: Int, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor, label) = when (status) {
        StockStatus.SUFFICIENT -> Triple(
            NeonGreen.copy(alpha = 0.15f),
            NeonGreen,
            "Suficiente ($count)"
        )
        StockStatus.LOW -> Triple(
            WarningYellow.copy(alpha = 0.15f),
            WarningYellow,
            "Bajo Stock ($count)"
        )
        StockStatus.OUT_OF_STOCK -> Triple(
            DangerRed.copy(alpha = 0.15f),
            DangerRed,
            "Agotado (0)"
        )
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
