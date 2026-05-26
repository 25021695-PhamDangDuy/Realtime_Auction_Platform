package myWeb.function;

public class SystemLogger {
    private static volatile SystemLogger instance;
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
    public synchronized void log(String level,String msg){
        String threadName = Thread.currentThread().getName();
        String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        System.out.println(String.format("[%s] [%s] [%s]: %s",timeStamp,level,threadName,msg));
    }
}
