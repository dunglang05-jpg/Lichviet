# Lịch Việt – Âm lịch Việt Nam cho Android

Ứng dụng lịch âm Việt Nam, chạy offline 100%: lịch ngày, lịch tháng có âm lịch, ngày tốt xấu,
giờ hoàng đạo, lịch trăng, ngày lễ, việc thuận tiện, đổi ngày, sự kiện cá nhân, nhắc nhở và 3 widget.

> Phiên bản 0.2: thêm lịch vạn niên chi tiết (xem bảng Độ chính xác).
> Phiên bản 0.1: **đủ tính năng, giao diện tạm** (Material mặc định). 10 giao diện sẽ làm ở bản sau
> (cài đặt đã có sẵn trường `themeId` 0..9).

## Lấy file APK (không cần cài gì)

1. Tạo repo mới trên GitHub, đẩy toàn bộ thư mục này lên (giữ nguyên `.github/`).
2. Vào tab **Actions** → workflow **Build APK** tự chạy (hoặc bấm *Run workflow*).
3. Khi xong (~5–8 phút), mở lần chạy → mục **Artifacts** → tải `LichViet-apk.zip`.
4. Giải nén, cài `LichViet.apk` trên điện thoại (cho phép "Cài ứng dụng không rõ nguồn").

Hoặc mở bằng Android Studio → *Build › Build APK(s)*.

## Cấu trúc module

```
core/lunar        Java thuần, không phụ thuộc Android – thuật toán & dữ liệu
  LunarCalendar     đổi dương ⇄ âm (thuật toán thiên văn Hồ Ngọc Đức, UTC+7), tháng nhuận
  DayInfo           can chi, tiết khí, trực (theo tháng tiết khí), 12 thần (hoàng/hắc đạo), 28 sao,
                    giờ hoàng đạo, nên/kỵ, điểm ngày 1–5
  Almanac           lịch vạn niên chi tiết: 111 sao tốt/xấu Ngọc Hạp Thông Thư, ngày kỵ (Tam Nương,
                    Nguyệt Kỵ, Dương Công, Sát Chủ Dương/Âm, Thụ Tử, Nguyệt Tận), Khổng Minh lục diệu,
                    giờ xuất hành Lý Thuần Phong, nạp âm ngày/tháng/năm, quan hệ can–chi (bảo/nghĩa/
                    chế/phạt/chuyên nhật), tuổi xung & ngoại lệ, lục hợp–tam hợp–xung–hình–hại–phá,
                    Tam Sát, Bành Tổ bách kỵ, Hỷ thần, Tài thần, Hạc thần
  AlmanacData       bảng dữ liệu tự sinh (tools/gen_almanac.py)
  Holidays          ngày lễ dương & âm lịch, mùng 1, rằm, giao thừa
  MoonPhase         tuổi trăng, độ sáng, tên pha, các mốc trăng sắp tới
  Activity          danh sách việc (cưới hỏi, khai trương, động thổ…) ↔ Trực hợp
  GoodDayFinder     tìm ngày thuận tiện cho một việc (Trực + ngày kỵ + sao xấu kỵ riêng việc đó)
  Countdown         đếm ngược Tết, ngày âm/dương lặp hằng năm
  MonthGrid         lưới 6×7 cho lịch tháng & widget
core/data         sự kiện người dùng (âm/dương, lặp năm, nhắc trước), cài đặt, "hôm nay" giờ VN
feature/reminder  thông báo mỗi sáng, nhắc mùng 1/rằm, lễ lớn, sự kiện cá nhân (WorkManager)
feature/widget    3 widget: Ngày (2×2), Chi tiết ngày (4×2), Tháng (4×4); tự cập nhật nửa đêm
app               điều hướng + màn hình tạm: Ngày · Tháng · Trăng · Sự kiện · Tiện ích
```

Phụ thuộc chỉ đi một chiều: `app → feature/* → core/data → core/lunar`.

## Mở rộng sau này

- **Thêm việc cần xem ngày**: thêm một dòng vào `Activity.java`.
- **Thêm ngày lễ**: thêm vào mảng `SOLAR`/`LUNAR` trong `Holidays.java`.
- **Thêm tính năng lớn** (xem tuổi, văn khấn, la bàn, đồng bộ Google Calendar…): tạo module
  `feature/<tên>` mới, khai báo trong `settings.gradle.kts`, cho `app` phụ thuộc vào.
- **10 giao diện**: thay `MaterialTheme` trong `MainActivity` và các file trong `app/.../ui/`;
  widget đọc `SettingsRepository.settings.themeId` để đổi màu.
- **Lưu trữ**: `EventRepository` đang dùng SharedPreferences; có thể đổi sang Room mà giữ nguyên API.

## Độ chính xác (đối chiếu 109.938 ngày, 1900–2200)

Đối chiếu với bộ dữ liệu [ILamTH/du-lieu-ngay-tot-xau](https://github.com/ILamTH/du-lieu-ngay-tot-xau)
(lấy từ xemlicham.com), giai đoạn 1950–2100:

| Mục | Khớp |
|---|---|
| Can chi ngày | 100% |
| Giờ hoàng đạo | 99% |
| Ngày kỵ (Tam Nương, Nguyệt Kỵ, Dương Công, Sát Chủ, Thụ Tử) | 99,5–99,9% |
| Sao Ngọc Hạp – trùng khớp toàn bộ danh sách trong ngày | 98,5% (độ chính xác từng sao 98,7%) |
| Khổng Minh lục diệu, giờ Lý Thuần Phong, nạp âm, tuổi xung, Bành Tổ | 98,4–98,7% |
| 28 sao (từ 2017; dữ liệu nguồn trước 2017 bị lỗi) | 99% |
| Trực | 95,4% – app tính theo tiết khí thiên văn; nguồn dùng mốc tiết gần đúng nên lệch vài ngày quanh lúc giao tiết |

Phần chưa khớp còn lại phần lớn là các ngày bộ dữ liệu bị trống hoặc ghi khác định dạng.
"Kim Thần Thất Sát" chưa đưa vào vì dữ liệu nguồn không theo quy luật cổ điển.

## Tái tạo dữ liệu (khi muốn cập nhật bảng sao)

1. `git clone https://github.com/ILamTH/du-lieu-ngay-tot-xau`
2. Xuất lịch của app ra CSV (lớp `Dump` trong tools), chạy lần lượt `fit.py` → `derive.py` → `ky.py` → `gen_almanac.py`.
3. `gen_almanac.py` ghi đè `core/lunar/.../AlmanacData.java`.

## Kiểm thử

`./gradlew :core:lunar:test` – đối chiếu Tết 2024–2027, tháng nhuận 2023/2025, can chi, trực, sao,
giờ hoàng đạo, tiết khí, sao Ngọc Hạp, ngày kỵ, lục diệu, nạp âm, và kiểm tra khứ hồi
dương→âm→dương mọi ngày 1900–2100.

Thông tin ngày tốt xấu mang tính tham khảo theo văn hoá dân gian.
