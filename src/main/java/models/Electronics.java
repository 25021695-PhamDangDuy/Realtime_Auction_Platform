package models;

public class Electronics extends Item{
    private Integer MonthofWarranty;//số tháng bảo hành.
    public Electronics (User user, String name, long price, String condition, Integer monthofwarranty){
        super(user,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Integer getMonthofWarranty() {
            return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getID() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }
}
