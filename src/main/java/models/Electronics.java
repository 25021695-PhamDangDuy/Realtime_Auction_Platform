package models;

import function.ItemStatus;

import java.util.UUID;

public class Electronics extends Item{
    private Integer MonthofWarranty;//số tháng bảo hành.
    public Electronics (User owner, String name, long price, String condition, Integer monthofwarranty){
        super(owner,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Electronics (UUID ID, User owner, String name, long price, String condition, Integer monthofwarranty, ItemStatus status){
        super(ID,owner,name,condition,price, status);
        this.MonthofWarranty = monthofwarranty;
    }
    public Integer getMonthofWarranty() {
            return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getID() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }

    @Override
    protected String getItemType() {
        return "ELECTRONIC"; // Báo cho Client biết đây là đồ công nghệ
    }

    @Override
    protected String getSpecificDetails() {
        // Nối các thông tin đặc thù của Đồ điện tử
        Integer month = getMonthofWarranty();
        return String.valueOf(month);
    }
}