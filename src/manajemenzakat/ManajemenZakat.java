/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author RIZAL
 */
public class ManajemenZakat extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/login.fxml"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest((WindowEvent event) -> {
            //event.consume();
            System.exit(0);
        });
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(ManajemenZakat.class.getResourceAsStream("favicon.png")));
        stage.setTitle("Manajemen Zakat - Login");
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
