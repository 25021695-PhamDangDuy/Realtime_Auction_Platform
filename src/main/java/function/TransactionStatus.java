package function;

/*
Khác với TransactionType(Biến dạng Giao dịch thực hiện) thì TransactionStatus sẽ lưu dữ liệu giao dịch để biết xem giao dịch đã thành công hay không
 */
public enum TransactionStatus {
    SUCCESS("Giao dịch thành công"),
    PENDING("Giao dịch đang chờ"),
    FAILED("Giao dịch hoàn tất");

    private String description;
    private TransactionStatus(String description){this.description = description;}

    public String getDescription() {
        return description;
    }
}
