package com.example.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.NewsTickerBar
import com.example.ui.components.SignageHeader
import com.example.ui.theme.*

@Composable
fun ExecutiveDashboardTemplate(
    screen: DisplayScreen,
    weather: WeatherData,
    kpis: List<KpiMetric>,
    meetings: List<MeetingItem>,
    tasks: List<SignageTask>,
    stocks: List<StockQuote>,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header
        SignageHeader(
            screen = screen,
            weather = weather,
            onOpenControlPanel = onOpenControlPanel
        )

        // KPI Metric Cards Row (4 Cards)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            kpis.take(4).forEach { kpi ->
                KpiCard(kpi = kpi, modifier = Modifier.weight(1f))
            }
        }

        // Center Split: Left = Boardroom Schedule & Announcements, Right = Strategic Tasks & Market Overview
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Executive Schedule & Board Sessions (50%)
            Surface(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "جدول جلسات واجتماعات الإدارة التنفيذية",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(meetings) { item ->
                            val isCurrent = item.status == "current"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF1E2D4A) else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isCurrent) CyanAccent.copy(alpha = 0.5f) else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${item.room} • منظم الاجتماع: ${item.organizer}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.timeRange,
                                        fontSize = 12.sp,
                                        color = if (isCurrent) CyanAccent else GoldAccent,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(EmeraldSuccess.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "جاري الآن",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldSuccess
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Column: Priority Strategic Tasks & Milestones (50%)
            Surface(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(16.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "المبادرات الاستراتيجية ومؤشرات الإنجاز",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks) { task ->
                            val priorityColor = when (task.priority.uppercase()) {
                                "HIGH" -> CrimsonAlert
                                "LOW" -> EmeraldSuccess
                                else -> GoldAccent
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(priorityColor)
                                    )
                                    Column {
                                        Text(
                                            text = task.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        if (task.description.isNotEmpty()) {
                                            Text(
                                                text = task.description,
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = task.dueTime,
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Ticker
        if (screen.tickerEnabled) {
            NewsTickerBar(
                tickerText = screen.customTickerText,
                stocks = stocks,
                showStocks = screen.stockEnabled,
                speedSeconds = screen.tickerSpeed
            )
        }
    }
}

@Composable
private fun KpiCard(
    kpi: KpiMetric,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = kpi.title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Icon(
                    imageVector = when (kpi.trend) {
                        "up" -> Icons.Default.TrendingUp
                        "down" -> Icons.Default.TrendingDown
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    tint = if (kpi.trend == "up") EmeraldSuccess else GoldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = kpi.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            if (kpi.subtitle.isNotEmpty()) {
                Text(
                    text = kpi.subtitle,
                    fontSize = 10.sp,
                    color = if (kpi.trend == "up") EmeraldSuccess else TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
