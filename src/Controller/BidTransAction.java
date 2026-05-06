package Controller;

public class BidTransAction {
    private double currentPrice;
    private double userBalance;
    public BidTransAction(double currentPrice,double useBalance){
        this.currentPrice = currentPrice;
        this.userBalance = useBalance;
    }
    public void executeBid(double bidAmount){
        checkBalance(bidAmount);
        compareBid(bidAmount);
        this.currentPrice = bidAmount;
        this.userBalance -= bidAmount;
        System.out.println("Đặt giá thành công!Giá mới: " + currentPrice);
    }
    private void checkBalance(double bidAmount) {
        if (bidAmount > userBalance) {
            throw new AuthenticationException("Số dư không đủ để thực hiện giao dịch này.");
        }
    }
    private void compareBid(double bidAmount) {
        if (bidAmount <= currentPrice) {
            throw new InvalidBidException("Giá thầu mới phải lớn hơn giá hiện tại (" + currentPrice + ").");
        }
    }
    public double getCurrentPrice() { return currentPrice; }
    public double getUserBalance() { return userBalance; }

}
