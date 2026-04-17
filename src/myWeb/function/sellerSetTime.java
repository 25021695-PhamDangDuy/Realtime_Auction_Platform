package myWeb.function;

import java.time.LocalDateTime;

public interface sellerSetTime {
    void setTime(LocalDateTime start, LocalDateTime end);
    void extendDuration(int minutes);
    long getRemainingTime();
}

