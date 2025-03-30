package Backend;
import java.io.*;
import java.util.ArrayList;

import java.net.Socket;

public class Client {
    private ArrayList<String> ServerFiles = new ArrayList<>();

    private int id;
    Socket Socket;
    DataInputStream ClientInput;
    DataOutputStream ClientOutput;
    public Client() throws IOException {
        Socket = new Socket("localhost", Server.PORT);
        ClientInput = new DataInputStream(Socket.getInputStream());
        ClientOutput = new DataOutputStream(Socket.getOutputStream());
        id = ClientInput.readInt();
        getFiles();
    }
    public ArrayList<String> getServerFiles() {
        return ServerFiles;
    }
    public int getId() {
        return id;
    }

    public void getFiles() throws IOException {
        ServerFiles.clear();
        int len = ClientInput.readInt();
        for(int i = 0; i < len; i++) ServerFiles.add(ClientInput.readUTF());
    }

    public void sendCommand(int command) throws IOException {
        ClientOutput.writeInt(command);
    }
    public void SendFile(File file) throws IOException {
        DataInputStream tmpds = new DataInputStream(new FileInputStream(file));
        ClientOutput.writeUTF(file.getName());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        ClientOutput.writeLong(file.length());
        System.out.println(file.length());
        while (bytesRead != -1) {
            ClientOutput.write(buffer, 0, bytesRead);
            bytesRead = tmpds.read(buffer);
        }
        tmpds.close();
        getFiles();
    }
    public void ReceiveFile(String filename) throws IOException {
        ClientOutput.writeUTF(filename);
        int res = ClientInput.readInt();
        if(res == -1) {
            long filesize = ClientInput.readLong();
            File file = new File("src\\ClientBank\\"+ getId() + "\\" + filename);
            FileOutputStream tempfs = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            int read = 0;
            while (read != filesize) {
                bytesRead = ClientInput.read(buffer);
                tempfs.write(buffer, 0, bytesRead);
                read += bytesRead;
            }
            tempfs.close();
        }
        else System.out.println("Server Error");
    }

    public void Disconnect() throws IOException {
        ClientInput.close();
        ClientOutput.close();
        if(Socket.isConnected())
            Socket.close();
    }

    public static void main(String[] args) throws IOException {
        Client cl = new Client();
    }
}



