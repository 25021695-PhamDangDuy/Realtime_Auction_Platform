package myWeb.models;

public class Vehicle extends Item{
    public Vehicle(User user,String id,String name,Double price,String condition){
        super(user,id,name,condition,price);
    }
    public String toString(){
        return "Vehicles: " + "id=" + getId() + ", name= " + getName() + "Owner: " + this.getOwner();
    }
}
