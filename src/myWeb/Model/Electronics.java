package myWeb.Model;

public class Electronics extends Item{
    private Integer MonthofWarranty;//số tháng bảo hành.
    public Electronics (String id,String name,Double price,String condition,Integer monthofwarranty){
        super(id,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Integer getMonthofWarranty() {
            return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getId() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }
}
