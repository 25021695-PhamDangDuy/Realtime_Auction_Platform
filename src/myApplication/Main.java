import myApplication.models.Admin;
import myApplication.models.Bidder;
import myApplication.models.Seller;
import myApplication.models.User;

void main(){
    HashMap<String, User> tasks = new HashMap<>();

    Admin ad1 = new Admin();
    Admin ad2 = new Admin();
    Seller sl1 = new Seller();
    Seller sl2 = new Seller();
    Bidder bd1 = new Bidder();
    Bidder bd2 = new Bidder();

    List<User> inv = new ArrayList<>();
    inv.add(ad1);
    inv.add(ad2);
    inv.add(bd1);
    inv.add(bd2);
    inv.add(sl2);
    inv.add(sl1);

    for(User u : inv){
        if (u instanceof Bidder){
            System.out.println(u.Hello());
        }
    }
}