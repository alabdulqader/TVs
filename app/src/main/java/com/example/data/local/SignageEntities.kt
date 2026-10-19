package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SignageTemplate

@Entity(tableName = "screens")
data class ScreenEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val templateName: String,
    val isOnline: Boolean,
    val lastSeenEpochMs: Long,
    val pairingCode: String,
    val isPaired: Boolean,
    val city: String,
    val cityNameAr: String,
    val newsCategory: String,
    val customTickerText: String,
    val tickerSpeed: Int,
    val tickerEnabled: Boolean,
    val stockEnabled: Boolean,
    val weatherEnabled: Boolean,
    val clockFormat24: Boolean,
    val hijriEnabled: Boolean,
    val companyName: String,
    val companySlogan: String,
    val themePalette: String,
    val language: String,
    val revision: Long
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val imageUrl: String,
    val durationSeconds: Int,
    val targetScreenId: String,
    val isActive: Boolean,
    val priority: Int,
    val accentColor: Long
)

@Entity(tableName = "meetings")
data class MeetingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val room: String,
    val organizer: String,
    val timeRange: String,
    val status: String
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val assignee: String,
    val priority: String,
    val dueTime: String,
    val showOnTv: Boolean,
    val isCompleted: Boolean
)

@Entity(tableName = "kpis")
data class KpiEntity(
    @PrimaryKey val id: String,
    val title: String,
    val value: String,
    val trend: String,
    val subtitle: String,
    val target: String
)

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val city: String,
    val cityNameAr: String,
    val temperature: Double,
    val conditionText: String,
    val weatherCode: Int,
    val humidity: Int,
    val windSpeed: Double,
    val lastUpdated: String
)
