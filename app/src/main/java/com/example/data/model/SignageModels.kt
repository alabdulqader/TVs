package com.example.data.model

enum class SignageTemplate(val id: String, val titleAr: String, val titleEn: String) {
    CORPORATE_DASHBOARD("corporate", "لوحة معلومات المؤسسة", "Corporate Dashboard"),
    ADVERTISING_DISPLAY("advertising", "شاشة الإعلانات والوسائط", "Advertising Display"),
    EXECUTIVE_DASHBOARD("executive", "لوحة القيادة التنفيذية", "Executive Dashboard")
}

data class DisplayScreen(
    val id: String,
    val name: String,
    val location: String,
    val template: SignageTemplate,
    val isOnline: Boolean = true,
    val lastSeenEpochMs: Long = System.currentTimeMillis(),
    val pairingCode: String,
    val isPaired: Boolean = true,
    val city: String = "Riyadh",
    val cityNameAr: String = "الرياض",
    val newsCategory: String = "saudi",
    val customTickerText: String = "أهلاً بكم في نظام العرض الذكي Smart Display Lite • استقرار عالي وتحديث لحظي عن بُعد",
    val tickerSpeed: Int = 30, // seconds for full scroll
    val tickerEnabled: Boolean = true,
    val stockEnabled: Boolean = true,
    val weatherEnabled: Boolean = true,
    val clockFormat24: Boolean = true,
    val hijriEnabled: Boolean = true,
    val companyName: String = "شركة المسار التقني المتقدم",
    val companySlogan: String = "الريادة في الحلول الرقمية والذكية",
    val themePalette: String = "navy",
    val language: String = "ar",
    val revision: Long = 1L
)

data class AnnouncementItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String = "إعلان مؤسسي",
    val imageUrl: String = "",
    val durationSeconds: Int = 10,
    val targetScreenId: String = "all", // "all", "screen_1", "screen_2", "screen_3"
    val isActive: Boolean = true,
    val priority: Int = 1,
    val accentColor: Long = 0xFF38BDF8
)

data class MeetingItem(
    val id: String,
    val title: String,
    val room: String,
    val organizer: String,
    val timeRange: String,
    val status: String = "upcoming" // "current", "upcoming", "completed"
)

data class SignageTask(
    val id: String,
    val title: String,
    val description: String = "",
    val assignee: String = "",
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val dueTime: String = "اليوم",
    val showOnTv: Boolean = true, // Privacy filter
    val isCompleted: Boolean = false
)

data class KpiMetric(
    val id: String,
    val title: String,
    val value: String,
    val trend: String = "up", // "up", "down", "neutral"
    val subtitle: String = "",
    val target: String = "100%"
)

data class StockQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val isPositive: Boolean
)

data class WeatherData(
    val city: String,
    val cityNameAr: String,
    val temperature: Double,
    val conditionText: String,
    val weatherCode: Int,
    val humidity: Int,
    val windSpeed: Double,
    val lastUpdated: String,
    val isOfflineCached: Boolean = false
)
