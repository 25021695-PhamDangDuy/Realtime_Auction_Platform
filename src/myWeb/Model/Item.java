package myWeb.Model;

abstract class Item {
    String id,name,condition;
    Double price;
    public Item(String id,String name,String condition,double price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.condition = condition;
    }
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Double getprice(Double price){
        return price;
    }
    public String getCondition(String condition){
        return condition;
    }
}
class Electronics extends Item{
    private Integer MonthofWarranty;
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
class Art extends Item {
    String author;
    String material;
    public Art(String id,String name,Double price,String condition,String author,String material){
        super(id,name,condition,price);
        this.author = author;
        this.material = material;
    }
    public String getAuthor(){
        return author;
    }
    public String getMaterial() {
        return material;
    }
    public String toString(){
        return "Arts:" + "id=" + getId() + ", name=" + getName() + "Author:" + getAuthor() + "Material:" + getMaterial();
    }
}
class Vehicle extends Item{
    String owner;
    public Vehicle(String id,String name,Double price,String condition,String owner){
        super(id,name,condition,price);
        this.owner = owner;
    }
    public String getOwner(){
        return owner;
    }
    public String toString(){
        return "Vehicles:" + "id=" + getId() + ", name=" + getName() + "Owner:" + getOwner();
    }
}