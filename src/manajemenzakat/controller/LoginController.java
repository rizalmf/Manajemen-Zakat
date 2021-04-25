/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import manajemenzakat.ManajemenZakat;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.service.impl.ServiceJdbc;
import manajemenzakat.util.FXResizeHelper;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class LoginController implements Initializable {

    @FXML
    private JFXTextField tfUser;
    @FXML
    private JFXPasswordField tfPassword;
    @FXML
    private Label lMsg;
    
    private boolean in_button;
    @FXML
    private AnchorPane ap;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ap.setBackground(Background.EMPTY);
        if (service == null) {
//            service = new ServiceTemp();
            service = new ServiceJdbc();
        }
        service.create();
        lMsg.setText("");
        in_button = false;
        Platform.runLater(() -> {
            tfUser.requestFocus();
        });
    }    

    @FXML
    private void mini(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onDrag(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    double xOffset = 0;
    double yOffset = 0;
    @FXML
    private void onPress(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    
    @FXML
    private void login(ActionEvent event) {
        checkLogin();
    }
    
    @FXML
    private void keyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) { checkLogin(); }
    }
    
    /**
     * Logic GUI START
     */
    private void checkLogin(){
        if (in_button) { return; }
        if (isEmpty()) {
            setMsg("*Data yang dimasukkan tidak lengkap");
            return;
        }
        in_button = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.login(new User(tfUser.getText(), tfPassword.getText()));
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) {
                        Session.setUser((User) response.getData());
                        openMain();
                    }else setMsg(response.getMsg());
                }));
                tl.play();
                in_button = false;
            }
        }, 0);
    }
    private boolean isEmpty(){
        return (tfUser.getText().isEmpty() || tfPassword.getText().isEmpty());
    }
    private void setMsg(String msg){
        lMsg.setText(msg);
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            lMsg.setText("");
        }));
        tl.setDelay(Duration.seconds(1.3));
        tl.play();
    }
    private void openMain(){
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (ActionEvent e) -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ManajemenZakat.class.getResource("views/main.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
            MainController mainController = fxmlLoader.getController();
            mainController.setLoginController(LoginController.this);
            Stage stage = new Stage();
            stage.setOnCloseRequest((WindowEvent event) -> {
                //                    event.consume();
                System.exit(0);
            });
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Manajemen Zakat - Dashboard");
            stage.getIcons().add(new Image(ManajemenZakat.class.getResourceAsStream("favicon.png")));
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);  
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setMinWidth(485);
            stage.setMinHeight(300);
            new FXResizeHelper(stage, -10, 7, 485, 300);
            stage.show();
            Stage thisStage = (Stage) lMsg.getScene().getWindow();
            thisStage.close();
        }));
        tl.setCycleCount(1);
        tl.play();
    }
    /**
     * Logic GUI END
     */
}
