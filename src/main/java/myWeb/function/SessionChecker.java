package myWeb.function;

import myWeb.controller.AuctionSession;
import myWeb.models.Item;

import java.time.Duration;
import java.time.LocalDateTime;

public class SessionChecker {
    public boolean durationTime(LocalDateTime startTime, LocalDateTime endTime, int minMinutes , int maxMinutes) throws IllegalArgumentException, NullPointerException{
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
        if(duration.toMinutes() < minMinutes){
            throw new IllegalArgumentException("Duration must be longer than 30 minutes");
        }
        if(duration.toDays() > maxMinutes){
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
        if(this.isSessionTimeUp(session) && (session.getStatus() == SessionStatus.RUNNING) ){
            return true;
        }else {
            return false;
        }
    }

    public boolean isUpComing(AuctionSession session) throws NullPointerException {
        if(session == null){
            throw new NullPointerException("Session is not available");
        }
        if(!(this.isSessionTimeUp(session)) && (session.getStatus() == SessionStatus.UPCOMING) ){
            return true;
        }else {
            return false;
        }
    }

}
