package myWeb.models;

import myWeb.function.BidStatus;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/*
Lớp quản lí BidTicket bằng việc lưu trữ thông qua ConcurrentDeque,Why?
ConcurrentDeque sỡ hữu cơ chế thread-safety cực tốt cho việc đọc / ghi liên tục thông qua thuật toán Compare and Swap(CAS)
Thông qua cơ chế LIFO(Last in First out) ta có thể truy xuất BidTicket tiếp theo gần nhất
Vì là Deque nên bản thân nó là một Queue hai đầu, nó có thể removeLast để xoóa phần tử ở cuối.
Điều này cực tiện cho việc truy xuất và tối ưu bộ nhớ khi một phiên có quá nhiều lượt đặt BidTicket
 */
public class BidHistory {
    private ConcurrentLinkedDeque<BidTicket> history;
    private AuctionSession session;
    private AtomicInteger size;  //An toàn đa luồng trong việc cộng số lượng

    public BidHistory(AuctionSession session){
        this.session = session;
        this.history = new ConcurrentLinkedDeque<>();
        size = new AtomicInteger(0);
    }

    /*
    Đẩy BidTicket vào lịch sử
     */
    private Object pushKey = new Object();
    public void pushTicket(BidTicket bidTicket){
        synchronized (pushKey){
            if(bidTicket == null){
                throw new NullPointerException("Null tham so");
            }
            if(history.contains(bidTicket)){
                throw new IllegalArgumentException("BidTicket has");
            }
            if(!bidTicket.getSession().equals(session)){
                throw new IllegalArgumentException("This ticket is not here");
            }

            history.push(bidTicket);
            size.getAndIncrement();
        }
    }

    /*
    -------------------------------
    Tập các method mà Logic chủ yếu là return và không xóa các phần tử
    -------------------------------
     */
    public BidTicket topAcctually(){
        /*
        Trả về BidTicket cuối
         */
        return history.peekFirst();
    }

    public BidTicket topLegal(){
        /*
        Trả về BidTicket cuối cùng mà BidTicket đó là hợp lệ giao dịch - Tức là status : FINISHED
        nếu duyệt không có -> trả về null
         */
        if(history.isEmpty()){
            return null;
        }
        for(BidTicket bidTicket: history){
            if(bidTicket.getStatus() == BidStatus.VALID){
                return bidTicket;
            }
        }
        return null;
    }

    public BidTicket topSecondAcctually(){
        Iterator<BidTicket> iterator = history.iterator();
        int i = 0;
        BidTicket result = null;
        while(iterator.hasNext() && i < 2){
            result = iterator.next();
            i++;
        }
        if(i == 2){
            return result;
        }else {
            return null;
        }
    }

    public BidTicket topSecondLegal(){
        Iterator<BidTicket> iterator = history.iterator();
        int n = 2;
        while(iterator.hasNext()){
                BidTicket now = iterator.next();
                if(now.getStatus() == BidStatus.VALID){
                    n -= 1;
                    if(n == 0){
                        return now;
                    }
                }
        }
        return null;
    }

    /*
    Về cơ bản, class này sẽ không cho phép việc thay đổi Hitory, hay nói cách khác là Lịch sử thì vĩnh viễn không đổi
    class này sẽ kết nối với database để phục vụ cho việc lưu trữ history lớn, và class này đóng vai trò là bộ nhớ đệm để truy xuất nhanh
    Những method dưới đây sẽ tập trung vào xóa và chuyển dữ liệu sang db
     */
    public void removeLast() throws IllegalArgumentException{
        if(history.isEmpty()){
            throw new IllegalArgumentException("History is empty");
        }
        history.removeLast();
        //Chuyen sang DB
    }
    public void removeLast(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("amount cannot lower than 1");
        }
        Iterator<BidTicket> iterator = history.iterator();
        int i = 1;
        while(iterator.hasNext() && i <= amount){
            history.removeLast();
            i++;
        }
    }

    /*
    Dưới đây là những method giúp tính toán kích cỡ size history hiện tại để dễ dàng quản lí
     */
    public int size(){
        return size.get();
    }

    /*

     */
}
