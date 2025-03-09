package Backend;// Передача и получение файла с противоположного клиента или сервера
// в виде последовательности блоков с двоичными данными в установленном соединении
// (FTP-сервер);
import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.net.Socket;

public class Client {
    public static final int[] idList = {303, 5012, 1, 2, 0};
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

    private void getFiles() throws IOException {
        int len = ClientInput.readInt();
        for(int i = 0; i < len; i++) {
            ServerFiles.add(ClientInput.readUTF());
        }
    }

    public int getId() {
        return id;
    }

    public void sendCommand(int command) throws IOException {
        ClientOutput.writeInt(command);
    }

    private void HandleCommand() throws IOException {
        Scanner scan = new Scanner(System.in);
        int command = 0;
        while(command != -1) {
            ClientOutput.writeInt(command);
            switch (command) {
                case 1:
                    File file = new File(scan.next());
                    SendFile(file);
                    break;
                case 2:
                    String filename = scan.next();
                    ReceiveFile(filename);
                    break;
                case -1:
                    Disconnect();
                    break;
                default:
                    System.out.println("Unknown command, try again");
            }
        }
    }

    public void SendFile(File file) throws IOException {
        DataInputStream tmpds = new DataInputStream(new FileInputStream(file));
        ClientOutput.writeUTF(file.getName());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        ClientOutput.writeLong(file.length());
        while (bytesRead != -1) {
            ClientOutput.write(buffer, 0, bytesRead);
            System.out.println(bytesRead);
            bytesRead = tmpds.read(buffer);
        }
        System.out.println("Sent File");
    }

    public void ReceiveFile(String filename) throws IOException {
        ClientOutput.writeUTF(filename);
        String result = ClientInput.readUTF();
        if (result.equals("+")) {
            Long filesize = ClientInput.readLong();
            File file = new File("src\\ClientBank\\"+filename);
            FileOutputStream tempfs = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            int read = 0;
            while (read != filesize) {
                bytesRead = ClientInput.read(buffer);
                tempfs.write(buffer, 0, bytesRead);
                read += bytesRead;
            }
            System.out.println("File received");
            tempfs.close();
        }
        else {
            System.out.println("Server error");
        }
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



