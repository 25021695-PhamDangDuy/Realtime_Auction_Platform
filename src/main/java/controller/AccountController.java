package controller;

import database.*;
import function.*;

import models.Bidder;
import models.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

//Class kiểm soát đăng nhập và tài khoản người dùng
public class AccountController{
    private static AccountController instance = null;

    private BidderDAOImpl bidderDAO = new BidderDAOImpl();
    private SystemLogger log = SystemLogger.getInstance();
    private getUserDAO getUserDAO = new getUserDAO();
    private WalletManager walletManager = WalletManager.getInstance();

    //Validator
    private PasswordValidator passwordValidator;
    private UserNameValidator userNameValidator;

    private AccountController(){
        passwordValidator = new PasswordValidator();
        userNameValidator = new UserNameValidator();
    }

    public static AccountController getInstance() {
        if(instance == null){
            synchronized (AuctionController.class){
                if(instance == null ){
                    instance = new AccountController();
                    return  instance;
                }
            }
        }
        return instance;
    }

    public void Register(String name, String pw, String idPW) throws SQLException {
      if(!passwordValidator.valid(pw)){
          throw new IllegalArgumentException("Mật khẩu không đủ mạnh");
      }
      if(!userNameValidator.valid(name)){
          throw new IllegalArgumentException("Tên không đủ mạnh");
      }
      if (getUserDAO.getbyUsername(name) != null){
          throw new IllegalArgumentException("Tên đã được sử dụng");
      }
      if (!passwordValidator.checkEquals(pw,idPW)){
          throw new IllegalArgumentException("Mật khẩu không giống nhau");
      }
      Bidder newBidder = new Bidder(name,pw);

      bidderDAO.save(newBidder);
      walletManager.createWallet(newBidder.getID(),0);
      log.info("Tài khoản ID:" + newBidder.getID().toString() + " tạo thành công");

    }

    public User Login(String name, String pw) throws SQLException, IllegalArgumentException {
        User user = getUserDAO.getbyUsername(name);

        if(user == null){
            throw new IllegalArgumentException("Tài khoản này chưa tồn tại");
        }

        if(!passwordValidator.checkEquals(user.getPassword(), pw)){
            throw new IllegalArgumentException("Mật khẩu không chính xác");
        }

        return user;

    }

//    public void UpdateInfor(String name, )
}
