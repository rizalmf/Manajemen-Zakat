/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import manajemenzakat.ManajemenZakat;
import static manajemenzakat.controller.MainController.service;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_MUSTAHIQ_ADD;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_MUSTAHIQ_LIST;
import manajemenzakat.model.JenisMustahiq;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class MustahiqAddController implements Initializable {

    @FXML
    private JFXTextField tfFormMustahiqNama;
    @FXML
    private JFXTextArea tfFormMustahiqAlamat;
    @FXML
    private JFXTextArea tfFormMustahiqKeterangan;
    @FXML
    private JFXComboBox<JenisMustahiq> cbJenisMustahiq;
    @FXML
    private Label lMsgFormMustahiq;
    @FXML
    private VBox parent;
    private boolean in_button;
    private Mustahiq mustahiq;
    public void setMustahiq(Mustahiq mustahiq){
        this.mustahiq = mustahiq;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        in_button = false;
        cbJenisMustahiq.setConverter(new StringConverter<JenisMustahiq>() {
            @Override
            public String toString(JenisMustahiq object) {
                return object.getNama_jenis();
            }

            @Override
            public JenisMustahiq fromString(String string) {
                return null;
            }
        });
        Response response = service.getJenisMustahiq();
        if (response.isStatus()) {
            tfFormMustahiqNama.clear();
            tfFormMustahiqAlamat.clear();
            tfFormMustahiqKeterangan.clear();
            cbJenisMustahiq.setItems((ObservableList<JenisMustahiq>) response.getData());
        }
        Platform.runLater(() -> {
            if (mustahiq != null) {
                tfFormMustahiqNama.setText(mustahiq.getNama());
                tfFormMustahiqAlamat.setText(mustahiq.getAlamat());
                tfFormMustahiqKeterangan.setText(mustahiq.getKeterangan());
                cbJenisMustahiq.getSelectionModel().select(mustahiq.getId_jenis_mustahiq());
            }
        });
    }    

    @FXML
    private void simpanMustahiq(ActionEvent event) {
        if (in_button) {
            return;
        }
        String nama = tfFormMustahiqNama.getText();
        String alamat = tfFormMustahiqAlamat.getText();
        String keterangan = tfFormMustahiqKeterangan.getText();
        JenisMustahiq jenisMustahiq = cbJenisMustahiq.getSelectionModel().getSelectedItem();
        if (nama.isEmpty() || jenisMustahiq == null) {
            openMsgMustahiq("*Nama & jenis mustahiq tidak boleh kosong");
            return;
        }
        if (mustahiq == null) {
            mustahiq = new Mustahiq(nama, alamat, keterangan, 
                    jenisMustahiq.getId_jenis_mustahiq(), Session.getUser().getId_user());
        }else{
            mustahiq.setNama(nama);
            mustahiq.setAlamat(alamat);
            mustahiq.setKeterangan(keterangan);
            mustahiq.setId_jenis_mustahiq(jenisMustahiq.getId_jenis_mustahiq());
            mustahiq.setId_user(Session.getUser().getId_user());
        }
        
        in_button = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.saveMustahiq(mustahiq);
                in_button = false;
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close();}
                    else openMsgMustahiq(response.getMsg());
                }));
                tl.play();
            }
        }, 0);
        

    }

    @FXML
    private void batalSimpanMustahiq(ActionEvent event) {
        close();
    }
    private void close(){
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_MUSTAHIQ_ADD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_MUSTAHIQ_LIST);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/mustahiq.fxml"));
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (IOException e) {
        }
        row.setVisible(true);
    }
    private void openMsgMustahiq(String msg){
        lMsgFormMustahiq.setText(msg);
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            lMsgFormMustahiq.setText("");
        }));
        tl.setDelay(Duration.seconds(2.5));
        tl.play();
    }
}
