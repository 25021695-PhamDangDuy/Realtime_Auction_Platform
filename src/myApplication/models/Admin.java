package myApplication.models;

public class Admin extends User {
    Bidder bidder = new Bidder();
    public String Hello(){
        return "Admin xin chào!";
    }
}
