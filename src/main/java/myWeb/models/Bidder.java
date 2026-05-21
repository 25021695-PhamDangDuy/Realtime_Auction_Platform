package myWeb.models;


import myWeb.function.ItemStatus;

import java.util.ArrayList;
import java.util.List;

public class Bidder extends User {
    private double balance=0;
    private List<Item> itemList;

    //Lock Objects
    /**
     Thiết lập khóa bảo vệ: Object Lock
     Việc sử dụng Sychronized(this) cho đối tượng đồng nghĩa với việc
     Mỗi khi gọi dòng code có chứa đoạn trên -> khóa toàn bộ đối tượng
     Vì vậy ta hướng tới giải pháp chia nhỏ khóa bằng Object Lock
     Mỗi method sẽ có một khóa riêng
     */
    private final Object itemListKeyLock = new Object();

    //Constructor
    public Bidder(String id,String name,String password){
        super(id,name,password);
        this.itemList = new ArrayList<>();
    }
    //Getter
    public double getBalance() {
        return balance;
    }

    //Setter
    public void setBalance(double bal){
        if (bal < 0){
            System.out.println("số dư không hợp lệ");
        }else {
            balance = bal;
        }
    }
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

