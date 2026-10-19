package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.example.data.model.AnnouncementItem
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MediaSlideshow(
    announcements: List<AnnouncementItem>,
    modifier: Modifier = Modifier,
    isLargeBanner: Boolean = false
) {
    if (announcements.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(CardSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "لا توجد إعلانات نشطة حالياً",
                color = TextSecondary,
                fontSize = 18.sp
            )
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentItem = announcements.getOrElse(currentIndex % announcements.size) { announcements[0] }
    val durationSeconds = currentItem.durationSeconds.coerceAtLeast(4)

    // Progress bar animation
    var progressTarget by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(
            durationMillis = durationSeconds * 1000,
            easing = LinearEasing
        ),
        label = "progress"
    )

    // Slide loop timer
    LaunchedEffect(currentIndex, durationSeconds) {
        progressTarget = 0f
        delay(50)
        progressTarget = 1f
        delay((durationSeconds * 1000L) - 50)
        currentIndex = (currentIndex + 1) % announcements.size
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp)),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = currentItem,
                animationSpec = tween(700),
                label = "slideshow"
            ) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF131D33),
                                    Color(0xFF0F172A),
                                    Color(0xFF070B14)
                                )
                            )
                        )
                ) {
                    // Optional network / cache image background
                    if (item.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f))
                        )
                    }

                    // Decorative glow
                    Box(
                        modifier = Modifier
                            .size(350.dp)
                            .align(Alignment.TopStart)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(item.accentColor).copy(alpha = 0.18f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Content Container
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isLargeBanner) 36.dp else 24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header with Badge & Slide counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(item.accentColor).copy(alpha = 0.25f))
                                        .border(
                                            1.dp,
                                            Color(item.accentColor).copy(alpha = 0.6f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Campaign,
                                            contentDescription = null,
                                            tint = Color(item.accentColor),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = item.badge,
                                            fontSize = if (isLargeBanner) 15.sp else 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(item.accentColor)
                                        )
                                    }
                                }
                            }

                            // Slide indicator: "2 / 3"
                            Surface(
                                color = Color(0xFF0F172A).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${currentIndex + 1} / ${announcements.size}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Main Announcement Headline & Subtitle
                        Column(
                            verticalArrangement = Arrangement.spacedBy(if (isLargeBanner) 16.dp else 10.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = item.title,
                                fontSize = if (isLargeBanner) 34.sp else 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                lineHeight = if (isLargeBanner) 44.sp else 32.sp
                            )

                            if (item.subtitle.isNotEmpty()) {
                                Text(
                                    text = item.subtitle,
                                    fontSize = if (isLargeBanner) 18.sp else 15.sp,
                                    color = TextSecondary,
                                    lineHeight = if (isLargeBanner) 26.sp else 22.sp
                                )
                            }
                        }

                        // Slide Dots Indicator at bottom
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            announcements.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == currentIndex) 12.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == currentIndex) CyanAccent
                                            else TextMuted.copy(alpha = 0.4f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Top Countdown Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.TopCenter),
                color = CyanAccent,
                trackColor = Color.Transparent
            )
        }
    }
}
