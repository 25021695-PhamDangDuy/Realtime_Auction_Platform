package myWeb.controller;

import myWeb.function.*;
import myWeb.function.StrongRule;
import myWeb.models.Bidder;
import myWeb.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Class kiểm soát đăng nhập và tài khoản người dùng
public class AccountController{
    // thêm SIngleton để quản lý tập trung
    private static AccountController instance;
    public static synchronized AccountController getInstance() {
        if (instance == null) {
            instance = new AccountController();
        }
        return instance;
    }

    //Map người dùng được lưu dưới dạng User - username(String)
    private HashMap<String,User> userList;

    //Setting bộ quy tắc cho việc setName
    List<StrongRule> ruleName;
    //Setting bộ quy tắc cho PW
    List<StrongRule> rulePW;

    private AccountController(){
        userList = new HashMap<>();
        ruleName = new ArrayList<>();
        rulePW = new ArrayList<>();

        //Tùy chỉnh cho quy tắc mật khẩu
        rulePW.add(new LengthRule(6));
        rulePW.add(new DiversityRule());
        //Tùy chỉnh cho quy tắc đặt tên
        ruleName.add(new LengthRule(3));
        ruleName.add(new ExistRule(userList.keySet()));

        
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

    public void Register(String id, String name, String pw, String idPW) throws Exception{
        // Trả lỗi cụ thể để Command bắt được và gửi về cho khách hàng
        if (!pw.equals(idPW)) {
            throw new Exception("Mật khẩu xác nhận không trùng khớp!");
        }
        if (!this.isPWValidStrong(pw)) {
            throw new Exception("Mật khẩu không đủ mạnh (Cần ít nhất 6 ký tự và đa dạng)!");
        }
        if (!this.isNameValidStrong(name)) {
            throw new Exception("Tên đăng nhập không hợp lệ hoặc đã tồn tại!");
        }

        // Nếu qua hết các bài test -> Đăng ký thành công!
        userList.put(name, new Bidder(id, name, pw));
    }

    public User Login(String name, String pw) throws Exception{
        if (userList.containsKey(name) && userList.get(name).getPassword().equals(pw)) {
            return userList.get(name); // Thả đối tượng User ra cho Mạng hứng
        }

        throw new Exception("Sai tài khoản hoặc mật khẩu!");
    }


}
