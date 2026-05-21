package myWeb.controller;

import myWeb.function.SessionChecker;
import myWeb.function.SessionStatus;
import myWeb.models.Bidder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionSessionWatcher implements Runnable{
    private AuctionManager manager;
    private volatile boolean status = true;
    private List<AuctionSession> failedSession = new ArrayList<>();
    /*
    Tại sao phải dùng volatile? Bản chất của Thread khi tạo sẽ có một vùng bộ nhớ đệm (cache) lưu các biến thuộc tính của class
    Vì vậy bản chất khi ta chạy run() nó sẽ chỉ soi biến status trong cache của riêng thread đó. Khi các luồng như system gọi hàm stop
    -> Luồng này vẫn chạy while -> gây lỗi hệ thống
    Biến volatile sẽ tạo một ràng buộc rằng biến status này phải được Đọc/Ghi ở RAM toàn sever, vì thế nó có thể cập nhập nhanh trạng thái
     */
    public AuctionSessionWatcher(){
        manager = AuctionManager.getInstance();
    }
    public void stop() {
        status = false;
    }
    @Override
    public void run() {
        while (status){
            List<AuctionSession> auctionSessionList = new ArrayList<>();

            for(AuctionSession as : manager.getSessions()){
                try{
                    manager.finishSession(as);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }finally {
                    failedSession.add(as); //thêm phiên lỗi vào danh sách lỗi
                    auctionSessionList.add(as); //Thực hiện xóa phiên sau đó
                }
            }

            manager.getSessions().removeAll(auctionSessionList); //Xóa toàn bộ phiên đã thành công
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                this.status = false;   //Tự động thoát vòng lặp
                Thread.currentThread().interrupt();  //Báo hiệu luồng sẵn sàng để tắt.
            }
        }
    }

    public List<AuctionSession> getFailedSession(){return failedSession;}
}
