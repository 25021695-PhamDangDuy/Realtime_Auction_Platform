package controller;

import database.BidderDAOImpl;
import function.*;

import models.Bidder;
import models.User;
import java.util.HashMap;
import java.util.List;

//Class kiểm soát đăng nhập và tài khoản người dùng
public class AccountController{
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

    public void Register(String name, String pw, String idPW) {
        if (this.isPWValidStrong(pw) && this.isNameValidStrong(name) && pw.equals(idPW)) {
            Bidder bidder = new Bidder(name,pw);

            bidderDAO.save(bidder);
            log.info("Tài khoản ID:" + bidder.getID().toString() + " tạo thành công");
        } else if (!pw.equals(idPW)) {
            System.out.println("MK không trùng nhau");
        }else{
            System.out.println("MK không đủ mạnh");
        }
    }

//    public void Login(String name, String pw){
//        if (userList.containsKey(name) && (userList.get(name).getPassword()).equals(pw)){
//            System.out.println("Login thành công");
//        }else{
//            System.out.println("Login không thành công");
//        }
//    }


}
