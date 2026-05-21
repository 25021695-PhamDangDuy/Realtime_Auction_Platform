package myWeb.models;

public class Vehicle extends Item{
    private Bidder owner;
    public Vehicle(User user,String id,String name,Double price,String condition,Bidder owner){
        super(user,id,name,condition,price);
        this.owner = owner;
    }
    public Bidder getOwner(){
        return owner;
    }

    public String toString(){
        return "Vehicles: " + "id=" + getId() + ", name= " + getName() + "Owner: " + getOwner();
    }
}
