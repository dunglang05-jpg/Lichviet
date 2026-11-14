@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vn.lichviet.app.ui.ShadowCard
import vn.lichviet.app.ui.SectionTitle

/** Danh sách tiện ích. [group] 0 = Gợi ý cho bạn, 1 = Tiện ích khác. */
enum class Tool(val title: String, val icon: ImageVector, val color: Long, val group: Int) {
    VAN_HAN("Vận Hạn Năm", Icons.Filled.Insights, 0xFFE53935, 0),
    TINH_DUYEN("Tình Duyên", Icons.Filled.Favorite, 0xFFEC407A, 0),
    NGHE_NGHIEP("Định Hướng Nghề Nghiệp", Icons.Filled.Work, 0xFF3F51B5, 0),
    TAI_CHINH("Tư Vấn Tài Chính", Icons.Filled.AttachMoney, 0xFF43A047, 0),
    TONG_QUAN_VAN_MENH("Tổng Quan Vận Mệnh", Icons.Filled.AutoAwesome, 0xFFD81B60, 0),
    NOI_BAT("Nổi Bật Hôm Nay", Icons.Filled.Today, 0xFFFB8C00, 0),
    HOP_NHAU("Mức Độ Hợp Nhau", Icons.Filled.People, 0xFFFFA000, 0),
    PHONG_THUY("Tư Vấn Phong Thủy", Icons.Filled.Home, 0xFFC0A000, 0),
    GIEO_QUE("Gieo Quẻ Hỏi Việc", Icons.Filled.Casino, 0xFFAD1457, 0),
    CHON_TEN("Chọn Tên Hay", Icons.Filled.Badge, 0xFF1E88E5, 0),
    THANH_VIEN("Thành Viên Gia Đình", Icons.Filled.FamilyRestroom, 0xFFC62828, 0),
    DOI_NGAY("Đổi Ngày", Icons.Filled.SwapHoriz, 0xFF8D3B2F, 0),
    THAN_SO("Thần Số Học", Icons.Filled.Numbers, 0xFF00897B, 0),
    THONG_TIN("Thông Tin Tổng Quan", Icons.Filled.AccountBox, 0xFF5C6BC0, 0),
    GIAI_MA_NGAY_SINH("Giải Mã Ngày Sinh", Icons.Filled.Cake, 0xFF7E57C2, 0),
    NHAC_GIO("Nhắc Ngày Giỗ", Icons.Filled.NotificationsActive, 0xFF6A5ACD, 1),
    VAN_KHAN("Văn Khấn", Icons.Filled.MenuBook, 0xFFEF6C00, 1),
    SONG_KHOE("Sống Khỏe", Icons.Filled.SelfImprovement, 0xFF7CB342, 1),
    TU_VI_NGAY("Tử Vi Hằng Ngày", Icons.Filled.WbSunny, 0xFF26A69A, 1),
    GIA_PHA("Gia Phả", Icons.Filled.AccountTree, 0xFFB8860B, 1),
    HOANG_DAO_12("12 Cung Hoàng Đạo", Icons.Filled.Stars, 0xFF3949AB, 1),
    TU_VI_TRON_DOI("Tử Vi Trọn Đời", Icons.Filled.AllInclusive, 0xFF039BE5, 1),
    BOI_TINH_DUYEN("Bói Tình Duyên", Icons.Filled.VolunteerActivism, 0xFFF06292, 1),
    LO_BAN("Thước Lỗ Ban", Icons.Filled.Straighten, 0xFF1565C0, 1),
    LA_BAN("La Bàn", Icons.Filled.Explore, 0xFF5E35B1, 1),
    XEM_SAO("Xem Sao Hạn", Icons.Filled.Star, 0xFF2E7D32, 1),
    FAQ("Câu Hỏi Thường Gặp", Icons.Filled.QuestionAnswer, 0xFFF9A825, 1),
    GIAI_MONG("Giải Mộng", Icons.Filled.Bedtime, 0xFF8E24AA, 1),
}

@Composable
fun ToolIcon(t: Tool, size: Int = 56) {
    val c = Color(t.color)
    Box(
        Modifier.size(size.dp).shadow(4.dp, CircleShape).clip(CircleShape)
            .background(Brush.linearGradient(listOf(c.copy(alpha = 0.75f), c))),
        contentAlignment = Alignment.Center,
    ) { Icon(t.icon, null, tint = Color.White, modifier = Modifier.size((size * 0.5f).dp)) }
}

@Composable
fun ToolGrid(onOpen: (Tool) -> Unit) {
    listOf(0 to "Gợi ý cho bạn", 1 to "Tiện ích khác").forEach { (g, title) ->
        ShadowCard(elevation = 6.dp) {
            SectionTitle(title)
            val items = Tool.entries.filter { it.group == g }
            items.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { t ->
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).clickable { onOpen(t) }.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            ToolIcon(t)
                            Spacer(Modifier.height(6.dp))
                            Text(t.title, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, maxLines = 2,
                                fontWeight = FontWeight.Medium)
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** Trang con của một tiện ích, có nút quay lại. */
@Composable
fun ToolPage(tool: Tool, todayJd: Int, onOpenDay: (Int) -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    BackHandler(onBack = onBack)
    Column(modifier.verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Quay lại") }
            ToolIcon(tool, 36)
            Text(tool.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp))
        }
        when (tool) {
            Tool.VAN_HAN -> VanHanScreen()
            Tool.TINH_DUYEN -> MenhAdviceScreen(MenhTopic.TINH_DUYEN)
            Tool.NGHE_NGHIEP -> MenhAdviceScreen(MenhTopic.NGHE)
            Tool.TAI_CHINH -> MenhAdviceScreen(MenhTopic.TAI_CHINH)
            Tool.TONG_QUAN_VAN_MENH -> TongQuanVanMenhScreen()
            Tool.NOI_BAT -> NoiBatScreen(todayJd, onOpenDay)
            Tool.HOP_NHAU -> HopNhauScreen(love = false)
            Tool.BOI_TINH_DUYEN -> HopNhauScreen(love = true)
            Tool.PHONG_THUY -> PhongThuyScreen()
            Tool.GIEO_QUE -> GieoQueScreen()
            Tool.CHON_TEN -> ChonTenScreen()
            Tool.THANH_VIEN -> FamilyScreen()
            Tool.DOI_NGAY -> DoiNgayScreen(todayJd, onOpenDay)
            Tool.THAN_SO -> ThanSoScreen()
            Tool.THONG_TIN -> ThongTinScreen()
            Tool.GIAI_MA_NGAY_SINH -> GiaiMaNgaySinhScreen()
            Tool.NHAC_GIO -> NhacGioScreen()
            Tool.VAN_KHAN -> VanKhanScreen(todayJd)
            Tool.SONG_KHOE -> SongKhoeScreen(todayJd)
            Tool.TU_VI_NGAY -> TuViNgayScreen(todayJd)
            Tool.GIA_PHA -> GiaPhaScreen()
            Tool.HOANG_DAO_12 -> HoangDao12Screen(todayJd)
            Tool.TU_VI_TRON_DOI -> TuViTronDoiScreen()
            Tool.LO_BAN -> LoBanScreen()
            Tool.LA_BAN -> LaBanScreen()
            Tool.XEM_SAO -> XemSaoScreen()
            Tool.FAQ -> FaqScreen()
            Tool.GIAI_MONG -> GiaiMongScreen()
        }
    }
}
