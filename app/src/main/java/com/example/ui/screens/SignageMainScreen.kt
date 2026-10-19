package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SignageTemplate
import com.example.data.repository.SignageRepository
import com.example.server.SignageHttpServer
import com.example.ui.templates.AdvertisingDisplayTemplate
import com.example.ui.templates.CorporateDashboardTemplate
import com.example.ui.templates.ExecutiveDashboardTemplate
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SignageMainScreen(
    repository: SignageRepository,
    httpServer: SignageHttpServer,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val currentScreen by repository.currentScreen.collectAsStateWithLifecycle()
    val allScreens by repository.screens.collectAsStateWithLifecycle()
    val weather by repository.weather.collectAsStateWithLifecycle()
    val announcements by repository.announcements.collectAsStateWithLifecycle()
    val meetings by repository.meetings.collectAsStateWithLifecycle()
    val tasks by repository.tasks.collectAsStateWithLifecycle()
    val kpis by repository.kpis.collectAsStateWithLifecycle()
    val stocks by repository.stocks.collectAsStateWithLifecycle()

    var isControlPanelOpen by remember { mutableStateOf(false) }

    val serverUrl = remember { httpServer.getServerUrl() }

    BackHandler(enabled = isControlPanelOpen) {
        isControlPanelOpen = false
    }

    val screen = currentScreen
    if (screen == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CyanAccent)
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            // First time setup / Pairing screen
            !screen.isPaired -> {
                PairingScreen(
                    currentScreen = screen,
                    allScreens = allScreens,
                    serverUrl = serverUrl,
                    onConfirmPairing = { id ->
                        coroutineScope.launch {
                            repository.confirmPairing(id)
                        }
                    },
                    onSelectScreen = { id ->
                        repository.selectScreen(id)
                    },
                    onOpenControlPanel = {
                        isControlPanelOpen = true
                    }
                )
            }

            // In-app Web Control Panel / Settings
            isControlPanelOpen -> {
                ControlPanelScreen(
                    currentScreen = screen,
                    allScreens = allScreens,
                    announcements = announcements,
                    meetings = meetings,
                    tasks = tasks,
                    weather = weather,
                    serverUrl = serverUrl,
                    onSelectScreen = { id ->
                        repository.selectScreen(id)
                    },
                    onUpdateScreen = { updated ->
                        coroutineScope.launch {
                            repository.updateScreen(updated)
                        }
                    },
                    onCopySettings = { from, to ->
                        coroutineScope.launch {
                            repository.copyScreenSettings(from, to)
                        }
                    },
                    onUnpairScreen = { id ->
                        coroutineScope.launch {
                            repository.unpairScreen(id)
                            isControlPanelOpen = false
                        }
                    },
                    onAddAnnouncement = { item ->
                        coroutineScope.launch { repository.addAnnouncement(item) }
                    },
                    onDeleteAnnouncement = { id ->
                        coroutineScope.launch { repository.deleteAnnouncement(id) }
                    },
                    onAddMeeting = { m ->
                        coroutineScope.launch { repository.addMeeting(m) }
                    },
                    onDeleteMeeting = { id ->
                        coroutineScope.launch { repository.deleteMeeting(id) }
                    },
                    onAddTask = { t ->
                        coroutineScope.launch { repository.addTask(t) }
                    },
                    onDeleteTask = { id ->
                        coroutineScope.launch { repository.deleteTask(id) }
                    },
                    onClose = { isControlPanelOpen = false }
                )
            }

            // Signage Display: Active Template
            else -> {
                when (screen.template) {
                    SignageTemplate.CORPORATE_DASHBOARD -> {
                        CorporateDashboardTemplate(
                            screen = screen,
                            weather = weather,
                            announcements = announcements,
                            meetings = meetings,
                            tasks = tasks,
                            stocks = stocks,
                            onOpenControlPanel = { isControlPanelOpen = true }
                        )
                    }
                    SignageTemplate.ADVERTISING_DISPLAY -> {
                        AdvertisingDisplayTemplate(
                            screen = screen,
                            weather = weather,
                            announcements = announcements,
                            stocks = stocks,
                            onOpenControlPanel = { isControlPanelOpen = true }
                        )
                    }
                    SignageTemplate.EXECUTIVE_DASHBOARD -> {
                        ExecutiveDashboardTemplate(
                            screen = screen,
                            weather = weather,
                            kpis = kpis,
                            meetings = meetings,
                            tasks = tasks,
                            stocks = stocks,
                            onOpenControlPanel = { isControlPanelOpen = true }
                        )
                    }
                }
            }
        }
    }
}
