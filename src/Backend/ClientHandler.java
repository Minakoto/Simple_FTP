package Backend;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
            Random rand = new Random();
            while(true) {
                id = Server.idList[rand.nextInt(5)];
                if(!Server.ConnectedId.contains(id))
                    break;
            }
            Server.ConnectedId.add(id);
            ServerInput = new DataInputStream(socket.getInputStream());
            ServerOutput = new DataOutputStream(socket.getOutputStream());
            ServerOutput.writeInt(id);
            ClientDir = new File("src\\ServerBank\\client"+id);
            ClientDir.mkdirs();
            sendFiles();
            System.out.println("" + "Client " + id + " connected");
            HandleCommand();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFiles() throws IOException {
        if(ClientDir.listFiles().length != 0) {
            ServerOutput.writeInt(ClientDir.listFiles().length);
            for(File f : Objects.requireNonNull(ClientDir.listFiles())) {
                ServerOutput.writeUTF(f.getName());
            }
        }
    }

    private void HandleCommand() throws IOException {
        int command = 0;
        while (command != -1) {
            command = ServerInput.readInt();
            switch (command) {
                case 1:
                    System.out.println(command);
                    ReceiveFile();
                    break;
                case 2:
                    System.out.println(command);
                    SendFile();
                    break;
                case -1:
                    System.out.println(command);
                    Disconnect();
                    break;
                default:
                    System.out.println("Client " + id + " has sent an unknown command");
            }
        }
    }

    private void SendFile() throws IOException {
        String filename = ServerInput.readUTF();
        File file = new File(ClientDir.getPath()+ "\\" +filename);
        System.out.println(file.getPath());
        if(file.exists()) {
            ServerOutput.writeUTF("+");
            ServerOutput.writeLong(file.length());
            System.out.println("Sending file: "+file.getName()+ " to client" + id);
            FileInputStream tempfs = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while (bytesRead != -1) {
                ServerOutput.write(buffer, 0, bytesRead);
                System.out.println(bytesRead);
                bytesRead = tempfs.read(buffer);
            }
            System.out.println("File sent");
            tempfs.close();
        }
        else {
            ServerOutput.writeUTF("-");
        }
    }

    private void ReceiveFile() throws IOException {
        String filename = ServerInput.readUTF();
        File file = new File(ClientDir.getPath()+"\\"+filename);
        System.out.println(ClientDir.getName()+filename);
        System.out.println("Receiving file "+file.getName());
        byte[] buffer = new byte[4096];
        Long filesize = ServerInput.readLong();
        FileOutputStream tempfs = new FileOutputStream(file);
        int bytesRead = 0;
        int read = 0;
        while(read != filesize) {
            bytesRead = ServerInput.read(buffer);
            tempfs.write(buffer, 0, bytesRead);
            read += bytesRead;
        }
        System.out.println("File received");
        tempfs.close();
    }

    private void Disconnect() throws IOException {
        System.out.println("" + "Client " + id + " disconnected");
        if(socket.isConnected())
            socket.close();
    }
}