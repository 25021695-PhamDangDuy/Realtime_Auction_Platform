package myWeb.models;

public class Electronics extends Item{
    private Integer MonthofWarranty;//số tháng bảo hành.
    public Electronics (User user, String id,String name,Double price,String condition,Integer monthofwarranty){
        super(user,id,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Integer getMonthofWarranty() {
            return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getId() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }
}
