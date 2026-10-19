package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SignageTemplate
import com.example.data.model.SignageTask
import com.example.data.repository.SignageRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Smart Display Lite`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart Display Lite", appName)
    }

    @Test
    fun `repository initializes three screens with expected templates`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SignageRepository(context)

        // Wait for seeding
        var screens = repository.screens.value
        var attempts = 0
        while (screens.size < 3 && attempts < 20) {
            kotlinx.coroutines.delay(100)
            screens = repository.screens.value
            attempts++
        }

        assertTrue("Should seed at least 3 screens", screens.size >= 3)
        val screen1 = screens.find { it.id == "screen_1" }
        assertNotNull(screen1)
        assertEquals(SignageTemplate.CORPORATE_DASHBOARD, screen1?.template)

        val screen2 = screens.find { it.id == "screen_2" }
        assertNotNull(screen2)
        assertEquals(SignageTemplate.ADVERTISING_DISPLAY, screen2?.template)

        val screen3 = screens.find { it.id == "screen_3" }
        assertNotNull(screen3)
        assertEquals(SignageTemplate.EXECUTIVE_DASHBOARD, screen3?.template)
    }

    @Test
    fun `tasks marked showOnTv false are filtered out from public signage`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SignageRepository(context)

        // Add private task
        repository.addTask(
            SignageTask(
                id = "private_task_1",
                title = "تقرير سري للغاية",
                showOnTv = false
            )
        )

        val tvTasks = repository.tasks.value
        val hasPrivate = tvTasks.any { it.id == "private_task_1" }
        assertTrue("Private task must not be shown on public TV", !hasPrivate)
    }
}
