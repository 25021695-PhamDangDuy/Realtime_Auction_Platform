public class Admin {
    private String adminHello(){
        return "Admin xin chào!";
    }
}
class Bidder extends Admin{
    public void bidderHello(){
        System.out.println("Bidder xin chào!");
    }
}

void main(){
    Bidder b = new Bidder();
    String a = (((Admin) b).adminHello());
    System.out.println(a);
}