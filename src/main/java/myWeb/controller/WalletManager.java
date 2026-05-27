package myWeb.controller;

import myWeb.models.Wallet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WalletManager {
    private ConcurrentHashMap<String,Wallet> Wallets;
    private static WalletManager instance;  //Singleton

    private WalletManager(){
        Wallets = new ConcurrentHashMap<>();
    }

    public static WalletManager getInstance(){
        if(instance == null){
            synchronized (WalletManager.class){
                instance = new WalletManager();
            }
        }
        return instance;
    }

    private Wallet getWallet(String ID) throws IllegalArgumentException, NullPointerException{
        if(ID == null){
            throw new NullPointerException("ID is null");
        }
        if(!Wallets.contains(ID)){
            throw new IllegalArgumentException("Wallet is not found");
        }
        return Wallets.get(ID);
    }

    public void createWallet(String ID, String ownerID,double amount){
        //Logic ID

        //Logic ownerID

        //Logic amount

        Wallet newWallet = new Wallet(ID,ownerID,amount);
        Wallets.put(ID,newWallet);
    }

    public void withdrawWallet(String walletID,String ownerID,double amount) throws IllegalArgumentException{
        try{
            Wallet wallet = this.getWallet(walletID);
            //Kiểm tra xem có phải ví của owner k
            if(!wallet.getOwnerID().equals(ownerID)){
                throw new IllegalArgumentException("User is not owned this Wallet");
            }
            wallet.withdraw(amount);
        }catch (IllegalArgumentException | NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    public void depositWallet(String walletID,String ownerID,double amount) throws  IllegalArgumentException{
        try{
            Wallet wallet = this.getWallet(walletID);
            //Kiểm tra xem có phải ví của owner k
            if(!wallet.getOwnerID().equals(ownerID)){
                throw new IllegalArgumentException("User is not owned this Wallet");
            }
            wallet.deposit(amount);
        }catch (IllegalArgumentException | NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    public void transferMoney(String walletSender, String walletReveicer, String SenderID, String ReveicerID, double amount){
        try{
            Wallet wallet1 = this.getWallet(walletSender);
            Wallet wallet2 = this.getWallet(walletReveicer);

            if(!wallet1.getOwnerID().equals(SenderID) || !wallet2.getOwnerID().equals(ReveicerID)){
                throw new IllegalArgumentException("User is not owned Wallet");
            }

            wallet1.withdraw(amount);
            wallet2.deposit(amount);
        }catch (IllegalArgumentException | NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    public void lockMoney(String walletID,String ownerID, double amount) throws IllegalArgumentException{
        try{
            Wallet wallet = this.getWallet(walletID);
            if(!wallet.getOwnerID().equals(ownerID)){
                throw new IllegalArgumentException("User is not owned Wallet");
            }

            wallet.lockMoney(amount);
        }catch (IllegalArgumentException | NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    public void unlockMoney(String walletID,String ownerID, double amount) throws IllegalArgumentException{
        try{
            Wallet wallet = this.getWallet(walletID);
            if(!wallet.getOwnerID().equals(ownerID)){
                throw new IllegalArgumentException("User is not owned Wallet");
            }

            wallet.unlockMoney(amount);
        }catch (IllegalArgumentException | NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

}
