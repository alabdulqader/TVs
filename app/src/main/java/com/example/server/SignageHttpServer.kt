package com.example.server

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.data.model.*
import com.example.data.repository.SignageRepository
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID

class SignageHttpServer(
    private val context: Context,
    private val repository: SignageRepository,
    private val port: Int = 8080
) {
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        if (isRunning) return
        isRunning = true
        scope.launch {
            try {
                serverSocket = ServerSocket(port)
                while (isRunning) {
                    val client = serverSocket?.accept() ?: break
                    launch { handleClient(client) }
                }
            } catch (e: Exception) {
                // Log or handle closed socket
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
    }

    fun getLocalIpAddress(): String {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ip = wifiManager?.connectionInfo?.ipAddress ?: 0
            if (ip != 0) {
                @Suppress("DEPRECATION")
                Formatter.formatIpAddress(ip)
            } else {
                "localhost"
            }
        } catch (_: Exception) {
            "localhost"
        }
    }

    fun getServerUrl(): String {
        return "http://${getLocalIpAddress()}:$port"
    }

    private suspend fun handleClient(socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val out = socket.getOutputStream()

            val requestLine = reader.readLine() ?: return@withContext
            val parts = requestLine.split(" ")
            if (parts.size < 2) return@withContext
            val method = parts[0]
            val path = parts[1]

            // Read headers
            var contentLength = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrEmpty()) break
                if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            var body = ""
            if (contentLength > 0) {
                val charArray = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val read = reader.read(charArray, readTotal, contentLength - readTotal)
                    if (read == -1) break
                    readTotal += read
                }
                body = String(charArray, 0, readTotal)
            }

            when {
                path == "/" || path.startsWith("/index") -> {
                    val html = buildWebDashboardHtml()
                    sendResponse(out, "200 OK", "text/html; charset=UTF-8", html.toByteArray(Charsets.UTF_8))
                }
                path == "/api/status" -> {
                    val json = buildStatusJson()
                    sendResponse(out, "200 OK", "application/json", json.toByteArray(Charsets.UTF_8))
                }
                path == "/api/switch-screen" && method == "POST" -> {
                    val json = JSONObject(body)
                    val screenId = json.optString("screenId", "screen_1")
                    repository.selectScreen(screenId)
                    sendResponse(out, "200 OK", "application/json", "{\"success\":true}".toByteArray())
                }
                path == "/api/update-template" && method == "POST" -> {
                    val json = JSONObject(body)
                    val screenId = json.optString("screenId", repository.currentScreenId.value)
                    val templateName = json.optString("template", "CORPORATE_DASHBOARD")
                    val screen = repository.screens.value.firstOrNull { it.id == screenId }
                    if (screen != null) {
                        val template = try { SignageTemplate.valueOf(templateName) } catch (_: Exception) { SignageTemplate.CORPORATE_DASHBOARD }
                        repository.updateScreen(screen.copy(template = template))
                    }
                    sendResponse(out, "200 OK", "application/json", "{\"success\":true}".toByteArray())
                }
                path == "/api/update-screen" && method == "POST" -> {
                    val json = JSONObject(body)
                    val screenId = json.optString("screenId", repository.currentScreenId.value)
                    val screen = repository.screens.value.firstOrNull { it.id == screenId }
                    if (screen != null) {
                        val updated = screen.copy(
                            companyName = json.optString("companyName", screen.companyName),
                            city = json.optString("city", screen.city),
                            customTickerText = json.optString("tickerText", screen.customTickerText),
                            tickerSpeed = json.optInt("tickerSpeed", screen.tickerSpeed),
                            themePalette = json.optString("themePalette", screen.themePalette),
                            language = json.optString("language", screen.language)
                        )
                        repository.updateScreen(updated)
                    }
                    sendResponse(out, "200 OK", "application/json", "{\"success\":true}".toByteArray())
                }
                path == "/api/add-announcement" && method == "POST" -> {
                    val json = JSONObject(body)
                    val item = AnnouncementItem(
                        id = "ann_" + UUID.randomUUID().toString().take(8),
                        title = json.optString("title", "إعلان جديد"),
                        subtitle = json.optString("subtitle", ""),
                        badge = json.optString("badge", "إعلان"),
                        durationSeconds = json.optInt("duration", 10),
                        targetScreenId = json.optString("targetScreen", "all")
                    )
                    repository.addAnnouncement(item)
                    sendResponse(out, "200 OK", "application/json", "{\"success\":true}".toByteArray())
                }
                path == "/api/add-task" && method == "POST" -> {
                    val json = JSONObject(body)
                    val task = SignageTask(
                        id = "task_" + UUID.randomUUID().toString().take(8),
                        title = json.optString("title", "مهمة جديدة"),
                        assignee = json.optString("assignee", "فريق العمل"),
                        priority = json.optString("priority", "MEDIUM"),
                        dueTime = json.optString("dueTime", "اليوم"),
                        showOnTv = json.optBoolean("showOnTv", true)
                    )
                    repository.addTask(task)
                    sendResponse(out, "200 OK", "application/json", "{\"success\":true}".toByteArray())
                }
                else -> {
                    sendResponse(out, "404 Not Found", "text/plain", "Not Found".toByteArray())
                }
            }
        } catch (_: Exception) {
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private fun sendResponse(out: OutputStream, status: String, contentType: String, body: ByteArray) {
        val header = "HTTP/1.1 $status\r\n" +
                "Content-Type: $contentType\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Headers: Content-Type\r\n" +
                "Connection: close\r\n\r\n"
        out.write(header.toByteArray(Charsets.UTF_8))
        out.write(body)
        out.flush()
    }

    private fun buildStatusJson(): String {
        val current = repository.currentScreen.value
        val json = JSONObject()
        json.put("currentScreenId", repository.currentScreenId.value)
        json.put("screenName", current?.name ?: "")
        json.put("template", current?.template?.name ?: "")
        json.put("weatherCity", repository.weather.value.city)
        json.put("weatherTemp", repository.weather.value.temperature)
        json.put("screensCount", repository.screens.value.size)
        return json.toString()
    }

    private fun buildWebDashboardHtml(): String {
        val screens = repository.screens.value
        val activeScreen = repository.currentScreen.value
        val activeScreenId = repository.currentScreenId.value

        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart Display Lite - لوحة التحكم عن بُعد</title>
    <style>
        :root {
            --bg-color: #0b1120;
            --card-bg: #1e293b;
            --accent: #38bdf8;
            --text-main: #f8fafc;
            --text-muted: #94a3b8;
            --success: #10b981;
            --gold: #f59e0b;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui, -apple-system, sans-serif; }
        body { background: var(--bg-color); color: var(--text-main); padding: 20px; line-height: 1.6; }
        .container { max-width: 900px; margin: 0 auto; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #334155; padding-bottom: 15px; margin-bottom: 25px; }
        h1 { font-size: 24px; color: var(--accent); }
        .badge { background: #0284c7; color: white; padding: 4px 10px; border-radius: 20px; font-size: 13px; }
        .online-dot { width: 10px; height: 10px; background: var(--success); border-radius: 50%; display: inline-block; margin-left: 6px; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; margin-bottom: 25px; }
        .screen-card { background: var(--card-bg); border-radius: 12px; padding: 18px; border: 2px solid transparent; cursor: pointer; transition: 0.2s; }
        .screen-card.active { border-color: var(--accent); background: #162a45; }
        .screen-card h3 { font-size: 17px; margin-bottom: 6px; }
        .screen-card p { font-size: 13px; color: var(--text-muted); margin-bottom: 10px; }
        .panel { background: var(--card-bg); border-radius: 12px; padding: 22px; margin-bottom: 20px; }
        .panel h2 { font-size: 18px; margin-bottom: 16px; color: #e2e8f0; border-right: 4px solid var(--accent); padding-right: 10px; }
        label { display: block; font-size: 13px; color: var(--text-muted); margin-bottom: 6px; margin-top: 12px; }
        input, select, textarea { width: 100%; padding: 10px 14px; background: #0f172a; border: 1px solid #334155; border-radius: 8px; color: white; font-size: 14px; }
        button { background: var(--accent); color: #0b1120; border: none; font-weight: bold; padding: 12px 20px; border-radius: 8px; cursor: pointer; margin-top: 15px; width: 100%; transition: 0.2s; }
        button:hover { background: #7dd3fc; }
        .templates-btn { display: flex; gap: 10px; margin-top: 10px; flex-wrap: wrap; }
        .t-btn { flex: 1; padding: 10px; background: #0f172a; border: 1px solid #334155; color: white; border-radius: 8px; cursor: pointer; text-align: center; font-size: 13px; }
        .t-btn.active { border-color: var(--gold); color: var(--gold); background: #262010; font-weight: bold; }
        .toast { display: none; background: var(--success); color: white; padding: 12px; border-radius: 8px; text-align: center; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <div>
                <h1>Smart Display Lite</h1>
                <p style="font-size: 13px; color: var(--text-muted);">لوحة التحكم اللحظية بالشاشات الثلاث (Android TV)</p>
            </div>
            <span class="badge"><span class="online-dot"></span> متصل مباشرة</span>
        </header>

        <div id="toast" class="toast">تم تحديث الإعدادات وإرسالها للتلفزيون فوراً!</div>

        <h3 style="margin-bottom: 12px; font-size: 16px;">اختيار الشاشة للتحكم بها:</h3>
        <div class="grid">
            ${screens.map { s -> """
                <div class="screen-card ${if (s.id == activeScreenId) "active" else ""}" onclick="selectScreen('${s.id}')">
                    <h3>${s.name}</h3>
                    <p>${s.location}</p>
                    <div style="font-size: 12px; color: #38bdf8;">القالب: ${s.template.titleAr}</div>
                    <div style="font-size: 11px; color: #94a3b8; margin-top: 6px;">كود الربط: <strong>${s.pairingCode}</strong></div>
                </div>
            """ }.joinToString("")}
        </div>

        <div class="panel">
            <h2>تغيير قالب العرض (Live Template)</h2>
            <div class="templates-btn">
                <div class="t-btn ${if (activeScreen?.template == SignageTemplate.CORPORATE_DASHBOARD) "active" else ""}" onclick="setTemplate('CORPORATE_DASHBOARD')">
                    لوحة المؤسسة الشاملة
                </div>
                <div class="t-btn ${if (activeScreen?.template == SignageTemplate.ADVERTISING_DISPLAY) "active" else ""}" onclick="setTemplate('ADVERTISING_DISPLAY')">
                    شاشة الإعلانات والوسائط
                </div>
                <div class="t-btn ${if (activeScreen?.template == SignageTemplate.EXECUTIVE_DASHBOARD) "active" else ""}" onclick="setTemplate('EXECUTIVE_DASHBOARD')">
                    لوحة القيادة التنفيذية
                </div>
            </div>
        </div>

        <div class="panel">
            <h2>إعدادات الهوية وشريط الأخبار والطقس</h2>
            <label>اسم الشركة / المنشأة:</label>
            <input type="text" id="companyName" value="${activeScreen?.companyName ?: ""}">

            <label>مدينة الطقس (Open-Meteo):</label>
            <select id="weatherCity">
                <option value="Riyadh" ${if (activeScreen?.city == "Riyadh") "selected" else ""}>الرياض</option>
                <option value="Jeddah" ${if (activeScreen?.city == "Jeddah") "selected" else ""}>جدة</option>
                <option value="Dammam" ${if (activeScreen?.city == "Dammam") "selected" else ""}>الدمام</option>
                <option value="Mecca" ${if (activeScreen?.city == "Mecca") "selected" else ""}>مكة المكرمة</option>
                <option value="Medina" ${if (activeScreen?.city == "Medina") "selected" else ""}>المدينة المنورة</option>
                <option value="Khobar" ${if (activeScreen?.city == "Khobar") "selected" else ""}>الخبر</option>
                <option value="Dubai" ${if (activeScreen?.city == "Dubai") "selected" else ""}>دبي</option>
            </select>

            <label>شريط الأخبار المتحرك (Ticker):</label>
            <textarea id="tickerText" rows="3">${activeScreen?.customTickerText ?: ""}</textarea>

            <button onclick="saveScreenSettings()">حفظ وتحديث الشاشة فوراً</button>
        </div>

        <div class="panel">
            <h2>نشر إعلان جديد على الشاشات</h2>
            <label>عنوان الإعلان الرئيسي:</label>
            <input type="text" id="adTitle" placeholder="مثال: ورشة تدريبية في الذكاء الاصطناعي">

            <label>تفاصيل الإعلان:</label>
            <input type="text" id="adSubtitle" placeholder="الموعد والمكان والتفاصيل">

            <label>وسم الإعلان (Badge):</label>
            <input type="text" id="adBadge" value="إعلان مؤسسي">

            <label>مدة العرض (ثوانٍ):</label>
            <input type="number" id="adDuration" value="10">

            <button onclick="addAnnouncement()">نشر الإعلان على الشاشة</button>
        </div>
    </div>

    <script>
        function showToast(msg) {
            const t = document.getElementById('toast');
            if (msg) t.innerText = msg;
            t.style.display = 'block';
            setTimeout(() => { t.style.display = 'none'; }, 3000);
        }

        async function selectScreen(id) {
            await fetch('/api/switch-screen', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({screenId: id})
            });
            window.location.reload();
        }

        async function setTemplate(tpl) {
            await fetch('/api/update-template', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({template: tpl})
            });
            showToast('تم تغيير القالب بنجاح على التلفزيون!');
            setTimeout(() => window.location.reload(), 1000);
        }

        async function saveScreenSettings() {
            const company = document.getElementById('companyName').value;
            const city = document.getElementById('weatherCity').value;
            const ticker = document.getElementById('tickerText').value;

            await fetch('/api/update-screen', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    companyName: company,
                    city: city,
                    tickerText: ticker
                })
            });
            showToast('تم حفظ الإعدادات وتحديث الشاشة مباشرة!');
        }

        async function addAnnouncement() {
            const title = document.getElementById('adTitle').value;
            const subtitle = document.getElementById('adSubtitle').value;
            const badge = document.getElementById('adBadge').value;
            const duration = parseInt(document.getElementById('adDuration').value) || 10;

            if (!title) { alert('يرجى إدخال عنوان الإعلان'); return; }

            await fetch('/api/add-announcement', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    title: title,
                    subtitle: subtitle,
                    badge: badge,
                    duration: duration,
                    targetScreen: 'all'
                })
            });
            showToast('تم إضافة الإعلان وبدء عرضه على التلفزيون!');
            document.getElementById('adTitle').value = '';
            document.getElementById('adSubtitle').value = '';
        }
    </script>
</body>
</html>
        """.trimIndent()
    }
}
