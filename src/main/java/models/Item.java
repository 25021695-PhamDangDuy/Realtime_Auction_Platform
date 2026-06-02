package models;

import function.ItemStatus;

import java.util.UUID;

public abstract class Item {
    private UUID ID;
    private String name;
    private String condition;
    private long price;
    private ItemStatus itemStatus;
    private User owner;

    //Lock Object
    private final Object ownerKey = new Object();


    public Item(User user,String name,String condition,long price){
        this.owner = user;
        this.ID = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.condition = condition;
        this.itemStatus = ItemStatus.AVAILABLE;
    }public Item(UUID ID,User user,String name,String condition,long price, ItemStatus status){
        this.owner = user;
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.condition = condition;
        this.itemStatus = ItemStatus.AVAILABLE;
        this.itemStatus = status;
    }

    public UUID getID(){
        return ID;
    }
    public String getName(){
        return name;
    }
    public Long getPrice(){return price;}
    public String getCondition(){
        return condition;
    }
    public ItemStatus getItemStatus() {
        return itemStatus;
    }
    public User getOwner() {
        return owner;
    }

    public synchronized void setItemStatus(ItemStatus itemStatus) throws NullPointerException{
        if(itemStatus == null){
            throw new NullPointerException();
        }
            this.itemStatus = itemStatus;
    }

    public synchronized void setOwner(User user){
        if(user == null){
            throw new NullPointerException("User is not available");
        }
        if(this.owner == user){
            throw new IllegalArgumentException("User has owned");
        }
        synchronized (ownerKey){
            this.owner = user;
        }
    }
}
