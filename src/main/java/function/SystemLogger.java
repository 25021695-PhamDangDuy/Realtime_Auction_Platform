package function;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemLogger {
    //Định nghĩa cấp độ Log bằng enum
    public enum LogLevel {
        INFO(1),
        BUG(2),
        CRASH(3),
        WARNING(4);
        private final int priority;

        LogLevel(int priority) {
            this.priority = priority;
        }

        int getPriority() {
            return priority;
        }
    }
    public void setMininumLevel(LogLevel level){
        this.minimumLevel = level;
    }

    private static volatile SystemLogger instance;
    private LogLevel minimumLevel = LogLevel.INFO;
    private SystemLogger(){}
    public static SystemLogger getInstance(){
        if (instance == null){
            synchronized (SystemLogger.class){
                if (instance == null){
                    instance = new SystemLogger();

                }
            }
        }
        return instance;
    }
    public void setMinimumLevel(LogLevel level){
        this.minimumLevel = level;
    }
    // Hàm logic ghi log cốt lõi (được đồng bộ hóa đa luồng)
    private synchronized void writeLog(LogLevel level, String message, Throwable throwable) {
        // Kiểm tra xem log này có đủ độ nghiêm trọng để in ra không

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String threadName = Thread.currentThread().getName();

        String logMessage = String.format("[%s] [%s] [%s]: %s", timeStamp, level, threadName, message);
        System.out.println(logMessage);

        if (throwable != null) {
            throwable.printStackTrace(System.out);
        }
    }
    public void info(String message) {
        writeLog(LogLevel.INFO, message, null);
    }

    public void warning(String message) {
        writeLog(LogLevel.WARNING, message, null);
    }

    public void error(String message) {
        writeLog(LogLevel.BUG, message, null);
    }

    public void crash(String message, Throwable throwable) {
        writeLog(LogLevel.CRASH, message, throwable);
    }
}
