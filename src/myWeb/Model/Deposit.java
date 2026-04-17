package myWeb.Model;

public class Deposit {

    public static void deposit(Bidder bidder){
//        System.out.println("Nhập số tiền cần nạp");
//        Scanner sc=new Scanner(System.in);
        double addMoney=Double.parseDouble(sc.nextLine());
        bidder.setBalance(addMoney);
    }

}
