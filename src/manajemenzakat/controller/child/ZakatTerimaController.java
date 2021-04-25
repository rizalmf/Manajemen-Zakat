/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT_CHILD;
import manajemenzakat.model.JenisZakat;
import manajemenzakat.model.Response;
import manajemenzakat.model.Zakat;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class ZakatTerimaController implements Initializable {

    @FXML
    private JFXTextField tfFormZakatNama;
    @FXML
    private JFXTextField tfFormZakatAlamat;
    @FXML
    private Spinner<Integer> spinnerFormZakat;
    @FXML
    private JFXComboBox<JenisZakat> cbJenisZakat;
    @FXML
    private JFXTextField tfFormZakatNominal;
    @FXML
    private Label lJenisNominal;
    @FXML
    private VBox parent;
    private boolean in_button;
    @FXML
    private Label lMsgFormZakat;
    private SimpleDateFormat sdf;
    private SimpleDateFormat dateFormat;
    private Zakat zakat;
    @FXML
    private JFXButton bKalkulator;
    public void setZakat(Zakat zakat){
        this.zakat = zakat;
    }
    private AdditionalNode additionalNode;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        in_button = false;
        sdf = new SimpleDateFormat("/yyMMdd/HHmmss");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        additionalNode = new AdditionalNode();
        bKalkulator.setVisible(false);
        lJenisNominal.setText("");
        cbJenisZakat.setConverter(new StringConverter<JenisZakat>() {
            @Override
            public String toString(JenisZakat object) {
                return object.getNama_jenis();
            }

            @Override
            public JenisZakat fromString(String string) {
                return null;
            }
        });
        Response response = service.getJenisZakat();
        if (response.isStatus()) {
            cbJenisZakat.setItems((ObservableList<JenisZakat>) response.getData());
        }
        spinnerFormZakat.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(1, 1000));
        cbJenisZakat.setOnAction((e) -> {
            JenisZakat jz = cbJenisZakat.getSelectionModel().getSelectedItem();
            lJenisNominal.setText(jz.getKeterangan());
        });
        Platform.runLater(() -> {
            if (zakat != null) {
                tfFormZakatNama.setText(zakat.getNama());
                tfFormZakatAlamat.setText(zakat.getAlamat());
                spinnerFormZakat.getValueFactory().setValue(convertJiwa(zakat.getJiwa()));
                cbJenisZakat.getSelectionModel().select(zakat.getId_jenis_zakat());
                tfFormZakatNominal.setText(zakat.getNominal()+"");
            }
        });
    }    
    private int convertJiwa(String text){
        int j;
        try {
            j = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            j = 0;
        }
        return j;
    }
    @FXML
    private void kembaliMenuZakat(ActionEvent event) {
        if (in_button) {
            return;
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_ZAKAT_CHILD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_ZAKAT);
        row.setVisible(true);
    }

    @FXML
    private void simpanZakat(ActionEvent event) {
        if (in_button) {
            return;
        }
        String nama = tfFormZakatNama.getText();
        String alamat = tfFormZakatAlamat.getText();
        String nominal = tfFormZakatNominal.getText();
        int jmlJiwa = spinnerFormZakat.getValue();
        JenisZakat jenisZakat = cbJenisZakat.getSelectionModel().getSelectedItem();
        if (nama.isEmpty() || jenisZakat == null || nominal.isEmpty()) {
            openMsgZakat("*Nama, jenis zakat & nominal tidak boleh kosong");
            return;
        }
        if (!isNominal(nominal)) {
            openMsgZakat("*Format nominal salah. contoh yang benar: (Rupiah) 50000,(Liter) 7.5");
            return;
        }
        in_button = true;
        if (zakat == null) { zakat = new Zakat(); }
        zakat.setNama(nama);
        zakat.setAlamat(alamat);
        zakat.setNominal(Double.parseDouble(nominal));
        zakat.setJiwa(jmlJiwa+"");
        zakat.setId_jenis_zakat(jenisZakat.getId_jenis_zakat());
        zakat.setId_user(Session.getUser().getId_user());
        zakat.setKode(generateCodeZakat(jenisZakat.getKode()));
        zakat.setTanggal(dateFormat.format(new Date()));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.saveZakat(zakat);
                in_button =false;
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(); }
                    else openMsgZakat(response.getMsg());
                }));
                tl.play();
            }
        }, 0);
    }
    private boolean isNominal(String text){
        boolean nom = true;
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            nom = false;
        }
        return nom;
    }
    private String generateCodeZakat(String kode){
        return kode+sdf.format(new Date());
    }
    private void openMsgZakat(String msg){
        lMsgFormZakat.setText(msg);
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            lMsgFormZakat.setText("");
        }));
        tl.setDelay(Duration.seconds(3.5));
        tl.play();
    }
    @FXML
    private void batalSimpanZakat(ActionEvent event) {
        if (in_button) {
            return;
        }
        close();
    }
    
    private void close(){
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
//        ap.getChildren().get(ROW_ZAKAT_CHILD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_ZAKAT_CHILD);
        row.setVisible(true);
        additionalNode.openMenu("views/zakatBuku.fxml", row);
    }

    @FXML
    private void kalkulatorZakat(ActionEvent event) {
    }
}
