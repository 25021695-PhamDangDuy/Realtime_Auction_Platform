package models;

import function.ItemStatus;

import java.util.UUID;

public class Vehicle extends Item{
    public Vehicle(User user,String name,long price,String condition){
        super(user,name,condition,price);
    }
    public Vehicle(UUID ID, User user, String name, long price, String condition, ItemStatus status){
        super(ID,user,name,condition,price, status);
    }
    public String toString(){
        return "Vehicles: " + "id=" + getID() + ", name= " + getName() + "Owner: " + this.getOwner();
    }
}
