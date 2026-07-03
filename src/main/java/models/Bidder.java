package models;


import service.AuctionObserver;
import function.ItemStatus;
import server.Role;

import java.util.List;
import java.util.UUID;

public class Bidder extends User implements AuctionObserver {
    private transient Wallet wallet;
    private transient List<Item> itemList;


    //Lock Objects
    /**
     Thiết lập khóa bảo vệ: Object Lock
     Việc sử dụng Sychronized(this) cho đối tượng đồng nghĩa với việc
     Mỗi khi gọi dòng code có chứa đoạn trên -> khóa toàn bộ đối tượng
     Vì vậy ta hướng tới giải pháp chia nhỏ khóa bằng Object Lock
     Mỗi method sẽ có một khóa riêng
     */
    private final transient Object itemListKeyLock = new Object();

    //Constructor
    public Bidder(String name,String password){
        super(name,password);

    }
    public Bidder(UUID id, String name, String pw){
        super(id,name,pw);
    }

    //Getter
    public long getBalance() {
        return wallet.getBalance();
    }
    public double getLockBalance(){
        return  wallet.getBalanceLocked();
    }
    public UUID getWalletID(){
        return wallet.getID();
    }
    @Override
    public String getName() {
        return super.getName();
    }
    public Role getRole(){return Role.BIDDER;}


    public void update(String message){
        System.out.println("Bidder " + getName() + " : " + message);

    }
    //Setter
    public void addWallet(Wallet wallet){this.wallet = wallet;}

    public synchronized void addItem(Item item) throws NullPointerException,IllegalArgumentException{
        //Kiểm tra xem item có null k
        if(item == null){
            throw new NullPointerException("item is not available");
        }
        //Kiểm tra xem item có đang trong phiên đấu giá nào không
        if(item.getItemStatus().equals(ItemStatus.AUCTIONING)){
            throw new IllegalArgumentException("item is auctioning!");
        }
        //Kiểm tra xem item này đã có trong giỏ hàng chưa.
        if(itemList.contains(item)){
            throw new IllegalArgumentException("item has owned");
        }

        synchronized (itemListKeyLock){
            this.itemList.add(item);
        }

    }
    public void removeItem(Item item){
        //Kiểm tra xem item có null k
        if(item == null){
            throw new NullPointerException("item is not available");
        }
        //Kiểm tra xem item có đang trong phiên đấu giá nào không
        if(item.getItemStatus().equals(ItemStatus.AUCTIONING)){
            throw new IllegalArgumentException("item is auctioning!");
        }
        //Kiểm tra xem item này đã có trong giỏ hàng chưa.
        if(!itemList.contains(item)){
            throw new IllegalArgumentException("item has not owned");
        }
        synchronized (itemListKeyLock){
            this.itemList.remove(item);
        }
    }
}

