package myWeb.Models;

public class Vehicle extends Item{
    private String owner;
    public Vehicle(String id,String name,Double price,String condition,String owner){
        super(id,name,condition,price);
        this.owner = owner;
    }
    public String getOwner(){
        return owner;
    }

    @Override
    public Double getprice(Double price) {
        return price;
    }

    public String toString(){
        return "Vehicles: " + "id=" + getId() + ", name= " + getName() + "Owner: " + getOwner();
    }
}
