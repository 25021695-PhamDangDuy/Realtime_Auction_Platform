package myWeb.function;

import myWeb.exception.InsufficientBalanceException;
import myWeb.exception.InvalidBidException;
import myWeb.models.User;

public class BiddingManager {
    private final SystemLogger logger = SystemLogger.getInstance();
    public void ProcessBid(String userId,double bidAmount,double currentPrice,double userBalance){
        try{
            if (userBalance < bidAmount){
                throw new InsufficientBalanceException("Tài khoản không đủ số dư");
            }
            if (bidAmount < currentPrice){
                throw new InvalidBidException("Giá đặt mua phải lớn hơn giá hiện tại!");
            }
            logger.info("User " + userId + "đã đặt giá thành công" + bidAmount);
        } catch (InsufficientBalanceException e){
            logger.error("Lỗi tài chính");
        } catch (InvalidBidException e){
            logger.error("Vi phạm quy chế đấu giá!");
        }
    }
}
