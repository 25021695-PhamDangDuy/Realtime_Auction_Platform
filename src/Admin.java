public class Admin extends User {
    Bidder bidder = new Bidder();
    public String Hello(){
        return "Admin xin chào!";
    }
}
class Bidder extends User{
    public String Hello(){
        return "Bidder xin chào!";
    }
}

class Seller extends Bidder{
    public String Hello(){
        return "Seller xin chào!";
    }
}