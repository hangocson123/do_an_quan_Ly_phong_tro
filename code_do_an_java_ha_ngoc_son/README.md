# AN CƯ – Quản lý phòng trọ

## Chạy dự án

1. Giải nén thư mục `ha_ngoc_son`.
2. Mở thư mục `code_do_an_java_ha_ngoc_son` bằng Visual Studio Code.
3. Đảm bảo SQL Server đang chạy và cấu hình kết nối trong `src/com/phongtro/dao/DatabaseConnection.java` khớp với máy của bạn.
4. Mở `src/App.java` và chọn **Run Java**.

JDBC Driver đã được giữ trong thư mục `lib/`; cấu hình VS Code sử dụng `src` làm source path và `bin` làm output path.

## Giao diện mới

Dự án sử dụng phong cách **AN CƯ – Property Desk** với xanh rừng, nền giấy ấm và điểm nhấn màu đồng. Màn hình đăng nhập và dashboard được thiết kế lại; các cửa sổ quản lý phòng, khách thuê, loại phòng, nhân viên và khu vực dành cho người thuê dùng chung bộ màu, kiểu ô nhập, nút thao tác và bảng dữ liệu.

## Phạm vi thay đổi

- Đã nâng cấp các lớp giao diện Swing và bổ sung `com.phongtro.ui.UITheme`.
- Giữ nguyên các lớp DAO, model và cấu trúc bảng/cơ sở dữ liệu hiện có.
- Không tự thay đổi thông tin đăng nhập SQL Server trong `DatabaseConnection.java`.

## Sửa lỗi hiển thị dấu tiếng Việt

Các tiêu đề kiểu serif đã chuyển từ font Georgia sang logical font Serif của Java để Java tự fallback glyph, tránh hiện ô vuông ở các ký tự tiếng Việt như Ư, ư, ở, ễ. Các nhãn và trường nhập vẫn dùng Segoe UI.
