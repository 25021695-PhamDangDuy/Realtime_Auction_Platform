package server.command;


 // Mở ra nếu bạn có dùng ClientManager để quản lý User Online

import server.ClientSession;
import server.Role;

import java.util.Set;

public class LogoutCommand implements Command {

    @Override
    public Set<Role> getAllowedRoles() {
        // Chỉ những người ĐÃ ĐĂNG NHẬP mới có quyền gọi lệnh đăng xuất.
        // GUEST (người chưa đăng nhập) không được phép dùng lệnh này.
        return Set.of(Role.BIDDER, Role.SELLER, Role.ADMIN);
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // 1. Kiểm tra an toàn (dù đã có chặn Role ở trên nhưng vẫn nên kiểm tra)
        if (session.getCurrentUser() == null) {
            session.sendMessage("ERROR|Bạn chưa đăng nhập!");
            return;
        }

        // Lưu lại tên để in thông báo trên màn hình Server trước khi xóa
        String username = session.getCurrentUser().getName(); // Hoặc getName() tùy class User của bạn

        // --- GIAI ĐOẠN DỌN DẸP BỘ NHỚ (CLEAN UP) ---

        // 2. (tính năng Theo dõi phòng):
        // Phải gạch tên khách này khỏi tất cả các danh sách nhận thông báo phòng đấu giá!

        // 3. (Tùy chọn): Xóa khách khỏi danh sách "Đang Online" của hệ thống

        // 4. Xóa cuốn hồ sơ: Rút thông tin User ra và hạ quyền xuống GUEST
        session.setCurrentUser(null);
        session.setRole(Role.GUEST);

        // 5. Báo tin về cho máy khách (Client)
        session.sendMessage("SUCCESS|Đăng xuất thành công. Hẹn gặp lại!");


    }
}