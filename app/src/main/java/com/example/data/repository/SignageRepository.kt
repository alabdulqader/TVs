package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.OpenMeteoWeatherService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SignageRepository(private val context: Context) {
    private val database = SignageDatabase.getInstance(context)
    private val dao = database.signageDao()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentScreenId = MutableStateFlow("screen_1")
    val currentScreenId: StateFlow<String> = _currentScreenId.asStateFlow()

    private val _screens = MutableStateFlow<List<DisplayScreen>>(emptyList())
    val screens: StateFlow<List<DisplayScreen>> = _screens.asStateFlow()

    private val _announcements = MutableStateFlow<List<AnnouncementItem>>(emptyList())
    val announcements: StateFlow<List<AnnouncementItem>> = _announcements.asStateFlow()

    private val _meetings = MutableStateFlow<List<MeetingItem>>(emptyList())
    val meetings: StateFlow<List<MeetingItem>> = _meetings.asStateFlow()

    private val _tasks = MutableStateFlow<List<SignageTask>>(emptyList())
    val tasks: StateFlow<List<SignageTask>> = _tasks.asStateFlow()

    private val _kpis = MutableStateFlow<List<KpiMetric>>(emptyList())
    val kpis: StateFlow<List<KpiMetric>> = _kpis.asStateFlow()

    private val _weather = MutableStateFlow<WeatherData>(
        WeatherData(
            city = "Riyadh",
            cityNameAr = "الرياض",
            temperature = 29.5,
            conditionText = "صافٍ مشمس",
            weatherCode = 0,
            humidity = 18,
            windSpeed = 14.0,
            lastUpdated = "10:00",
            isOfflineCached = true
        )
    )
    val weather: StateFlow<WeatherData> = _weather.asStateFlow()

    private val _stocks = MutableStateFlow<List<StockQuote>>(
        listOf(
            StockQuote("TASI", "تاسي", 11842.15, 68.40, 0.58, true),
            StockQuote("ARAMCO", "أرامكو", 27.65, 0.20, 0.73, true),
            StockQuote("ALRAJHI", "الراجحي", 88.90, -0.40, -0.45, false),
            StockQuote("STC", "إس تي سي", 41.20, 0.35, 0.86, true),
            StockQuote("SABIC", "سابك", 73.10, -0.60, -0.81, false),
            StockQuote("ACWA", "أكوا باور", 345.00, 4.80, 1.41, true)
        )
    )
    val stocks: StateFlow<List<StockQuote>> = _stocks.asStateFlow()

    val currentScreen: StateFlow<DisplayScreen?> = combine(_screens, _currentScreenId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)

    init {
        coroutineScope.launch {
            seedDefaultDataIfNeeded()
            observeDatabase()
            refreshWeatherForCurrentScreen()
        }
    }

    private suspend fun seedDefaultDataIfNeeded() {
        val existingScreens = dao.getAllScreensFlow().firstOrNull() ?: emptyList()
        if (existingScreens.isEmpty()) {
            val initialScreens = listOf(
                ScreenEntity(
                    id = "screen_1",
                    name = "شاشة الاستقبال الرئيسية (Lobby)",
                    location = "المدخل الرئيسي - بهو الاستقبال",
                    templateName = SignageTemplate.CORPORATE_DASHBOARD.name,
                    isOnline = true,
                    lastSeenEpochMs = System.currentTimeMillis(),
                    pairingCode = "DISP-7812",
                    isPaired = true,
                    city = "Riyadh",
                    cityNameAr = "الرياض",
                    newsCategory = "saudi",
                    customTickerText = "أهلاً وسهلاً بضيوف شركة المسار التقني المتقدم • أسبوع الابتكار والتحول الرقمي 2026 • اجتماع مجلس الإدارة الساعة 11:00 صباحاً",
                    tickerSpeed = 30,
                    tickerEnabled = true,
                    stockEnabled = true,
                    weatherEnabled = true,
                    clockFormat24 = true,
                    hijriEnabled = true,
                    companyName = "شركة المسار التقني المتقدم",
                    companySlogan = "ريادة التحول الرقمي والذكاء الاصطناعي",
                    themePalette = "navy",
                    language = "ar",
                    revision = 1L
                ),
                ScreenEntity(
                    id = "screen_2",
                    name = "شاشة صالة العمليات والمطورين",
                    location = "الطابق الثاني - قاعة العمليات المركزية",
                    templateName = SignageTemplate.ADVERTISING_DISPLAY.name,
                    isOnline = true,
                    lastSeenEpochMs = System.currentTimeMillis(),
                    pairingCode = "DISP-4390",
                    isPaired = true,
                    city = "Riyadh",
                    cityNameAr = "الرياض",
                    newsCategory = "tech",
                    customTickerText = "إطلاق الإصدار 2.0 من المنصة السحابية • تم تحقيق نسبة جاهزية الأنظمة 99.98% هذا الشهر • تهانينا لفريق البنية التحتية",
                    tickerSpeed = 25,
                    tickerEnabled = true,
                    stockEnabled = false,
                    weatherEnabled = true,
                    clockFormat24 = true,
                    hijriEnabled = false,
                    companyName = "شركة المسار التقني المتقدم",
                    companySlogan = "مركز مراقبة العمليات الرقمية",
                    themePalette = "obsidian",
                    language = "ar",
                    revision = 1L
                ),
                ScreenEntity(
                    id = "screen_3",
                    name = "شاشة الجناح التنفيذي ومجلس الإدارة",
                    location = "الطابق الخامس - الجناح التنفيذي",
                    templateName = SignageTemplate.EXECUTIVE_DASHBOARD.name,
                    isOnline = true,
                    lastSeenEpochMs = System.currentTimeMillis(),
                    pairingCode = "DISP-9105",
                    isPaired = true,
                    city = "Riyadh",
                    cityNameAr = "الرياض",
                    newsCategory = "business",
                    customTickerText = "تقرير الأداء المالي للربع السنوي: نمو الإيرادات بنسبة 18.5% • إغلاق مؤشر تاسي على مكاسب • اكتمال 94% من المشاريع الاستراتيجية",
                    tickerSpeed = 35,
                    tickerEnabled = true,
                    stockEnabled = true,
                    weatherEnabled = true,
                    clockFormat24 = true,
                    hijriEnabled = true,
                    companyName = "شركة المسار التقني المتقدم",
                    companySlogan = "المكتب التنفيذي - لوحة القيادة",
                    themePalette = "emerald",
                    language = "ar",
                    revision = 1L
                )
            )
            dao.insertScreens(initialScreens)

            val initialAnnouncements = listOf(
                AnnouncementEntity(
                    id = "ann_1",
                    title = "انطلاق ملتقى الابتكار الرقمي السنوي 2026",
                    subtitle = "ندعو جميع منسوبي الشركة وضيوفنا الكرام لحضور الجلسات النقاشية في القاعة الرئيسية وعبر البث المباشر",
                    badge = "حدث بارز",
                    imageUrl = "",
                    durationSeconds = 12,
                    targetScreenId = "all",
                    isActive = true,
                    priority = 1,
                    accentColor = 0xFF38BDF8
                ),
                AnnouncementEntity(
                    id = "ann_2",
                    title = "تدشين منظومة الذكاء الاصطناعي المؤسسي",
                    subtitle = "خطوة رائدة نحو أتمتة الإجراءات ورفع كفاءة الخدمات التشغيلية بنسبة تتجاوز 40%",
                    badge = "إنجاز تقني",
                    imageUrl = "",
                    durationSeconds = 10,
                    targetScreenId = "all",
                    isActive = true,
                    priority = 2,
                    accentColor = 0xFF10B981
                ),
                AnnouncementEntity(
                    id = "ann_3",
                    title = "جائزة التميز المؤسسي لأفضل بيئة عمل",
                    subtitle = "شكر وتقدير لكافة فرق العمل على مساهمتهم المتميزة في تعزيز ثقافة الإبداع والإنتاجية",
                    badge = "تكريم",
                    imageUrl = "",
                    durationSeconds = 10,
                    targetScreenId = "all",
                    isActive = true,
                    priority = 3,
                    accentColor = 0xFFF59E0B
                )
            )
            dao.insertAnnouncements(initialAnnouncements)

            val initialMeetings = listOf(
                MeetingItem(
                    id = "meet_1",
                    title = "اجتماع المراجعة التشغيلية الأسبوعي",
                    room = "قاعة الرواد (A1)",
                    organizer = "م. خالد الفهد",
                    timeRange = "10:00 - 11:00 ص",
                    status = "current"
                ),
                MeetingItem(
                    id = "meet_2",
                    title = "جلسة اعتماد استراتيجية الخدمات السحابية",
                    room = "قاعة المجلس التنفيذي",
                    organizer = "د. نورة السبيعي",
                    timeRange = "11:30 - 12:30 م",
                    status = "upcoming"
                ),
                MeetingItem(
                    id = "meet_3",
                    title = "ورشة عمل تطوير التطبيقات والأمن السيبراني",
                    room = "مختبر الابتكار (B2)",
                    organizer = "فريق الدعم الفني",
                    timeRange = "02:00 - 03:30 م",
                    status = "upcoming"
                )
            ).map {
                MeetingEntity(it.id, it.title, it.room, it.organizer, it.timeRange, it.status)
            }
            dao.insertMeetings(initialMeetings)

            val initialTasks = listOf(
                TaskEntity(
                    id = "task_1",
                    title = "استكمال تدقيق معايير أمن البيانات ISO-27001",
                    description = "فحص النسخ الاحتياطي وحماية السجلات",
                    assignee = "سارة العتيبي",
                    priority = "HIGH",
                    dueTime = "اليوم 01:00 م",
                    showOnTv = true,
                    isCompleted = false
                ),
                TaskEntity(
                    id = "task_2",
                    title = "تحديث شاشات البهو والتحقق من المزامنة اللحظية",
                    description = "فحص استقرار الشبكة وعرض Open-Meteo",
                    assignee = "فهد المطيري",
                    priority = "MEDIUM",
                    dueTime = "اليوم 03:00 م",
                    showOnTv = true,
                    isCompleted = false
                ),
                TaskEntity(
                    id = "task_3",
                    title = "إعداد تقرير الإيرادات والنمو الربع سنوي",
                    description = "تحديث الرسوم البيانية للوحة التنفيذية",
                    assignee = "عمر باقيس",
                    priority = "HIGH",
                    dueTime = "غداً 10:00 ص",
                    showOnTv = true,
                    isCompleted = false
                )
            )
            dao.insertTasks(initialTasks)

            val initialKpis = listOf(
                KpiEntity("kpi_1", "تحقيق الأهداف الربع سنوية", "94.8%", "up", "+3.2% مقارنة بالهدف", "100%"),
                KpiEntity("kpi_2", "معدل رضا العملاء والزوار", "98.2%", "up", "استناداً إلى 1,420 استبيان", "95%"),
                KpiEntity("kpi_3", "مشاريع رقمية مكتملة", "28 / 30", "up", "مؤشر إنجاز مرتفع", "30"),
                KpiEntity("kpi_4", "جاهزية واستقرار الأنظمة", "99.98%", "neutral", "صفر انقطاعات حرجة", "99.9%")
            )
            dao.insertKpis(initialKpis)
        }
    }

    private fun observeDatabase() {
        coroutineScope.launch {
            dao.getAllScreensFlow().collect { entities ->
                _screens.value = entities.map { e ->
                    DisplayScreen(
                        id = e.id,
                        name = e.name,
                        location = e.location,
                        template = try { SignageTemplate.valueOf(e.templateName) } catch (_: Exception) { SignageTemplate.CORPORATE_DASHBOARD },
                        isOnline = e.isOnline,
                        lastSeenEpochMs = e.lastSeenEpochMs,
                        pairingCode = e.pairingCode,
                        isPaired = e.isPaired,
                        city = e.city,
                        cityNameAr = e.cityNameAr,
                        newsCategory = e.newsCategory,
                        customTickerText = e.customTickerText,
                        tickerSpeed = e.tickerSpeed,
                        tickerEnabled = e.tickerEnabled,
                        stockEnabled = e.stockEnabled,
                        weatherEnabled = e.weatherEnabled,
                        clockFormat24 = e.clockFormat24,
                        hijriEnabled = e.hijriEnabled,
                        companyName = e.companyName,
                        companySlogan = e.companySlogan,
                        themePalette = e.themePalette,
                        language = e.language,
                        revision = e.revision
                    )
                }
            }
        }

        coroutineScope.launch {
            dao.getActiveAnnouncementsFlow().collect { list ->
                _announcements.value = list.map {
                    AnnouncementItem(it.id, it.title, it.subtitle, it.badge, it.imageUrl, it.durationSeconds, it.targetScreenId, it.isActive, it.priority, it.accentColor)
                }
            }
        }

        coroutineScope.launch {
            dao.getAllMeetingsFlow().collect { list ->
                _meetings.value = list.map {
                    MeetingItem(it.id, it.title, it.room, it.organizer, it.timeRange, it.status)
                }
            }
        }

        coroutineScope.launch {
            dao.getTvTasksFlow().collect { list ->
                _tasks.value = list.map {
                    SignageTask(it.id, it.title, it.description, it.assignee, it.priority, it.dueTime, it.showOnTv, it.isCompleted)
                }
            }
        }

        coroutineScope.launch {
            dao.getAllKpisFlow().collect { list ->
                _kpis.value = list.map {
                    KpiMetric(it.id, it.title, it.value, it.trend, it.subtitle, it.target)
                }
            }
        }
    }

    fun selectScreen(screenId: String) {
        _currentScreenId.value = screenId
        coroutineScope.launch {
            refreshWeatherForCurrentScreen()
        }
    }

    suspend fun updateScreen(screen: DisplayScreen) {
        val entity = ScreenEntity(
            id = screen.id,
            name = screen.name,
            location = screen.location,
            templateName = screen.template.name,
            isOnline = screen.isOnline,
            lastSeenEpochMs = System.currentTimeMillis(),
            pairingCode = screen.pairingCode,
            isPaired = screen.isPaired,
            city = screen.city,
            cityNameAr = screen.cityNameAr,
            newsCategory = screen.newsCategory,
            customTickerText = screen.customTickerText,
            tickerSpeed = screen.tickerSpeed,
            tickerEnabled = screen.tickerEnabled,
            stockEnabled = screen.stockEnabled,
            weatherEnabled = screen.weatherEnabled,
            clockFormat24 = screen.clockFormat24,
            hijriEnabled = screen.hijriEnabled,
            companyName = screen.companyName,
            companySlogan = screen.companySlogan,
            themePalette = screen.themePalette,
            language = screen.language,
            revision = screen.revision + 1L
        )
        dao.insertOrUpdateScreen(entity)
        if (screen.id == _currentScreenId.value) {
            refreshWeatherForCurrentScreen()
        }
    }

    suspend fun copyScreenSettings(fromScreenId: String, toScreenId: String) {
        val source = _screens.value.firstOrNull { it.id == fromScreenId } ?: return
        val target = _screens.value.firstOrNull { it.id == toScreenId } ?: return
        val updated = target.copy(
            template = source.template,
            city = source.city,
            cityNameAr = source.cityNameAr,
            newsCategory = source.newsCategory,
            customTickerText = source.customTickerText,
            tickerSpeed = source.tickerSpeed,
            tickerEnabled = source.tickerEnabled,
            stockEnabled = source.stockEnabled,
            weatherEnabled = source.weatherEnabled,
            companyName = source.companyName,
            companySlogan = source.companySlogan,
            themePalette = source.themePalette,
            language = source.language,
            revision = target.revision + 1L
        )
        updateScreen(updated)
    }

    suspend fun unpairScreen(screenId: String) {
        val target = _screens.value.firstOrNull { it.id == screenId } ?: return
        val newCode = "DISP-${(1000..9999).random()}"
        val updated = target.copy(isPaired = false, pairingCode = newCode, revision = target.revision + 1L)
        updateScreen(updated)
    }

    suspend fun confirmPairing(screenId: String) {
        val target = _screens.value.firstOrNull { it.id == screenId } ?: return
        val updated = target.copy(isPaired = true, revision = target.revision + 1L)
        updateScreen(updated)
    }

    suspend fun addAnnouncement(item: AnnouncementItem) {
        dao.insertAnnouncement(
            AnnouncementEntity(item.id, item.title, item.subtitle, item.badge, item.imageUrl, item.durationSeconds, item.targetScreenId, item.isActive, item.priority, item.accentColor)
        )
    }

    suspend fun deleteAnnouncement(id: String) {
        dao.deleteAnnouncement(id)
    }

    suspend fun addMeeting(item: MeetingItem) {
        dao.insertMeeting(
            MeetingEntity(item.id, item.title, item.room, item.organizer, item.timeRange, item.status)
        )
    }

    suspend fun deleteMeeting(id: String) {
        dao.deleteMeeting(id)
    }

    suspend fun addTask(task: SignageTask) {
        dao.insertTask(
            TaskEntity(task.id, task.title, task.description, task.assignee, task.priority, task.dueTime, task.showOnTv, task.isCompleted)
        )
    }

    suspend fun deleteTask(id: String) {
        dao.deleteTask(id)
    }

    suspend fun refreshWeatherForCurrentScreen() {
        val screen = currentScreen.value ?: return
        val result = OpenMeteoWeatherService.fetchWeather(screen.city)
        if (result.isSuccess) {
            val weatherData = result.getOrThrow()
            _weather.value = weatherData
            dao.saveWeatherCache(
                WeatherCacheEntity(
                    city = weatherData.city,
                    cityNameAr = weatherData.cityNameAr,
                    temperature = weatherData.temperature,
                    conditionText = weatherData.conditionText,
                    weatherCode = weatherData.weatherCode,
                    humidity = weatherData.humidity,
                    windSpeed = weatherData.windSpeed,
                    lastUpdated = weatherData.lastUpdated
                )
            )
        } else {
            // Load from cache if offline
            val cached = dao.getWeatherCache(screen.city)
            if (cached != null) {
                _weather.value = WeatherData(
                    city = cached.city,
                    cityNameAr = cached.cityNameAr,
                    temperature = cached.temperature,
                    conditionText = cached.conditionText,
                    weatherCode = cached.weatherCode,
                    humidity = cached.humidity,
                    windSpeed = cached.windSpeed,
                    lastUpdated = "${cached.lastUpdated} (مخزن محلياً)",
                    isOfflineCached = true
                )
            }
        }
    }
}
