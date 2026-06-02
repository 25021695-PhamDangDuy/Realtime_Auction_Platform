package server;

import controller.brain.WalletManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuctionServer {
    private static final int PORT = 8080;
    private static final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) {
        System.out.println("=== HỆ THỐNG ĐẤU GIÁ KHỞI ĐỘNG ===");
        WalletManager manager = WalletManager.getInstance();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                ClientManager.addClient(handler);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }
}