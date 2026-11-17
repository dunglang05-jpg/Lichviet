package vn.lichviet.app.ui

import vn.lichviet.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.DayInfo
import vn.lichviet.core.GoodDayFinder
import vn.lichviet.data.SettingsRepository

/** Lịch ngày + xem ngày tốt xấu đầy đủ (lịch vạn niên). */
@Composable
fun DayScreen(jd: Int, todayJd: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val settings by repo.settings.collectAsState()
    var editProfile by remember { mutableStateOf(false) }
    val d = remember(jd) { dayOf(jd) }
    val fits = remember(jd) { GoodDayFinder.activitiesFor(d) }
    val a = remember(jd) { d.almanac() }

    Column(modifier.verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 16.dp)) {
        WeekStrip(jd, todayJd, settings.mondayFirst, onChange)
        DaySheet(jd, todayJd, onChange, Modifier.padding(top = 4.dp))

        DayOverviewCard(d)
        NenTranhCards(d, fits.takeIf { it.isNotEmpty() }?.joinToString(", ") { it.label })
        GioHoangDaoCard(d)
        ThienDiaNhanCard(d, settings) { editProfile = true }
        NguHanhTuoiXungCard(d, a)
        NgayKyCard(d, a)
        BanhToCard(a)
        KhongMinhCard(a)
        NhiThapBatTuCard(d)
        TrucCard(d)
        NgocHapCard(a)
        HuongXuatHanhCard(a)
        LyThuanPhongCard(a)
        ActivityGridCard(jd, onChange)
        TietKhiCard(d)
    }

    if (editProfile) {
        ProfileDialog(settings, onDismiss = { editProfile = false }) { name, year ->
            repo.update { it.copy(userName = name, birthYear = year) }
            editProfile = false
        }
    }
}
