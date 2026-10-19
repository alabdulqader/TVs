package com.example.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WbSunny
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
import com.example.data.model.AnnouncementItem
import com.example.data.model.DisplayScreen
import com.example.data.model.StockQuote
import com.example.data.model.WeatherData
import com.example.ui.components.MediaSlideshow
import com.example.ui.components.NewsTickerBar
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdvertisingDisplayTemplate(
    screen: DisplayScreen,
    weather: WeatherData,
    announcements: List<AnnouncementItem>,
    stocks: List<StockQuote>,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val timeFormat = remember(screen.clockFormat24) {
        if (screen.clockFormat24) SimpleDateFormat("HH:mm", Locale.ENGLISH)
        else SimpleDateFormat("hh:mm a", Locale("ar"))
    }

    val dateFormat = remember {
        SimpleDateFormat("d MMMM yyyy", Locale("ar"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Main Center Area (Large Ad on Left ~78%, Sidebar on Right ~22%)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Large Media Banner (78%)
            Box(
                modifier = Modifier
                    .weight(0.78f)
                    .fillMaxHeight()
            ) {
                MediaSlideshow(
                    announcements = announcements,
                    modifier = Modifier.fillMaxSize(),
                    isLargeBanner = true
                )
            }

            // Compact Lateral Sidebar (22%)
            Surface(
                modifier = Modifier
                    .weight(0.22f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top: Brand Emblem & Screen Identity
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
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
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Text(
                            text = screen.companyName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 2
                        )

                        Text(
                            text = screen.name,
                            fontSize = 12.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Middle: Weather card
                    if (screen.weatherEnabled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0F172A))
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(32.dp)
                            )

                            Text(
                                text = "${weather.temperature.toInt()}°C",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )

                            Text(
                                text = weather.cityNameAr,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccent
                            )

                            Text(
                                text = weather.conditionText,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Text(
                                text = "رطوبة: ${weather.humidity}%",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Bottom: Clock & Quick settings trigger
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = timeFormat.format(currentTime),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = dateFormat.format(currentTime),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Button(
                            onClick = onOpenControlPanel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CardBorder,
                                contentColor = CyanAccent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "لوحة التحكم", fontSize = 12.sp)
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
