@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        setContent { MaterialTheme { AppRoot() } }
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
private fun AppRoot() {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val todayJd = remember { Today.ymd().let { LunarCalendar.jdFromDate(it[0], it[1], it[2]) } }
    var selectedJd by rememberSaveable { mutableIntStateOf(todayJd) }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
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
