package models;

public class Vehicle extends Item{
    public Vehicle(User user,String name,long price,String condition){
        super(user,name,condition,price);
    }
    public String toString(){
        return "Vehicles: " + "id=" + getID() + ", name= " + getName() + "Owner: " + this.getOwner();
    }
}
