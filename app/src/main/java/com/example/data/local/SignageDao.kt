package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignageDao {
    // Screens
    @Query("SELECT * FROM screens")
    fun getAllScreensFlow(): Flow<List<ScreenEntity>>

    @Query("SELECT * FROM screens WHERE id = :screenId LIMIT 1")
    suspend fun getScreenById(screenId: String): ScreenEntity?

    @Query("SELECT * FROM screens WHERE id = :screenId LIMIT 1")
    fun getScreenFlow(screenId: String): Flow<ScreenEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateScreen(screen: ScreenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreens(screens: List<ScreenEntity>)

    // Announcements
    @Query("SELECT * FROM announcements WHERE isActive = 1 ORDER BY priority ASC")
    fun getActiveAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(items: List<AnnouncementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(item: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    // Meetings
    @Query("SELECT * FROM meetings")
    fun getAllMeetingsFlow(): Flow<List<MeetingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<MeetingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingEntity)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeeting(id: String)

    // Tasks
    @Query("SELECT * FROM tasks WHERE showOnTv = 1 AND isCompleted = 0")
    fun getTvTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    // KPIs
    @Query("SELECT * FROM kpis")
    fun getAllKpisFlow(): Flow<List<KpiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKpis(kpis: List<KpiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKpi(kpi: KpiEntity)

    // Weather Cache
    @Query("SELECT * FROM weather_cache WHERE city = :city LIMIT 1")
    suspend fun getWeatherCache(city: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeatherCache(weather: WeatherCacheEntity)
}
