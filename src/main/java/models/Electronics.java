package models;

import java.util.UUID;

public class Electronics extends Item{
    private Integer MonthofWarranty;//số tháng bảo hành.
    public Electronics (User owner, String name, long price, String condition, Integer monthofwarranty){
        super(owner,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Integer getMonthofWarranty() {
            return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getID() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }
}
