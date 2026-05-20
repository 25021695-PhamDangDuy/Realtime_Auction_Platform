package myWeb.models;

public class Vehicle extends Item{
    private Bidder owner;
    public Vehicle(String id,String name,Double price,String condition,Bidder owner){
        super(id,name,condition,price);
        this.owner = owner;
    }
    public Bidder getOwner(){
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
