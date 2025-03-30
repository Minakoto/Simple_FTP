package Backend;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;

public class Server {

    public static final int[] idList = {303, 5012, 1, 2, 0};
    public static LinkedHashSet<Integer> ConnectedId = new LinkedHashSet<>();
    public static int numberConnected = 0;
    public static int PORT = 9595;

    public Server() throws IOException {
        ServerSocket Server = new ServerSocket(PORT);
        while(!Server.isClosed()) {
            Socket ClientSocket = Server.accept();
            ClientHandler Client = new ClientHandler(ClientSocket);
            Thread ClientThread = new Thread(Client);
            ClientThread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        Server Server = new Server();
    }
}
