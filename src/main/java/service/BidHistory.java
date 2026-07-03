package service;

import database.BidTicketDAO;
import database.SessionDAO;
import models.BidTicket;

import java.util.List;
import java.util.UUID;

/*
Lớp quản lí BidTicket bằng việc lưu trữ thông qua ConcurrentDeque,Why?
ConcurrentDeque sỡ hữu cơ chế thread-safety cực tốt cho việc đọc / ghi liên tục thông qua thuật toán Compare and Swap(CAS)
Thông qua cơ chế LIFO(Last in First out) ta có thể truy xuất BidTicket tiếp theo gần nhất
Vì là Deque nên bản thân nó là một Queue hai đầu, nó có thể removeLast để xoóa phần tử ở cuối.
Điều này cực tiện cho việc truy xuất và tối ưu bộ nhớ khi một phiên có quá nhiều lượt đặt BidTicket
 */
public class BidHistory {
    private static BidTicketDAO bidTicketDAO = new BidTicketDAO();
    private static SessionDAO sessionDAO = new SessionDAO();
    public BidHistory(){
    }

    /*
    -------------------------------
    Tập các method mà Logic chủ yếu là return và không xóa các phần tử
    -------------------------------
     */
    //==========================[Getter]============================//

    //Lấy 1 topBid hợp lệ
    public static BidTicket getTopAcctuallyBySessionID(UUID sessionID){
        return bidTicketDAO.getTopBySession(sessionID);
    }
    //Lấy 1 topBid : tức là có thể topBid của spam
    public static BidTicket getTopBySessionID(UUID sessionID){
        return bidTicketDAO.getTopBySession(sessionID);
    }
    //Lấy danh sách bid hợp lệ: 0 -> n với 0 là bid mới nhất
    public static List<BidTicket> getLegalBySessionID(UUID sessionID){
        return bidTicketDAO.getLegalBySession(sessionID);
    }
    //Lấy danh sách bid
    public static List<BidTicket> getBySessionID(UUID sessionID){
        return bidTicketDAO.getBySession(sessionID);
    }
    public static BidTicket getSecondBySessionID(UUID sessionID){
        List<BidTicket> list = bidTicketDAO.getBySession(sessionID);
        if(list.size() < 2){
            return null;
        }
        return list.get(1);
    }

    /*
    Về cơ bản, class này sẽ không cho phép việc thay đổi Hitory, hay nói cách khác là Lịch sử thì vĩnh viễn không đổi
    class này sẽ kết nối với database để phục vụ cho việc lưu trữ history lớn, và class này đóng vai trò là bộ nhớ đệm để truy xuất nhanh
    Những method dưới đây sẽ tập trung vào xóa và chuyển dữ liệu sang db
     */
//    public void removeLast() throws IllegalArgumentException{
//        if(history.isEmpty()){
//            throw new IllegalArgumentException("History is empty");
//        }
//        history.removeLast();
//        //Chuyen sang DB
//    }
//    public void removeLast(int amount){
//        if(amount <= 0){
//            throw new IllegalArgumentException("amount cannot lower than 1");
//        }
//        Iterator<BidTicket> iterator = history.iterator();
//        int i = 1;
//        while(iterator.hasNext() && i <= amount){
//            history.removeLast();
//            i++;
//        }
//    }

    /*
    Dưới đây là những method giúp tính toán kích cỡ size history hiện tại để dễ dàng quản lí
     */
}
