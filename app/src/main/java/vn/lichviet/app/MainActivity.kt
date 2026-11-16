@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app

import vn.lichviet.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import vn.lichviet.tools.ToolsScreen
import vn.lichviet.settings.SettingsScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.NavigationBarItemDefaults

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
import vn.lichviet.ui.dayOf
import vn.lichviet.ui.theme.LichTheme
import vn.lichviet.ui.theme.Themes
import vn.lichviet.ui.theme.drawPattern
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

private enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    DAY("Ngày", "Lịch ngày", Icons.Filled.Home),
    MONTH("Tháng", "Lịch tháng", Icons.Filled.DateRange),
    MOON("Trăng", "Tuần trăng", Icons.Filled.Star),
    EVENTS("Sự kiện", "Sự kiện & ngày lễ", Icons.Filled.Notifications),
    TOOLS("Tiện ích", "Tiện ích", Icons.Filled.Build),
}

@Composable
private fun AppRoot(activity: ComponentActivity) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
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
        Modifier.fillMaxSize().background(spec.bg),
    ) {
    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
    } else Scaffold(
        containerColor = Color.Transparent,
        contentColor = spec.ink,
        topBar = {
            if (Tab.entries[tab] != Tab.TOOLS) AppHeader(Tab.entries[tab].title, "${spec.name} · ${spec.variant}", onSettings = { showSettings = true })
        },
        bottomBar = {
            Column {
                NavigationBar(containerColor = spec.card) {
                    Tab.entries.forEachIndexed { i, t ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = { Icon(t.icon, contentDescription = t.label) },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = spec.onAccent2, indicatorColor = spec.accent2,
                                selectedTextColor = spec.accent, unselectedIconColor = spec.dim, unselectedTextColor = spec.dim,
                            ),
                        )
                    }
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
            Tab.TOOLS -> ToolsScreen(todayJd, onOpenDay = { selectedJd = it; tab = 0 }, onSettings = { showSettings = true },
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()))
        }
    }
    }
    }
}
