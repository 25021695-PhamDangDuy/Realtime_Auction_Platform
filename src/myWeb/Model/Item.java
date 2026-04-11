package myWeb.Model;

public abstract class Item {
    String id,name,condition;
    Double price;
    public Item(String id,String name,String condition,double price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.condition = condition;
    }
    public String getName(String name){
        return name;
    }
    public Double getprice(Double price){
        return price;
    }
    public String getCondition(String condition){
        return condition;
    }



}
