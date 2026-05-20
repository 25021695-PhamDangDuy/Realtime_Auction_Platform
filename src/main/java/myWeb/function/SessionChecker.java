package myWeb.function;

import myWeb.controller.AuctionSession;
import myWeb.models.Item;

import java.time.Duration;
import java.time.LocalDateTime;

public class SessionChecker {
    public boolean durationTime(LocalDateTime startTime, LocalDateTime endTime) throws IllegalArgumentException, NullPointerException{
        if(startTime == null || endTime == null){
            throw new NullPointerException("Tham số thiếu");
        }
        //Config: Giới hạn một phiên sẽ gồm tối thiểu 30 phút, và tối đa 30 ngày
        Duration duration = Duration.between(startTime,endTime);
        if(startTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("StartTime is before than Now");
        }
        if(startTime.isAfter(endTime) || startTime.isEqual(endTime)){
            throw new IllegalArgumentException("StartTime is after than EndTime");
        }
        if(duration.toMinutes() < 30){
            throw new IllegalArgumentException("Duration must be longer than 30 minutes");
        }
        if(duration.toDays() > 30){
            throw new IllegalArgumentException("Duration must be shorter than 30 days ");
        }
        return true;

    }

    public boolean isItemAvailable(Item item) throws NullPointerException,IllegalArgumentException {
        if(item == null){
            throw new NullPointerException("Item k tồn tại");
        }
        if(item.getItemStatus() !=(ItemStatus.AVAILABLE)){
            throw new IllegalArgumentException("Item đã được sử dụng");
        }
        return true;
    }

    public void isSessionTimeUp(AuctionSession session){
        if(session.getEndTime().isAfter(LocalDateTime.now()) || session.getEndTime().equals(LocalDateTime.now())){
            System.out.println("Phiên đã kết thúc, Người chiến thẳng là: " + session.getTopBidder());
            session.setStatus(SessionStatus.FINISHED);
            session.getItem().setItemStatus(ItemStatus.SOLD);
            //Chuyển Item sang owner
            //Notify cho các bên
        }
    }
}
