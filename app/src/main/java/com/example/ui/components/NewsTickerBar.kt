package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockQuote
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun NewsTickerBar(
    tickerText: String,
    stocks: List<StockQuote>,
    showStocks: Boolean = true,
    speedSeconds: Int = 30,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Continuous auto scrolling marquee
    LaunchedEffect(scrollState.maxValue, speedSeconds) {
        if (scrollState.maxValue > 0) {
            while (true) {
                val max = scrollState.maxValue
                val duration = speedSeconds * 1000
                scrollState.animateScrollTo(
                    value = max,
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = LinearEasing
                    )
                )
                delay(400)
                scrollState.scrollTo(0)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TickerBg)
    ) {
        // Stocks strip (if enabled)
        if (showStocks && stocks.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Stocks",
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "مؤشرات السوق المالي (تداول)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Text(
                        text = "• متأخرة 15 دقيقة",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stocks.forEach { stock ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stock.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "%.2f".format(stock.price),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Icon(
                                imageVector = if (stock.isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (stock.isPositive) EmeraldSuccess else CrimsonAlert,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${if (stock.isPositive) "+" else ""}${stock.changePercent}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stock.isPositive) EmeraldSuccess else CrimsonAlert
                            )
                        }
                    }
                }
            }
        }

        // News Marquee Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF0B132B)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Static Breaking / News Tag
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(CrimsonAlert)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "News",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "شريط الأخبار",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Scrolling content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = tickerText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1
                )

                Text(
                    text = "◆",
                    color = CyanAccent,
                    fontSize = 12.sp
                )

                Text(
                    text = "وكالة الأنباء السعودية: استمرار برامج التحول الرقمي وتسريع الابتكار التقني",
                    fontSize = 15.sp,
                    color = TextSecondary,
                    maxLines = 1
                )

                Text(
                    text = "◆",
                    color = CyanAccent,
                    fontSize = 12.sp
                )

                Text(
                    text = "تحديثات لحظية بدون إعادة تشغيل التطبيق • يعمل بكفاءة مع استقرار تام دون انقطاع",
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
            }
        }
    }
}
