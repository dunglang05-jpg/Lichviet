# Cân xương tính số (cân lượng chỉ)

Nguồn chính: https://tracuutuvi.com/cach-tinh-can-luong-chi.html (đối chiếu hocvienlyso.org, thayungkhiem.vn — cùng bảng, kể cả tháng 10 = 1 lượng 8 chỉ).
Cài đặt trong app: core/lunar/.../fortune/CanXuong.java, màn hình feature/tools/.../CanXuongScreen.kt.

## Cách tính
1. Đổi ngày giờ sinh sang âm lịch.
2. Tra 4 giá trị: năm (can chi), tháng, ngày, giờ (chi). Đơn vị chỉ; 10 chỉ = 1 lượng.
3. Cộng lại → tổng từ 2 lượng 1 chỉ đến 7 lượng 2 chỉ; tra lời giải theo tổng.
Ví dụ của nguồn: giờ Dần 0.7 + ngày 24 0.9 + tháng 10 1.8 + năm Canh Thìn 1.2 = 4 lượng 6 chỉ.
Nam nữ dùng chung bảng. Phép xem dân gian, chủ yếu xem chữ "phú", chỉ để tham khảo.

## Bảng (đơn vị lượng)
Giờ: Tý 1.6, Sửu 0.6, Dần 0.7, Mão 1.0, Thìn 0.9, Tỵ 1.6, Ngọ 1.0, Mùi 0.8, Thân 0.8, Dậu 0.9, Tuất 0.6, Hợi 0.6.
Tháng 1–12: 0.6, 0.7, 1.8, 0.9, 0.5, 1.6, 0.9, 1.5, 1.8, 1.8, 0.9, 0.5.
Ngày 1–30: 0.5, 1.0, 0.8, 1.5, 1.6, 1.5, 0.8, 1.6, 0.8, 1.6, 0.9, 1.7, 0.8, 1.7, 1.0, 0.8, 0.9, 1.8, 0.5, 1.5, 1.0, 0.9, 0.8, 0.9, 1.5, 1.8, 0.7, 0.8, 1.6, 0.6.
Năm:
- Tý: Giáp 1.2, Bính 1.6, Mậu 1.5, Canh 0.7, Nhâm 0.5
- Sửu: Ất 0.9, Đinh 0.8, Kỷ 0.8, Tân 0.7, Quý 0.5
- Dần: Giáp 1.2, Bính 0.6, Mậu 0.8, Canh 0.9, Nhâm 0.9
- Mão: Ất 0.8, Đinh 0.7, Kỷ 1.9, Tân 1.2, Quý 1.2
- Thìn: Giáp 0.8, Bính 0.8, Mậu 1.2, Canh 1.2, Nhâm 1.0
- Tỵ: Ất 0.7, Đinh 0.6, Kỷ 0.5, Tân 0.6, Quý 0.7
- Ngọ: Giáp 1.5, Bính 1.3, Mậu 1.9, Canh 0.9, Nhâm 0.8
- Mùi: Ất 0.6, Đinh 0.5, Kỷ 0.6, Tân 0.8, Quý 0.7
- Thân: Giáp 0.5, Bính 0.5, Mậu 1.4, Canh 0.8, Nhâm 0.7
- Dậu: Ất 1.5, Đinh 1.4, Kỷ 0.5, Tân 1.6, Quý 0.8
- Tuất: Giáp 0.5, Bính 0.6, Mậu 1.4, Canh 0.9, Nhâm 1.0
- Hợi: Ất 0.9, Đinh 1.6, Kỷ 0.9, Tân 1.7, Quý 0.7

Lời giải từng mức: xem CanXuong.meaning() (tóm lược, diễn đạt lại từ nguồn).
