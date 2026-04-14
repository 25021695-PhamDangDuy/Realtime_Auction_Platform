package myWeb.Model;

abstract class Item {
    String id,name,condition;
    Double price;

    //Factory Method: Bắt các lớp con phải tự khai báo phương thức này -> tăng khả năng mở rộng code.
    abstract Item createdItem();


    //Contructors
    public Item(String id,String name,String condition,double price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.condition = condition;
    }
    public Item(){}

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
    //Thuộc tính
    private Integer MonthofWarranty;

    //Contructors
    public Electronics (String id,String name,Double price,String condition,Integer monthofwarranty){
        super(id,name,condition,price);
        this.MonthofWarranty = monthofwarranty;
    }
    public Electronics(){}

    public Integer getMonthofWarranty() {
        return MonthofWarranty;
    }
    public String toString(){
        return "Electronics{" + "id=" + getId() + ", name=" + getName() + ", warranty=" + getMonthofWarranty() + " months}";
    }

    @Override //Hàm Factory Method
    Item createdItem() {
        //doSomething
        return new Electronics();
    }
}
class Art extends Item {
    //Thuộc tính
    String author;
    String material;

    //Contructors
    public Art(String id,String name,Double price,String condition,String author,String material){
        super(id,name,condition,price);
        this.author = author;
        this.material = material;
    }
    public Art(){}

    public String getAuthor(){
        return author;
    }
    public String getMaterial() {
        return material;
    }
    public String toString(){
        return "Arts:" + "id=" + getId() + ", name=" + getName() + "Author:" + getAuthor() + "Material:" + getMaterial();
    }

    @Override
    Item createdItem() {
        //doSomething
        return new Art();
    }
}
class Vehicle extends Item{
    //Thuộc tính
    String owner;

    //Contructors
    public Vehicle(String id,String name,Double price,String condition,String owner){
        super(id,name,condition,price);
        this.owner = owner;
    }
    public Vehicle(){}

    public String getOwner(){
        return owner;
    }
    public String toString(){
        return "Vehicles:" + "id=" + getId() + ", name=" + getName() + "Owner:" + getOwner();
    }

    @Override
    Item createdItem() {
        return new Vehicle();
    }
}