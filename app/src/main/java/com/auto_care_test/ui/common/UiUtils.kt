package com.auto_care_test.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.auto_care_test.ui.theme.StatusPendiente
import com.auto_care_test.ui.theme.StatusPendienteContainer
import com.auto_care_test.ui.theme.StatusRealizado
import com.auto_care_test.ui.theme.StatusRealizadoContainer
import com.auto_care_test.ui.theme.StatusVencido
import com.auto_care_test.ui.theme.StatusVencidoContainer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Locale.forLanguageTag

fun estadoColor(estado: String): Color = when (estado) {
    "Pendiente" -> StatusPendiente
    "Realizado" -> StatusRealizado
    "Vencido"   -> StatusVencido
    else        -> Color.Gray
}

fun estadoContainerColor(estado: String): Color = when (estado) {
    "Pendiente" -> StatusPendienteContainer
    "Realizado" -> StatusRealizadoContainer
    "Vencido"   -> StatusVencidoContainer
    else        -> Color(0xFFF5F5F5)
}

fun formatFecha(raw: String): String = try {
    LocalDate.parse(raw)
        .format(DateTimeFormatter.ofPattern("dd MMM yyyy", forLanguageTag("es")))
} catch (e: Exception) { raw }

@Composable
fun StatusChip(estado: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(estadoContainerColor(estado))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = estadoColor(estado)
        )
    }
}
