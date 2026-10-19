package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.repository.SignageRepository
import com.example.server.SignageHttpServer
import com.example.ui.screens.SignageMainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: SignageRepository
    private lateinit var httpServer: SignageHttpServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Continuous Display: Keep TV screen on for 24/7 digital signage operation
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize repository and embedded HTTP server
        repository = SignageRepository(applicationContext)
        httpServer = SignageHttpServer(applicationContext, repository)
        httpServer.start()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    SignageMainScreen(
                        repository = repository,
                        httpServer = httpServer
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        httpServer.stop()
    }
}
