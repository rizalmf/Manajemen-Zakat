/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_DISTRIBUSI_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_MUSTAHIQ;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_USER;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_ZAKAT;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class ConfirmController implements Initializable {

    @FXML
    private Label lText;
    @FXML
    private AnchorPane ap;
    private Mustahiq mustahiq;
    public void setMustahiq(Mustahiq mustahiq){
        this.mustahiq = mustahiq;
    }
    private Zakat zakat;
    public void setZakat(Zakat zakat){
        this.zakat = zakat;
    }
    private User user;
    public void setUser(User user){
        this.user = user;
    }
    private String msg;
    public void setMsg(String msg){
        this.msg = msg;
    }
    private int type;
    public void setType(int type){
        this.type = type;
    }
    private ZakatKeluar zakatKeluar;
    public void setZakatKeluar(ZakatKeluar zakatKeluar){
        this.zakatKeluar = zakatKeluar;
    }
    private boolean in_button;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        in_button = false;
        ap.setBackground(Background.EMPTY);
        Platform.runLater(() -> {
            lText.setText((msg == null)?"Empty":msg);
        });
    }    

    @FXML
    private void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void onDrag(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
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
    private void confirm(ActionEvent event) {
        if (in_button) {
            return;
        }
        switch(type){
            case TYPE_ZAKAT: processZakat(event);
                break;
            case TYPE_MUSTAHIQ: processMustahiq(event);
                break;
            case TYPE_USER: processUser(event);
                break;
            case TYPE_DISTRIBUSI_ZAKAT: processZakatKeluar(event);
                break;
            default: lText.setText("Unknown request");
                break;
        }
    }
    private void processZakat(ActionEvent event){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                in_button = true;
                Response response = service.deleteZakat(zakat.getId_zakat(), 
                        zakat.getNama(), zakat.getKode());
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(event); }
                    else lText.setText(response.getMsg());
                }));
                tl.play();
                in_button = false;
            }
        }, 0);
    }
    private void processMustahiq(ActionEvent event){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                in_button = true;
                Response response = service.deleteMustahiq(mustahiq.getId_mustahiq(),
                        mustahiq.getNama());
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(event); }
                    else lText.setText(response.getMsg());
                }));
                tl.play();
                in_button = false;
            }
        }, 0);
    }
    private void processUser(ActionEvent event){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                in_button = true;
                Response response = service.deleteUser(user.getId_user(), user.getNama());
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(event); }
                    else lText.setText(response.getMsg());
                }));
                tl.play();
                in_button = false;
            }
        }, 0);
    }
    private void processZakatKeluar(ActionEvent event){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                in_button = true;
                Response response = service.deleteZakatKeluar(zakatKeluar.getId_zakat_keluar(), 
                         zakatKeluar.getKode());
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(event); }
                    else lText.setText(response.getMsg());
                }));
                tl.play();
                in_button = false;
            }
        }, 0);
    }

    @FXML
    private void cancel(ActionEvent event) {
        close(event);
    }
    
}
