package myWeb.Model;

import java.util.Map;

public class ItemFactory {
    public static Item createItem(String type, String id, String name, Double price, String condition, Map<String,Object> attributes){
       if (type.equalsIgnoreCase("Electronics")){
           Integer monthofWarrantty = (Integer) attributes.get("warranty");
           return new Electronics(id,name,price,condition,monthofWarrantty);
       } else if (type.equalsIgnoreCase("Art")){
           String author = (String) attributes.get("author");
           String material = (String) attributes.get("material");
           return new Art(id,name,price,condition,author,material);
       } else if (type.equalsIgnoreCase("Vehicle")){
           String owner = (String) attributes.get("owner");
           return new Vehicle(id,name,price,condition,owner);
       }
        return null;
    }
}
