@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import kotlinx.coroutines.delay
import vn.lichviet.app.ui.dayOf
import vn.lichviet.app.ui.theme.LichTheme
import vn.lichviet.app.ui.theme.Themes
import vn.lichviet.app.ui.theme.drawPattern
import vn.lichviet.data.SettingsRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import vn.lichviet.app.ui.DayScreen
import vn.lichviet.app.ui.EventsScreen
import vn.lichviet.app.ui.MonthScreen
import vn.lichviet.app.ui.MoonScreen
import vn.lichviet.app.ui.ToolsScreen
import vn.lichviet.core.LunarCalendar
import vn.lichviet.data.Today

/**
 * Khung điều hướng TẠM THỜI: chỉ để dùng/kiểm tra tính năng.
 * Phần 10 giao diện sẽ thay MaterialTheme mặc định và các màn hình trong gói ui.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot(this) }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    DAY("Ngày", Icons.Filled.Home),
    MONTH("Tháng", Icons.Filled.DateRange),
    MOON("Trăng", Icons.Filled.Star),
    EVENTS("Sự kiện", Icons.Filled.Notifications),
    TOOLS("Tiện ích", Icons.Filled.Build),
}

@Composable
private fun AppRoot(activity: ComponentActivity) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val todayJd = remember { Today.ymd().let { LunarCalendar.jdFromDate(it[0], it[1], it[2]) } }
    var selectedJd by rememberSaveable { mutableIntStateOf(todayJd) }

    // Giao diện: đổi theo cài đặt, theo ngày và (Thời Thần) theo giờ – kiểm tra lại mỗi phút
    val settings by SettingsRepository.get(activity).settings.collectAsState()
    val hour by produceState(Today.calendar().get(Calendar.HOUR_OF_DAY)) {
        while (true) {
            delay(60_000)
            value = Today.calendar().get(Calendar.HOUR_OF_DAY)
        }
    }
    val today = remember(todayJd) { dayOf(todayJd) }
    val spec = remember(settings.themeId, settings.autoSeason, todayJd, hour) {
        Themes.resolve(settings.themeId, settings.autoSeason, today, hour)
    }
    LaunchedEffect(spec.dark) {
        val style = if (spec.dark) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        activity.enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    LichTheme(spec) {
    Box(
        Modifier.fillMaxSize().background(spec.bg).drawBehind { drawPattern(spec.pattern, spec.patternColor, spec.patternAlpha) },
    ) {
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = spec.ink,
        bottomBar = {
            NavigationBar(containerColor = spec.card) {
                Tab.entries.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        val m = Modifier.padding(padding)
        when (Tab.entries[tab]) {
            Tab.DAY -> DayScreen(selectedJd, todayJd, onChange = { selectedJd = it }, modifier = m)
            Tab.MONTH -> MonthScreen(selectedJd, todayJd, onPick = { selectedJd = it; tab = 0 }, modifier = m)
            Tab.MOON -> MoonScreen(selectedJd, modifier = m)
            Tab.EVENTS -> EventsScreen(todayJd, onOpenDay = { selectedJd = it; tab = 0 }, modifier = m)
            Tab.TOOLS -> ToolsScreen(todayJd, onOpenDay = { selectedJd = it; tab = 0 }, modifier = m)
        }
    }
    }
    }
}
