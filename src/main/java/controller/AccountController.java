package controller;

import database.BidderDAOImpl;
import function.*;

import models.Bidder;
import models.User;
import models.Wallet;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//Class kiểm soát đăng nhập và tài khoản người dùng
public class AccountController{
    private static AccountController instance=null;
    //Map người dùng được lưu dưới dạng User - username(String)
    private BidderDAOImpl bidderDAO = new BidderDAOImpl();
    private SystemLogger log = SystemLogger.getInstance();
    //Setting bộ quy tắc cho việc setName
    List<StrongRule> ruleName;
    //Setting bộ quy tắc cho PW
    List<StrongRule> rulePW;

    public AccountController(){

        //Tùy chỉnh cho quy tắc mật khẩu
        rulePW.add(new LengthRule(6));
        rulePW.add(new DiversityRule());
        //Tùy chỉnh cho quy tắc đặt tên
        ruleName.add(new LengthRule(3));
//        ruleName.add(new ExistRule(userList.keySet()));

        
    }

    //Kiểm tra mật khẩu:
    public boolean checkPassword(User b, String pw){
        return b.getPassword().equals(pw);
    }

    //Kiểm tra mật khẩu có đủ mạnh không
    public boolean isPWValidStrong(String pw) {
        for(StrongRule pr : rulePW){
            if(!pr.validate(pw)){
                return false;
            }
        }
        return true;
    }
    //Kiểm tra tên đặt có chuẩn không
    public boolean isNameValidStrong(String pw) {
        for(StrongRule pr : ruleName){
            if(!pr.validate(pw)){
                return false;
            }
        }
        return true;
    }

    public void Register(String name, String pw, String idPW) throws SQLException {
        if (this.isPWValidStrong(pw) && this.isNameValidStrong(name) && pw.equals(idPW)) {
            Bidder bidder = new Bidder(name,pw);

            bidderDAO.save(bidder);
            WalletManager.getInstance().createWallet(bidder.getID(),0);
            log.info("Tài khoản ID:" + bidder.getID().toString() + " tạo thành công");

        } else if (!pw.equals(idPW)) {
            System.out.println("MK không trùng nhau");
        }else{
            System.out.println("MK không đủ mạnh");
        }
    }
    public static AccountController getInstance(){
        if (instance==null){
            synchronized (AccountController.class){
                if(instance == null){
                    instance = new AccountController();
                    return instance;
                }
            }
        }
        return instance;
    }
    //Goij ham tam de khong loi
    public User Login(String name, String pw){
        return new Bidder("Tesst","123");
    }
    /*{
        if (userList.containsKey(name) && (userList.get(name).getPassword()).equals(pw)){
           System.out.println("Login thành công");
       }else{
           System.out.println("Login không thành công");
       }
    }
*/

}
