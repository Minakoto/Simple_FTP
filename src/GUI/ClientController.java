package GUI;

import Backend.Client;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ClientController implements Initializable{
    @FXML
    Text idLabel;
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

    public void addF() throws IOException {
        FileChooser files = new FileChooser();
        File file = files.showOpenDialog(null);
        if (file != null) {
            String tmp = file.getName();
            LocalFiles.put(tmp, Files.copy(file.toPath(), Path.of(ClientBank + "\\" + tmp)).toFile());
            update();
        }
    }
    public void remF() {
        FileChooser files = new FileChooser();
        files.setInitialDirectory(new File(ClientBank));
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
        File[] ClientBan = new File(ClientBank).listFiles();
        for(File f : ClientBan) {
            LocalFiles.put(f.getName(), f);
        }
        ServerFiles.addAll(client.getServerFiles());
        localfiles.getItems().addAll(LocalFiles.keySet());
        serverfiles.getItems().addAll(ServerFiles);
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

    public void update() {
        localfiles.getItems().removeAll(LocalFiles.keySet());
        localfiles.getItems().addAll(LocalFiles.keySet());
        localfiles.getItems().removeAll(ServerFiles);
        localfiles.getItems().addAll(ServerFiles);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
