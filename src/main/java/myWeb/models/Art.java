package myWeb.models;

public class Art extends Item{
    String author;
    String material;

    public Art(User user, String name, long price, String condition, String author, String material){
        super(user,name,condition,price);
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
        return "Arts:" + "id=" + getID() + ", name=" + getName() + "Author:" + getAuthor() + "Material:" + getMaterial();

    }
}
