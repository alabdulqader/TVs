package com.example.data.remote

import com.example.data.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object OpenMeteoWeatherService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    data class CityCoords(val nameEn: String, val nameAr: String, val lat: Double, val lon: Double)

    val SUPPORTED_CITIES = listOf(
        CityCoords("Riyadh", "الرياض", 24.7136, 46.6753),
        CityCoords("Jeddah", "جدة", 21.5433, 39.1728),
        CityCoords("Dammam", "الدمام", 26.4207, 50.0888),
        CityCoords("Mecca", "مكة المكرمة", 21.3891, 39.8579),
        CityCoords("Medina", "المدينة المنورة", 24.5247, 39.5692),
        CityCoords("Khobar", "الخبر", 26.2886, 50.2108),
        CityCoords("Abha", "أبها", 18.2164, 42.5053),
        CityCoords("Tabuk", "تبوك", 28.3835, 36.5662),
        CityCoords("Dubai", "دبي", 25.2048, 55.2708),
        CityCoords("Cairo", "القاهرة", 30.0444, 31.2357)
    )

    fun getCityCoords(cityName: String): CityCoords {
        return SUPPORTED_CITIES.firstOrNull {
            it.nameEn.equals(cityName, ignoreCase = true) || it.nameAr == cityName
        } ?: SUPPORTED_CITIES[0]
    }

    suspend fun fetchWeather(cityName: String): Result<WeatherData> = withContext(Dispatchers.IO) {
        val city = getCityCoords(cityName)
        val url = "https://api.open-meteo.com/v1/forecast?latitude=${city.lat}&longitude=${city.lon}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"

        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SmartDisplayLite/2.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }
                val bodyString = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(bodyString)
                val current = json.getJSONObject("current")
                val temp = current.getDouble("temperature_2m")
                val humidity = current.optInt("relative_humidity_2m", 25)
                val code = current.optInt("weather_code", 0)
                val wind = current.optDouble("wind_speed_10m", 12.0)

                val conditionText = mapWeatherCodeToText(code)
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val data = WeatherData(
                    city = city.nameEn,
                    cityNameAr = city.nameAr,
                    temperature = temp,
                    conditionText = conditionText,
                    weatherCode = code,
                    humidity = humidity,
                    windSpeed = wind,
                    lastUpdated = timeStr,
                    isOfflineCached = false
                )
                Result.success(data)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun mapWeatherCodeToText(code: Int): String {
        return when (code) {
            0 -> "صافٍ مشمس"
            1, 2, 3 -> "غائم جزئياً"
            45, 48 -> "ضباب خفيف"
            51, 53, 55 -> "رذاذ خفيف"
            61, 63, 65 -> "أمطار معتدلة"
            71, 73, 75 -> "ثلوج"
            80, 81, 82 -> "زخات مطر"
            95, 96, 99 -> "عواصف رعدية"
            else -> "معتدل وصافٍ"
        }
    }
}
