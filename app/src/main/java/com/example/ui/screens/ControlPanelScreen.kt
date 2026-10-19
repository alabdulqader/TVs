package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.remote.OpenMeteoWeatherService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPanelScreen(
    currentScreen: DisplayScreen,
    allScreens: List<DisplayScreen>,
    announcements: List<AnnouncementItem>,
    meetings: List<MeetingItem>,
    tasks: List<SignageTask>,
    weather: WeatherData,
    serverUrl: String,
    onSelectScreen: (String) -> Unit,
    onUpdateScreen: (DisplayScreen) -> Unit,
    onCopySettings: (String, String) -> Unit,
    onUnpairScreen: (String) -> Unit,
    onAddAnnouncement: (AnnouncementItem) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onAddMeeting: (MeetingItem) -> Unit,
    onDeleteMeeting: (String) -> Unit,
    onAddTask: (SignageTask) -> Unit,
    onDeleteTask: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = listOf(
        "الشاشات الثلاث",
        "القوالب والعرض",
        "الإعلانات والوسائط",
        "المهام والتقويم",
        "الأخبار والطقس والهوية"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "لوحة التحكم المركزية عن بُعد",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "الخادم المباشر: $serverUrl",
                                fontSize = 11.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "العودة لشاشة العرض",
                            tint = CyanAccent
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = Color(0xFF090D16)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "معاينة العرض الحي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Strip
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F172A),
                contentColor = CyanAccent,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) CyanAccent else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> ScreensManagementTab(
                        currentScreen = currentScreen,
                        allScreens = allScreens,
                        onSelectScreen = onSelectScreen,
                        onCopySettings = onCopySettings,
                        onUnpairScreen = onUnpairScreen,
                        onShowMessage = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                    )
                    1 -> TemplatesTab(
                        screen = currentScreen,
                        onUpdateScreen = { s ->
                            onUpdateScreen(s)
                            scope.launch { snackbarHostState.showSnackbar("تم تغيير القالب وتحديث الشاشة فوراً") }
                        }
                    )
                    2 -> AnnouncementsTab(
                        announcements = announcements,
                        onAdd = { item ->
                            onAddAnnouncement(item)
                            scope.launch { snackbarHostState.showSnackbar("تم نشر الإعلان بنجاح") }
                        },
                        onDelete = { id ->
                            onDeleteAnnouncement(id)
                            scope.launch { snackbarHostState.showSnackbar("تم حذف الإعلان") }
                        }
                    )
                    3 -> TasksAndMeetingsTab(
                        meetings = meetings,
                        tasks = tasks,
                        onAddMeeting = { m ->
                            onAddMeeting(m)
                            scope.launch { snackbarHostState.showSnackbar("تمت إضافة الاجتماع للتقويم") }
                        },
                        onDeleteMeeting = { id ->
                            onDeleteMeeting(id)
                            scope.launch { snackbarHostState.showSnackbar("تم حذف الاجتماع") }
                        },
                        onAddTask = { t ->
                            onAddTask(t)
                            scope.launch { snackbarHostState.showSnackbar("تمت إضافة المهمة") }
                        },
                        onDeleteTask = { id ->
                            onDeleteTask(id)
                            scope.launch { snackbarHostState.showSnackbar("تم حذف المهمة") }
                        }
                    )
                    4 -> NewsWeatherBrandingTab(
                        screen = currentScreen,
                        weather = weather,
                        onUpdateScreen = { s ->
                            onUpdateScreen(s)
                            scope.launch { snackbarHostState.showSnackbar("تم حفظ الإعدادات بنجاح") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreensManagementTab(
    currentScreen: DisplayScreen,
    allScreens: List<DisplayScreen>,
    onSelectScreen: (String) -> Unit,
    onCopySettings: (String, String) -> Unit,
    onUnpairScreen: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "إدارة الشاشات الثلاث (Android TV)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "يمكنك التبديل بين الشاشات لمعاينة محتواها، أو نسخ إعدادات أي شاشة إلى شاشة أخرى، أو فك الارتباط.",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(allScreens) { screen ->
            val isCurrent = screen.id == currentScreen.id
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isCurrent) Color(0xFF162540) else CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isCurrent) CyanAccent else CardBorder
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = screen.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyanAccent)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "الشاشة النشطة حالياً",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF090D16)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${screen.location} • كود الربط: ${screen.pairingCode}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Online Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "متصل أونلاين",
                                fontSize = 11.sp,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(text = "القالب المطبق:", fontSize = 11.sp, color = TextMuted)
                            Text(text = screen.template.titleAr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GoldAccent)
                        }
                        Column {
                            Text(text = "المدينة:", fontSize = 11.sp, color = TextMuted)
                            Text(text = screen.cityNameAr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "سرعة الشريط:", fontSize = 11.sp, color = TextMuted)
                            Text(text = "${screen.tickerSpeed} ثانية", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                        Column {
                            Text(text = "الإصدار:", fontSize = 11.sp, color = TextMuted)
                            Text(text = "v${screen.revision}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CyanAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isCurrent) {
                            Button(
                                onClick = { onSelectScreen(screen.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF090D16)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("عرض هذه الشاشة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val target = allScreens.firstOrNull { it.id != screen.id }
                                if (target != null) {
                                    onCopySettings(screen.id, target.id)
                                    onShowMessage("تم نسخ الإعدادات إلى ${target.name}")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ الإعدادات لشاشة أخرى", fontSize = 11.sp, color = TextSecondary)
                        }

                        OutlinedButton(
                            onClick = {
                                onUnpairScreen(screen.id)
                                onShowMessage("تم فك ارتباط ${screen.name} وتوليد كود جديد")
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f))
                        ) {
                            Text("فك الارتباط", fontSize = 11.sp, color = CrimsonAlert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplatesTab(
    screen: DisplayScreen,
    onUpdateScreen: (DisplayScreen) -> Unit
) {
    val templates = listOf(
        Triple(
            SignageTemplate.CORPORATE_DASHBOARD,
            "القالب الأول: Corporate Dashboard (شامل)",
            "يعرض شعار الشركة، الساعة والتاريخ، الطقس، إعلانات الشركة، جدول الاجتماعات، المهام والتقويم، وشريط الأخبار والأسهم."
        ),
        Triple(
            SignageTemplate.ADVERTISING_DISPLAY,
            "القالب الثاني: Advertising Display (إعلانات كبرى)",
            "يركز على عرض إعلان أو وسائط بمساحة كبيرة جداً مع شعار الشركة، وشريط الأخبار، ومساحة جانبية مصغرة للساعة والطقس."
        ),
        Triple(
            SignageTemplate.EXECUTIVE_DASHBOARD,
            "القالب الثالث: Executive Dashboard (قيادة تنفيذية)",
            "يعرض 4 بطاقات مؤشرات أداء KPI استراتيجية، وجدول اجتماعات المجلس، والمبادرات اليومية، وأسعار الأسهم وتاسي."
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "تحديد القالب لشاشة (${screen.name})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        templates.forEach { (tpl, title, desc) ->
            val isSelected = screen.template == tpl
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateScreen(screen.copy(template = tpl)) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Color(0xFF162540) else CardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isSelected) CyanAccent else CardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onUpdateScreen(screen.copy(template = tpl)) },
                        colors = RadioButtonDefaults.colors(selectedColor = CyanAccent)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CyanAccent else TextPrimary
                        )
                        Text(
                            text = desc,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementsTab(
    announcements: List<AnnouncementItem>,
    onAdd: (AnnouncementItem) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("إعلان مؤسسي") }
    var duration by remember { mutableStateOf("10") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "نشر إعلان جديد على الشاشات",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الإعلان") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("التفاصيل والوصف") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = badge,
                            onValueChange = { badge = it },
                            label = { Text("وسم الإعلان (Badge)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("المدة (ثوانٍ)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(
                                    AnnouncementItem(
                                        id = "ann_${System.currentTimeMillis()}",
                                        title = title,
                                        subtitle = subtitle,
                                        badge = badge,
                                        durationSeconds = duration.toIntOrNull() ?: 10,
                                        targetScreenId = "all"
                                    )
                                )
                                title = ""
                                subtitle = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF090D16)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة الإعلان لقائمة العرض التلقائي", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "قائمة الإعلانات النشطة حالياً (${announcements.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(announcements) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = item.badge, fontSize = 10.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                            }
                            Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        if (item.subtitle.isNotEmpty()) {
                            Text(text = item.subtitle, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                        }
                        Text(text = "مدة العرض: ${item.durationSeconds} ثوانٍ", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                    }

                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = CrimsonAlert)
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksAndMeetingsTab(
    meetings: List<MeetingItem>,
    tasks: List<SignageTask>,
    onAddMeeting: (MeetingItem) -> Unit,
    onDeleteMeeting: (String) -> Unit,
    onAddTask: (SignageTask) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    var meetingTitle by remember { mutableStateOf("") }
    var meetingRoom by remember { mutableStateOf("") }
    var meetingTime by remember { mutableStateOf("01:00 - 02:00 م") }

    var taskTitle by remember { mutableStateOf("") }
    var taskAssignee by remember { mutableStateOf("") }
    var taskShowOnTv by remember { mutableStateOf(true) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // New Meeting Form
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "إضافة موعد أو اجتماع للتقويم", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = meetingTitle,
                        onValueChange = { meetingTitle = it },
                        label = { Text("عنوان الاجتماع") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = meetingRoom,
                            onValueChange = { meetingRoom = it },
                            label = { Text("القاعة / المكان") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = meetingTime,
                            onValueChange = { meetingTime = it },
                            label = { Text("الوقت") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (meetingTitle.isNotBlank()) {
                                onAddMeeting(
                                    MeetingItem(
                                        id = "m_${System.currentTimeMillis()}",
                                        title = meetingTitle,
                                        room = meetingRoom.ifBlank { "القاعة الرئيسية" },
                                        organizer = "الإدارة",
                                        timeRange = meetingTime,
                                        status = "upcoming"
                                    )
                                )
                                meetingTitle = ""
                                meetingRoom = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF090D16)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة الاجتماع للتقويم", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // New Task Form with TV-Privacy Filter Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "إضافة مهمة جديدة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("عنوان المهمة") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = taskAssignee,
                        onValueChange = { taskAssignee = it },
                        label = { Text("المسؤول / الفريق") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Privacy filter toggle: Requirement 6.5
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "عرض على شاشة التلفزيون (Public TV)", fontSize = 13.sp, color = TextPrimary)
                            Text(text = "أوقف الخيار للمهام والبيانات السرية الخاصة", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = taskShowOnTv,
                            onCheckedChange = { taskShowOnTv = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanAccent)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank()) {
                                onAddTask(
                                    SignageTask(
                                        id = "t_${System.currentTimeMillis()}",
                                        title = taskTitle,
                                        assignee = taskAssignee,
                                        priority = "MEDIUM",
                                        dueTime = "اليوم",
                                        showOnTv = taskShowOnTv
                                    )
                                )
                                taskTitle = ""
                                taskAssignee = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color(0xFF090D16)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة المهمة", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsWeatherBrandingTab(
    screen: DisplayScreen,
    weather: WeatherData,
    onUpdateScreen: (DisplayScreen) -> Unit
) {
    var companyName by remember(screen.companyName) { mutableStateOf(screen.companyName) }
    var tickerText by remember(screen.customTickerText) { mutableStateOf(screen.customTickerText) }
    var selectedCity by remember(screen.city) { mutableStateOf(screen.city) }
    var tickerSpeed by remember(screen.tickerSpeed) { mutableIntStateOf(screen.tickerSpeed) }
    var tickerEnabled by remember(screen.tickerEnabled) { mutableStateOf(screen.tickerEnabled) }
    var stockEnabled by remember(screen.stockEnabled) { mutableStateOf(screen.stockEnabled) }
    var hijriEnabled by remember(screen.hijriEnabled) { mutableStateOf(screen.hijriEnabled) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "الهوية المؤسسية وشريط الأخبار والطقس", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("اسم المنشأة / الشركة") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "مدينة الطقس (Open-Meteo مجاني):", fontSize = 13.sp, color = TextSecondary)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        listOf("Riyadh" to "الرياض", "Jeddah" to "جدة", "Dammam" to "الدمام", "Mecca" to "مكة", "Dubai" to "دبي").forEach { (cEn, cAr) ->
                            val isSelected = selectedCity.equals(cEn, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCity = cEn },
                                label = { Text(cAr, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccent,
                                    selectedLabelColor = Color(0xFF090D16)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = tickerText,
                        onValueChange = { tickerText = it },
                        label = { Text("نص شريط الأخبار المتحرك (Ticker)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "تفعيل شريط الأخبار المتحرك", fontSize = 14.sp, color = TextPrimary)
                        Switch(checked = tickerEnabled, onCheckedChange = { tickerEnabled = it })
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "عرض مؤشر الأسهم وتاسي", fontSize = 14.sp, color = TextPrimary)
                        Switch(checked = stockEnabled, onCheckedChange = { stockEnabled = it })
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "عرض التاريخ الهجري مع الميلادي", fontSize = 14.sp, color = TextPrimary)
                        Switch(checked = hijriEnabled, onCheckedChange = { hijriEnabled = it })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val cityCoords = OpenMeteoWeatherService.getCityCoords(selectedCity)
                            onUpdateScreen(
                                screen.copy(
                                    companyName = companyName,
                                    city = selectedCity,
                                    cityNameAr = cityCoords.nameAr,
                                    customTickerText = tickerText,
                                    tickerEnabled = tickerEnabled,
                                    stockEnabled = stockEnabled,
                                    hijriEnabled = hijriEnabled
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color(0xFF090D16)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ التغييرات وتحديث الشاشة فوراً", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
