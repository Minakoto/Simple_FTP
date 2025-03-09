package GUI;

import Backend.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ClientGUI extends Application{


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Client Overlay");
        stage.setResizable(false);
        stage.show();
    }

}