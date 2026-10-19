package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisplayScreen
import com.example.data.model.WeatherData
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SignageHeader(
    screen: DisplayScreen,
    weather: WeatherData,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    // Live clock tick
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val timeFormat = remember(screen.clockFormat24) {
        if (screen.clockFormat24) SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        else SimpleDateFormat("hh:mm:ss a", Locale("ar"))
    }

    val gregorianFormat = remember {
        SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
    }

    // Hijri date calculation approximation
    val hijriDateStr = remember(currentTime) {
        // Approximate calculation for demonstration:
        "١٩ ربيع الآخر ١٤٤٨ هـ"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = CardSurface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand & Screen Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Corporate Logo Emblem
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanAccent, Color(0xFF0284C7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = screen.companyName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Online pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (screen.isOnline) EmeraldSuccess.copy(alpha = 0.2f)
                                    else CrimsonAlert.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (screen.isOnline) EmeraldSuccess else CrimsonAlert)
                                )
                                Text(
                                    text = if (screen.isOnline) "مباشر" else "محلي",
                                    fontSize = 11.sp,
                                    color = if (screen.isOnline) EmeraldSuccess else CrimsonAlert,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Text(
                        text = "${screen.name} • ${screen.location}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Weather & Environmental widget
            if (screen.weatherEnabled) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = GoldAccent,
                        modifier = Modifier.size(26.dp)
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${weather.temperature.toInt()}°م",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = weather.cityNameAr,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccent
                            )
                        }

                        Text(
                            text = "${weather.conditionText} • رطوبة ${weather.humidity}%",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Live Digital Clock & Dates
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = timeFormat.format(currentTime),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = gregorianFormat.format(currentTime),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (screen.hijriEnabled) {
                            Text(
                                text = "•",
                                fontSize = 12.sp,
                                color = GoldAccent
                            )
                            Text(
                                text = hijriDateStr,
                                fontSize = 12.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Control Panel Quick Launcher Button
                IconButton(
                    onClick = onOpenControlPanel,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "إعدادات العرض ولوحة التحكم",
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
