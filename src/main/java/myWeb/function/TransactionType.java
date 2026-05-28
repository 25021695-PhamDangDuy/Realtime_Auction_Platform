package myWeb.function;

public enum TransactionType {
    AUCTION_SETTLEMENT("Quyết toán phiên đấu giá thắng"),
    TRANSFER_MONEY("Giao dịch chuyển tiền"),
    WITHDRAW_WALLET("Rút tiền"),
    DEPOSIT_WALLET("Nạp tiền");

    private String description;
    private TransactionType(String description){this.description = description;}

    public String getDescription() {
        return description;
    }
}
