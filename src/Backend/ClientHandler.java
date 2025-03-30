package Backend;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

public class ClientHandler implements Runnable {
    Socket socket;
    private DataInputStream ServerInput;
    private DataOutputStream ServerOutput;
    int id = -1;
    File ClientDir;

    public ClientHandler(Socket client) {
        socket = client;
    }
    @Override
    public void run() {
        try {
            Random rnd = new Random();
            while(true) {
                id = rnd.nextInt(4);
                if(!Server.ConnectedId.contains(id)) break;
            }
            Server.ConnectedId.add(id);
            ServerInput = new DataInputStream(socket.getInputStream());
            ServerOutput = new DataOutputStream(socket.getOutputStream());
            ClientDir = new File("src\\ServerBank\\client"+id);
            ClientDir.mkdirs();
            ServerOutput.writeInt(id);
            System.out.println("Client " + id + ": " + "Connect");
            sendFiles();
            HandleCommand();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendFiles() throws IOException {
        ServerOutput.writeInt(ClientDir.listFiles().length);
        for(File f : Objects.requireNonNull(ClientDir.listFiles())) ServerOutput.writeUTF(f.getName());
    }
    private void HandleCommand() throws IOException {
        int command = 0;
        while (command != -1) {
            command = ServerInput.readInt();
            System.out.println("Client" + id + ": " + command);
            switch (command) {
                case 1:
                    ReceiveFile();
                    break;
                case 2:
                    SendFile();
                    break;
                case -1:
                    Disconnect();
                    break;
                default:
                    System.out.println("Client" + id + ": " + "Unknown command");
            }
        }
    }

    private void SendFile() throws IOException {
        String filename = ServerInput.readUTF();
        File file = new File(ClientDir.getPath()+ "\\" +filename);
        if(file.exists()) {
            ServerOutput.writeInt(-1);
            ServerOutput.writeLong(file.length());
            FileInputStream tempfs = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while (bytesRead != -1) {
                ServerOutput.write(buffer, 0, bytesRead);
                bytesRead = tempfs.read(buffer);
            }
            tempfs.close();
        }
        else ServerOutput.writeInt(-2);
    }

    private void ReceiveFile() throws IOException {
        String filename = ServerInput.readUTF();
        File file = new File(ClientDir.getPath()+"\\"+filename);
        byte[] buffer = new byte[4096];
        long filesize = ServerInput.readLong();
        FileOutputStream tempfs = new FileOutputStream(file);
        int bytesRead = 0;
        int read = 0;
        while(read != filesize) {
            bytesRead = ServerInput.read(buffer);
            tempfs.write(buffer, 0, bytesRead);
            read += bytesRead;
        }
        tempfs.close();
        sendFiles();
    }

    private void Disconnect() throws IOException {
        System.out.println("Client " + id + ": " + "Disconnect");
        if(socket.isConnected())
            socket.close();
    }
}