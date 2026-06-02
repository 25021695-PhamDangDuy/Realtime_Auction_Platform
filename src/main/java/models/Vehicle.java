package models;

public class Vehicle extends Item{
    public Vehicle(User user,String name,long price,String condition){
        super(user,name,condition,price);
    }
    public String toString(){
        return "Vehicles: " + "id=" + getID() + ", name= " + getName() + "Owner: " + this.getOwner();
    }
    @Override
    protected String getItemType() {
        return "VEHICLE"; // Báo cho Client biết đây là Xe cộ
    }

    @Override
    protected String getSpecificDetails() {
        // Nối các thông tin đặc thù của Xe
        return null;
    }
}
