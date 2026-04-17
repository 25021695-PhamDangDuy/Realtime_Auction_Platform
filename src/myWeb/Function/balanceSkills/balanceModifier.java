package myWeb.Function.balanceSkills;

public class balanceModifier {
    public static void deposit(balanceModifyAble bma, double amount){
        bma.setBalance(amount);
    }

    public static void withdraw(balanceModifyAble bma, double amount){
        if(bma.getBalance() >= amount){
            bma.setBalance(-amount);
        }else {
            System.out.println("ERROR: Số dư không đủ");
        }
    }
}
