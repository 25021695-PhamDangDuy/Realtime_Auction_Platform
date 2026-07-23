package service.brain;

import database.WalletDAO;
import function.SystemLogger;
import models.DepositTransaction;
import models.Transaction;
import models.Wallet;
import database.getUserDAO;
import models.WithdrawTransaction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WalletManager {
    private final WalletDAO walletDAO;
    private static WalletManager instance;  //Singleton
    private final SystemLogger log = SystemLogger.getInstance();
    private final getUserDAO getUserDAO = new getUserDAO();

    private PaymentManager paymentManager = PaymentManager.getInstance();

    private WalletManager(){
        walletDAO = new WalletDAO();
    }

    public static WalletManager getInstance(){
        if(instance == null){
            synchronized (WalletManager.class){
                instance = new WalletManager();
            }
        }
        return instance;
    }

    public Wallet getWallet(UUID ID) throws IllegalArgumentException, NullPointerException, SQLException{
        if(ID == null){
            throw new NullPointerException("ID is null");
        }
        Wallet rs = null;
        try{
            rs = walletDAO.get(ID);
        } catch (SQLException e) {
            log.crash("Lỗi SQL khi thực thi lấy thông tin ví theo : " + ID, e);
            throw new SQLException(e);
        }
        if(rs == null){
            log.warning("ID ví: " + ID + " ví chưa tồn tại");
            throw new IllegalArgumentException("ví chưa tồn tại: " + ID);
        }
        return rs;
    }
    public Wallet getWalletbyOwner(String name) throws SQLException, NullPointerException,IllegalArgumentException{
        if ( name == null ) {
            throw new NullPointerException("owner là null");
        }
        Wallet rs = null;
        try{

            UUID ID = getUserDAO.getbyUsername(name).getID();
            rs = walletDAO.getByOwnerID(ID);
        } catch (SQLException e) {
            log.crash("Lỗi SQL khi thực thi lấy thông tin ví theo ID user: " + name, e);
            throw new SQLException(e);
        }
        if(rs == null){
            log.warning("ID: " + name + " ví chưa tồn tại");
            throw new IllegalArgumentException("ví chưa tồn tại: " + name);
        }
        return rs;
    }
    public Wallet getWalletbyOwner(UUID userID) throws SQLException, NullPointerException,IllegalArgumentException{
        if ( userID == null ) {
            throw new NullPointerException("owner là null");
        }
        Wallet rs = null;
        try{
            rs = walletDAO.getByOwnerID(userID);
        } catch (SQLException e) {
            log.crash("Lỗi SQL khi thực thi lấy thông tin ví theo ID user: " + userID, e);
            throw new SQLException(e);
        }
        if(rs == null){
            log.warning("ID: " + userID + " ví chưa tồn tại");
            throw new IllegalArgumentException("ví chưa tồn tại: " + userID);
        }
        return rs;
    }


    public long getBalancebyOwnerID(String name) throws SQLException,NullPointerException,IllegalArgumentException {
        Wallet wallet = getWalletbyOwner(name);
        return wallet.getBalance();
    }
    public long getBalanceLockedbyOwnerID(String name) throws SQLException,NullPointerException,IllegalArgumentException {
        Wallet wallet = getWalletbyOwner(name);
        return wallet.getBalanceLocked();
    }

    public void createWallet(UUID ownerID, long amount) throws IllegalArgumentException, SQLException{
        UUID walletID = UUID.randomUUID();
        //Logic ownerID
        if(walletDAO.isHasOwnerID(ownerID)){
            throw new IllegalArgumentException("userID: " + ownerID.toString() + " đã có tồn tại ví tiền");
        }
        //Logic amount
        if(amount < 0){
            throw new IllegalArgumentException("Không thể tạo số tiền nhỏ hơn 0 đồng");
        }
        Wallet newWallet = new Wallet(walletID,ownerID,amount,0);
        walletDAO.save(newWallet);
        log.info("Tạo ví mới ID:" + walletID + "|SUCCESS");
    }

    public void withdrawWallet(UUID walletID,UUID ownerID,long amount) throws IllegalArgumentException, NullPointerException, SQLException{
        Wallet wallet = getWalletHelper(walletID, ownerID);
        wallet.withdraw(amount);
        walletDAO.update(wallet);

        HashMap<String,Object> map = new HashMap<>(Map.<String, Object>of(
                "amount", amount,
                "senderWalletID",walletID,
                "senderID",ownerID
        ));
        Transaction transaction = paymentManager.createTransaction(WithdrawTransaction.class,map);
        paymentManager.executeTransaction(transaction);
        log.info("Rút tiền ví:" + walletID + "|SUCCESS");
    }


    public void depositWallet(UUID walletID,UUID ownerID,long amount) throws  IllegalArgumentException,NullPointerException,SQLException{
        Wallet wallet = getWalletHelper(walletID,ownerID);
        wallet.deposit(amount);
        walletDAO.update(wallet);

        HashMap<String,Object> map = new HashMap<>(Map.<String, Object>of(
                "amount", amount,
                "senderWalletID",walletID,
                "senderID",ownerID
        ));
        Transaction transaction = paymentManager.createTransaction(DepositTransaction.class,map);
        paymentManager.executeTransaction(transaction);
        log.info("Nạp tiền ví:" + walletID + "|SUCCESS");
    }

    public void transferMoney(UUID walletSender, UUID walletReveicer, UUID SenderID, UUID ReveicerID, long amount) throws SQLException, NullPointerException,IllegalArgumentException{

        Wallet wallet1 = getWalletHelper(walletSender,SenderID);
        Wallet wallet2 = getWalletHelper(walletReveicer,ReveicerID);

        wallet1.withdraw(amount);
        wallet2.deposit(amount);

        walletDAO.update(wallet1);
        walletDAO.update(wallet2);
        log.info("Chuyển tiền từ ví ID: " + walletSender + " sang ID: " + walletReveicer + " số tiền: " + amount + "|SUCCESS" );

    }

    public void lockMoney(UUID walletID,UUID ownerID, long amount) throws IllegalArgumentException, NullPointerException, SQLException{
        Wallet wallet = getWalletHelper(walletID,ownerID);
        wallet.lockMoney(amount);
        walletDAO.update(wallet);
    }

    public void unlockMoney(UUID walletID,UUID ownerID, long amount) throws IllegalArgumentException, NullPointerException, SQLException{
        Wallet wallet = getWalletHelper(walletID,ownerID);
        wallet.unlockMoney(amount);
        walletDAO.update(wallet);
    }

    private Wallet getWalletHelper(UUID walletID, UUID ownerID) throws SQLException,NullPointerException,IllegalArgumentException {
        if(ownerID == null){
            throw new NullPointerException("ownerID là null");
        }
        Wallet wallet = this.getWallet(walletID);
        //Kiểm tra xem có phải ví của owner k
        if(!wallet.getOwnerID().equals(ownerID)){
            throw new IllegalArgumentException("ownerID: " + ownerID.toString() + " không phải chủ của ví ID: " + walletID.toString());
        }
        return wallet;
    }

}
