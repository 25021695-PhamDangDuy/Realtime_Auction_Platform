package myWeb.models;

import myWeb.function.ItemStatus;

public abstract class Item {
    private String id,name,condition;
    private Double price;
    private ItemStatus itemStatus;

    public Item(String id,String name,String condition,double price){
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
    public Double getprice(){
        return price;
    }
    public String getCondition(){
        return condition;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public synchronized void setItemStatus(ItemStatus itemStatus) throws NullPointerException{
        if(itemStatus == null){
            throw new NullPointerException();
        }
            this.itemStatus = itemStatus;
    }

}
