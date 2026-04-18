package myWeb.function.balanceSkills;

public class balanceModifier {
    public static void deposit(balanceModifiable bma, double amount){
        double now = bma.getBalance();
        bma.setBalance(now + amount);
    }

    public static void withdraw(balanceModifiable bma, double amount){
        double now = bma.getBalance();
        if(now >= amount){
            bma.setBalance(now - amount);
        }else {
            System.out.println("ERROR: Số dư không đủ");
        }
    }
}
