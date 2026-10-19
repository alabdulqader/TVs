package com.example.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
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
import com.example.ui.components.MediaSlideshow
import com.example.ui.components.NewsTickerBar
import com.example.ui.components.SignageHeader
import com.example.ui.theme.*

@Composable
fun CorporateDashboardTemplate(
    screen: DisplayScreen,
    weather: WeatherData,
    announcements: List<AnnouncementItem>,
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
        // Top Broadcast Header
        SignageHeader(
            screen = screen,
            weather = weather,
            onOpenControlPanel = onOpenControlPanel
        )

        // Main Center Split (60% Slideshow / 40% Meetings & Tasks)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Corporate Media Slideshow (60%)
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                MediaSlideshow(
                    announcements = announcements,
                    modifier = Modifier.fillMaxSize(),
                    isLargeBanner = false
                )
            }

            // Right: Meetings & Tasks Columns (40%)
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Today's Meetings
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "جدول اجتماعات ومواعيد اليوم",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (meetings.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد اجتماعات متبقية لليوم",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(meetings) { item ->
                                    MeetingItemRow(item)
                                }
                            }
                        }
                    }
                }

                // Card 2: Operations & Action Tasks
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "مستجدات المهام والأعمال",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (tasks.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "كافة المهام العامة مكتملة",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(tasks) { task ->
                                    TaskItemRow(task)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Broadcast News & Stocks Ticker
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
private fun MeetingItemRow(item: MeetingItem) {
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
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = "${item.room} • ${item.organizer}",
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.timeRange,
                fontSize = 11.sp,
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
                        text = "الآن",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemRow(task: SignageTask) {
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
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )

            Column {
                Text(
                    text = task.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1
                )
                if (task.assignee.isNotEmpty()) {
                    Text(
                        text = "المسؤول: ${task.assignee}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Text(
            text = task.dueTime,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
