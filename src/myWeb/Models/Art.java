package myWeb.Models;

public class Art extends Item{
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
