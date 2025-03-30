package GUI;

import Backend.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

//TODO ADD CLIENT FOLDERS CONNECTION BUTTON

public class ClientController{
    @FXML
    Text idLabel;
    @FXML
    Text stateLabel;
    @FXML
    Button sendB;
    @FXML
    Button getB;
    @FXML
    Button addB;
    @FXML
    Button remB;
    @FXML
    Button closeB;
    @FXML
    Button connectB;
    @FXML
    Button disconnectB;
    @FXML
    Button updB;
    @FXML
    ListView localfiles;
    @FXML
    ListView serverfiles;

    private static final String ClientBank = "src\\ClientBank";
    Backend.Client client;
    private int numFiles = 0;
    private HashMap<String, File> LocalFiles = new HashMap<>();
    private ArrayList<String> ServerFiles = new ArrayList<>();
    String selectedLocal;
    String selectedServer;

    public void connect() throws IOException {
        init();
        stateLabel.setText("Open");
        stateLabel.setFill(Color.GREEN);
    }
    public void disconnect() throws IOException {
        client.Disconnect();
        stateLabel.setText("Closed");
        stateLabel.setFill(Color.RED);
    }

    public void addF() throws IOException {
        FileChooser files = new FileChooser();
        File file = files.showOpenDialog(null);
        if (file != null) {
            String tmp = file.getName();
            LocalFiles.put(tmp, Files.copy(file.toPath(), Path.of(ClientBank + "\\" + client.getId() + "\\" + tmp)).toFile());
            update();
        }
    }
    public void remF() {
        FileChooser files = new FileChooser();
        files.setInitialDirectory(new File(ClientBank + "\\" + client.getId()));
        File file = files.showOpenDialog(null);
        if (file != null) {
            String tmp = file.getName();
            if (file.delete()) {
                localfiles.getItems().removeAll(LocalFiles.keySet());
                LocalFiles.remove(tmp);
                update();
            }
        }
    }

    public void sendF() throws IOException {
        client.sendCommand(1);
        client.SendFile(LocalFiles.get(selectedLocal));

        update();
    }
    public void receiveF() throws IOException {
        client.sendCommand(2);
        client.ReceiveFile(selectedServer);
        update();
    }

    public void closeB() throws IOException {
        client.sendCommand(-1);
        client.Disconnect();
        Platform.exit();
    }

    private void init() throws IOException {
        client = new Client();
        idLabel.setText("Your ID: "+ client.getId());
        update();
        localfiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                selectedLocal = (String) localfiles.getSelectionModel().getSelectedItem();
            }
        });
        serverfiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                selectedServer = (String) serverfiles.getSelectionModel().getSelectedItem();
            }
        });
    }

    public void remove() {
        localfiles.getItems().removeAll(LocalFiles.keySet());
        serverfiles.getItems().removeAll(ServerFiles);
    }
    private void upd_local() {
        File ClientDir = new File(ClientBank + "\\" + client.getId());
        ClientDir.mkdirs();
        File[] ClientBan = ClientDir.listFiles();
        if (ClientBan != null) {
            for(File f : ClientBan) {
                LocalFiles.put(f.getName(), f);
            }
        }
    }
    public void update() {
        remove();
        ServerFiles.clear();
        ServerFiles.addAll(client.getServerFiles());
        upd_local();
        localfiles.getItems().addAll(LocalFiles.keySet());
        serverfiles.getItems().addAll(ServerFiles);
    }
}
