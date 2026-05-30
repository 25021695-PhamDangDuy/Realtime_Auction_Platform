package function;

public enum ItemStatus {
    AVAILABLE("Đang ở kho"),  //Trạng thái mới ở kho của Seller
    AUCTIONING("Đang trong phiên đấu giá"),   //Trạng thái đang trong một phiên đấu giá
    SOLD("Đã được bán");   //Trạng thái đã bán

    private final String description;
    private ItemStatus(String des){
        this.description = des;
    }

    public String getDescription() {
        return description;
    }
}
