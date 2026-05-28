package myWeb.function;

import myWeb.exception.NotificationException;

public class NotifyService {
    private final SystemLogger logger = SystemLogger.getInstance();
    public void sendNotification(String userId, String message){
        logger.info("Bắt đầu tiến trình gửi thông báo tới User [" + userId + "]");

        try {
            // STEP 1: Kiểm tra dữ liệu đầu vào (Validation)
            if (userId == null || userId.isEmpty()) {
                throw new NotificationException("Không thể gửi thông báo vì UserId bị trống!");
            }
            if (message == null || message.isEmpty()) {
                throw new NotificationException("Nội dung thông báo cho User [" + userId + "] không được để trống!");
            }

            // STEP 2: Giả lập logic kết nối mạng / WebSocket để gửi thông báo
            boolean isServerConnected = checkConnection(); // Hàm giả định kiểm tra kết nối
            if (!isServerConnected) {
                // Nếu mất kết nối mạng, ném ra lỗi hệ thống kèm nguyên nhân (IOException giả lập)
                throw new NotificationException("Mất kết nối tới máy chủ thông báo Realtime (WebSocket).");
            }

            // STEP 3: Thực hiện gửi dữ liệu thành công
            System.out.println("--> [Network] Đã đẩy thông báo tới client thành công: " + message);
            logger.info("Gửi thông báo thành công cho User [" + userId + "]");

        } catch (NotificationException e) {
            // BẮT LỖI CHUYÊN BIỆT: Đây chính là nơi kết nối giữa nghiệp vụ và Logger
            // Hàm error lúc này nhận cả 'e' (Throwable) để ghi lại toàn bộ StackTrace vào file log
            logger.error("LỖI NGHIỆP VỤ: Tiến trình gửi thông báo thất bại! Thao tác bị từ chối.", e);

            // Bạn có thể xử lý thêm ở đây (ví dụ: lưu thông báo vào database để gửi lại sau - Retry queue)

        } catch (Exception e) {
            // Bắt các lỗi hệ thống không lường trước được (ví dụ: NullPointerException, OutOfMemory...)
            logger.crash("LỖI HỆ THỐNG NGHIÊM TRỌNG: Gây sập tiến trình Notify!", e);
        }
    }

    // Hàm bổ trợ giả lập kiểm tra kết nối mạng
    private boolean checkConnection() {
        // Giả lập: trả về true là bình thường, false là mất mạng
        return true;
    }


}
