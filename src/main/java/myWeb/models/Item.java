package myWeb.models;

import myWeb.function.ItemStatus;

public abstract class Item {
    private String id,name,condition;
    private Double price;
    private ItemStatus itemStatus;
    private User owner;

    //Lock Object
    private final Object ownerKey = new Object();

    public Item(User user,String id,String name,String condition,double price){
        this.owner = user;
        this.id = id;
        this.name = name;
        this.price = price;
        this.condition = condition;
        this.itemStatus = ItemStatus.AVAILABLE;
    }
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Double getPrice(){return price;}
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
