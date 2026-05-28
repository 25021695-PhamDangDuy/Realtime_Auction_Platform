package myWeb.view.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener messageListener;

    public boolean connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            Thread listenerThread = new Thread(this::listenToServer);
            listenerThread.setDaemon(true);
            listenerThread.start();
            return true;
        } catch (IOException e) {
            System.err.println("Không thể kết nối Server: " + e.getMessage());
            return false;
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    private void listenToServer() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (messageListener != null) {
                    messageListener.onMessageReceived(serverMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("Mất kết nối với Server.");
        }
    }
}
