package myWeb.models;


import java.util.UUID;

/*
Đây sẽ là lớp để lưu trữ chính số dư của một người
Để quản lí tài chính các phiên đấu giá, ta sẽ đưa ra 2 loại số dư tồn tại
1. Số dư tự do: là số dư thực mà người dùng đã nạp vào VÀ phải chưa được sử dụng để tham gia bất kì giao dịch nào
2. Số dư bị khóa: là số dư mà người dùng đã dùng để thực thi giao dịch khác nhau
3. Tổng số dư: tổng số dư thực và số dư bị khóa -> kiểm tra nguồn tiền

Để phục vụ sau này phát triển đấu giá nhiều phiên cùng lúc, ta cần thiết lập bảo vệ đa luồng
 */
public class Wallet {
    private UUID ID;
    private UUID ownerID;
    private long balance;
    private long balanceLocked;
    //Set key
    private Object withdrawKey = new Object();
    private Object depositKey = new Object();
    private Object lockMoneyKey = new Object();
    private Object unlockMoneyKey = new Object();

    public Wallet(UUID user,long amount){
        this.ID = UUID.randomUUID();
        this.ownerID = user;
        this.balance = amount;
        this.balanceLocked = 0;
    }
    public Wallet(UUID user,long amount,long unlockMoney){
        this.ID = UUID.randomUUID();
        this.ownerID = user;
        this.balance = amount;
        this.balanceLocked = unlockMoney;
    }

    public UUID getID(){return ID;}
    public long getBalance(){return balance;}
    public long getBalanceLocked(){return balanceLocked;}
    public UUID getOwnerID(){return ownerID;}

    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount > balance) {
            throw new IllegalArgumentException("Cannot withdraw bigger now Balance");
        }
        synchronized (withdrawKey) {
            balance -= amount;
        }
    }
    public void deposit(double amount) throws IllegalArgumentException{
        if (amount < 10000) {
            throw new IllegalArgumentException("too little");
        }
        synchronized (depositKey) {
            balance += amount;
        }
    }

    public void lockMoney(double amount) throws IllegalArgumentException{
        if (amount > balance) {
            throw new IllegalArgumentException("Cannot lock with larger money");
        }
        synchronized (lockMoneyKey) {
            balance -= amount;
            balanceLocked += amount;
        }
    }

    public void unlockMoney(double amount) throws IllegalArgumentException{
        if(amount > balanceLocked){
            throw new IllegalArgumentException("balanceLocked has not enough");
        }
        synchronized (unlockMoneyKey){
            balance += amount;
            balanceLocked -= amount;
        }
    }

}
