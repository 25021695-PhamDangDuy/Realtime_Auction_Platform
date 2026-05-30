package function;

import models.AuctionSession;
import models.Item;
import java.time.Duration;
import java.time.LocalDateTime;

public class SessionChecker {
    public boolean durationTime(LocalDateTime startTime, LocalDateTime endTime, int minMinutes , int maxMinutes) throws IllegalArgumentException, NullPointerException{
        if(startTime == null || endTime == null){
            throw new NullPointerException("Tham số thiếu");
        }
        //Config: Giới hạn một phiên sẽ gồm tối thiểu minMinutes phút, và tối đa maxMinutes phút
        Duration duration = Duration.between(startTime,endTime);
        if(startTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalArgumentException("StartTime is before than Now");
        }
        if(startTime.isAfter(endTime) || startTime.isEqual(endTime)){
            throw new IllegalArgumentException("StartTime is after than EndTime");
        }
        if(duration.toMinutes() < minMinutes){
            throw new IllegalArgumentException("Duration must be longer than"+ minMinutes + "minutes");
        }
        if(duration.toDays() > maxMinutes){
            throw new IllegalArgumentException("Duration must be shorter than" + maxMinutes + "minutes");
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

    public boolean isSessionTimeUp(AuctionSession session){
        if(session == null){
            throw new NullPointerException("Session is not available");
        }
        if(session.getEndTime().isBefore(LocalDateTime.now()) || session.getEndTime().equals(LocalDateTime.now())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isAuctioning(AuctionSession session) throws NullPointerException{
        if(session == null){
            throw new NullPointerException("Session is not available");
        }
        if(!this.isSessionTimeUp(session) && (session.getStatus() == SessionStatus.RUNNING) ){
            return true;
        }else {
            return false;
        }
    }

    public boolean isUpComing(AuctionSession session) throws NullPointerException {
        if(session == null){
            throw new NullPointerException("Session is not available");
        }
        if(!(this.isAuctioning(session)) && (session.getStatus() == SessionStatus.UPCOMING) ){
            return true;
        }else {
            return false;
        }
    }

}
