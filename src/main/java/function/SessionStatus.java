package function;

public enum SessionStatus {
    UPCOMING("Sắp mở"),
    RUNNING("Đang diễn ra"),
    FINISHED("Đã kết thúc"),
    CANCELED("Đã bị hủy"),
    PENDING("Đang chờ");
    private final String description;

    private SessionStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
