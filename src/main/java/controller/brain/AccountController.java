package controller.brain;

import database.*;
import database.items.ArtDAO;
import database.items.ElectronicDAO;
import database.items.VehicleDAO;
import function.*;

import models.*;
import server.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//Class kiểm soát đăng nhập và tài khoản người dùng
public class AccountController{
    private static AccountController instance = null;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ElectronicDAO electronicDAO = new ElectronicDAO();
    private ArtDAO artDAO = new ArtDAO();
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
            synchronized (AccountController.class){
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
      if (getUserDAO.isUsername(name)){
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

        Wallet wallet = walletManager.getWalletbyOwner(name);
        user.setWallet(wallet);
        return user;

    }

    /*
    Tập methods lấy thông tin từ db của User sang server
     */
    public User getInfor(String name) throws SQLException {
        User user = getUserDAO.getbyUsername(name);
        return user;
    }

    public void updateUserLegal(User userNew) throws SQLException {
        User u = getUserDAO.getbyUsername(userNew.getName());
        //Kiểm tra mật khẩu mới:
        if(!passwordValidator.valid(userNew.getPassword())){
            throw new IllegalArgumentException("Mật khẩu không đủ mạnh");
        }
        //Kiểm tra tên mới
        if(getUserDAO.isUsername(userNew.getName())){
            throw new IllegalArgumentException("Tên đã tồn tại");
        }
        if(!userNameValidator.valid(userNew.getName())){
            throw new IllegalArgumentException("Tên mới không đủ mạnh");
        }
        //Kiểm tra role
        if(userNew.getRole().name().equals(u.getRole().name())){
            getUserDAO.update(userNew);
        }else if(userNew.getRole() == Role.SELLER){
            getUserDAO.update(userNew);
        }else {
            throw new IllegalArgumentException("Không thể ép Seller lên Bidder");
        }

    }

    public void upRole(User userNew){
        Seller seller = new Seller(userNew.getID(),userNew.getName(),userNew.getPassword());
        getUserDAO.update(seller);
    public List<UUID> getSessionsByUserID(UUID ID) throws SQLException {
        ObserverDAO observerDAO = new ObserverDAO();
        return  observerDAO.getSessionsByObserverID(ID);
    }




}
