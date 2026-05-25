package myWeb.function;

public enum BidStatus {
    VALID("Hợp lệ"),
    INVALID("Bị hủy"),
    WARNING("Tình trạng cảnh báo"),
    BAN("Cấm");
    private final String description;
    private BidStatus(String Description){
        description = Description;
    }

    public String getDescription() {
        return description;
    }

}
