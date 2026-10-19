package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisplayScreen
import com.example.ui.components.QrCodeView
import com.example.ui.theme.*

@Composable
fun PairingScreen(
    currentScreen: DisplayScreen,
    allScreens: List<DisplayScreen>,
    serverUrl: String,
    onConfirmPairing: (String) -> Unit,
    onSelectScreen: (String) -> Unit,
    onOpenControlPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScreenId by remember(currentScreen.id) { mutableStateOf(currentScreen.id) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D16),
                        Color(0xFF0D1527),
                        Color(0xFF060910)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            color = CardSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.spacedBy(36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: QR Code & Temporary Pairing PIN
                Column(
                    modifier = Modifier.weight(0.42f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        QrCodeView(
                            data = "$serverUrl?pair=${currentScreen.pairingCode}",
                            size = 200.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "كود الربط المؤقت",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = currentScreen.pairingCode,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "صلاحية الكود مؤقتة وتستخدم لربط آمن",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Right Column: Instructions, Screen Selection & Direct Confirmation
                Column(
                    modifier = Modifier.weight(0.58f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title & Description
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyanAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = Color(0xFF090D16),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "مرحباً بكم في Smart Display Lite",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "نظام العرض الرقمي والإعلانات المؤسسية لأجهزة Android TV",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    // Steps Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InstructionRow(
                            step = "1",
                            text = "امسح رمز QR أعلاه بكاميرا هاتفك أو افتح الرابط:",
                            subText = serverUrl
                        )
                        InstructionRow(
                            step = "2",
                            text = "أدخل كود الربط (${currentScreen.pairingCode}) في لوحة التحكم",
                            subText = null
                        )
                        InstructionRow(
                            step = "3",
                            text = "حدد اسم الشاشة وموقعها ليبدأ العرض التلقائي فوراً",
                            subText = null
                        )
                    }

                    // Screen Identity Choice (Assign this physical TV)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "تعيين هوية هذه الشاشة (3 شاشات متوفرة):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allScreens.forEach { s ->
                                val isSelected = s.id == selectedScreenId
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp)),
                                    color = if (isSelected) Color(0xFF1E2D4A) else Color(0xFF0F172A),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) CyanAccent else CardBorder
                                    ),
                                    onClick = {
                                        selectedScreenId = s.id
                                        onSelectScreen(s.id)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = s.name.substringBefore("(").trim(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CyanAccent else TextPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = s.template.titleAr,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onConfirmPairing(selectedScreenId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                contentColor = Color(0xFF090D16)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تأكيد الربط وبدء العرض الآن",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenControlPanel,
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = TextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "لوحة التحكم", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(step: String, text: String, subText: String?) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
            )
        }

        Column {
            Text(
                text = text,
                fontSize = 13.sp,
                color = TextPrimary
            )
            if (subText != null) {
                Text(
                    text = subText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            }
        }
    }
}
