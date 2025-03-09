package GUI;

import Backend.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    @FXML
    ListView<String> clientList;
    @FXML
    ListView<String> clientFiles;

    Server server;
    ArrayList<String> clients = new ArrayList<>();
    ArrayList<String> clientfiles = new ArrayList<>();
    private int a = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            server = new Server();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addPane() {
        clients.add("Test"+a);
        clientList.getItems().addAll(clients.getLast());
        clientfiles.add("Filetest"+a);
        clientFiles.getItems().addAll(clientfiles.getLast());
        a++;
    }


}
