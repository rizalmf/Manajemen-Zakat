/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import static manajemenzakat.controller.MainController.service;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT_CHILD;
import manajemenzakat.model.JenisMustahiq;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.ZakatKeluar;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class ZakatDistribusiController implements Initializable {

    @FXML
    private VBox parent;
    @FXML
    private JFXTextField tfFormNama;
    @FXML
    private JFXComboBox<Mustahiq> cbMustahiq;
    @FXML
    private JFXRadioButton rbUang;
    @FXML
    private JFXRadioButton rbBeras;
    @FXML
    private JFXTextField tfFormNominal;
    @FXML
    private Label lJenisNominal;
    @FXML
    private Label lMsgFormZakat;
    @FXML
    private Label lTotalUang;
    @FXML
    private Label lTotalBeras;
    private boolean in_button;
    private NumberFormat nf;
    private double totalUang, totalBeras;
    private SimpleDateFormat sdf;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        sdf = new SimpleDateFormat("/yyMMdd/HHmmss");
        in_button = false;
        initiateCurr();
        initiateTotal();
        initiateLater();
        lJenisNominal.setText("");
        rbBeras.selectedProperty().addListener((o, oldVal, newVal) -> {
            rbUang.setSelected(!newVal);
            if (newVal) {
                lJenisNominal.setText("Liter");
            }
        });
        rbUang.selectedProperty().addListener((o, oldVal, newVal) -> {
            rbBeras.setSelected(!newVal);
            if (newVal) {
                lJenisNominal.setText("Rupiah");
            }
        });
        cbMustahiq.setConverter(new StringConverter<Mustahiq>() {
            @Override
            public String toString(Mustahiq object) {
                return object.getNama();
            }

            @Override
            public Mustahiq fromString(String string) {
                return null;
            }
        });
        Platform.runLater(() -> {
            tfFormNama.setText(Session.getUser().getNama());
        });
    }    
    private void initiateCurr(){
        nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) nf).setDecimalFormatSymbols(dfs);
        nf.setMaximumFractionDigits(0);
    }
    private void initiateTotal(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response r1 = service.getDashboardZakat();
                Response r2 = service.getZakatKeluarList();
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (r1.isStatus()) {
                        double[] data = (double[]) r1.getData();
                        totalUang = data[1]+data[2]+data[3];
                        Map<String, Object> m = (Map<String, Object>) r2.getData();
                        double[] dataMinus = (double[]) m.get("total");
                        totalUang = totalUang - dataMinus[0];
                        totalBeras = data[0] - dataMinus[1]; 
                        lTotalUang.setText("Rp. "+nf.format(totalUang));
                        lTotalBeras.setText(totalBeras+" Liter");
                    }
                }));
                tl.play();
            }
        }, 0);
    }
    private void initiateLater(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response r = service.getMustahiqList("");
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (r.isStatus()) {
                        List<Mustahiq> mlist =(List<Mustahiq>) r.getData();
                        ObservableList<Mustahiq> list = FXCollections.observableArrayList();
                        list.addAll(mlist);
                        cbMustahiq.setItems(list);
                    }
                }));
                tl.play();
            }
        }, 0);
    }
    @FXML
    private void kembaliMenuZakat(ActionEvent event) {
        close();
    }

    @FXML
    private void simpan(ActionEvent event) {
        if (in_button) {
            return;
        }
        if (!rbUang.isSelected() && !rbBeras.isSelected()) {
            openMsgZakat("*Jenis distribusi belum dipilih");
            return;
        }
        Mustahiq m = cbMustahiq.getSelectionModel().getSelectedItem();
        String nominal = tfFormNominal.getText();
        if (m == null || nominal.replaceAll(" ", "").isEmpty()) {
            openMsgZakat("*Mustahiq & nominal tidak boleh kosong");
            return;
        }
        if (!isNominal(nominal)) {
            openMsgZakat("*Format nominal salah. contoh yang benar: (Rupiah) 50000,(Liter) 7.5");
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ZakatKeluar keluar = new ZakatKeluar();
        keluar.setId_mustahiq(m.getId_mustahiq());
        keluar.setId_user(Session.getUser().getId_user());
        keluar.setNominal(Double.parseDouble(nominal));
        keluar.setTanggal(df.format(new Date()));
        double nom = Double.parseDouble(nominal);
        if (rbUang.isSelected()) {
            if(nom > totalUang) {
                openMsgZakat("*Total uang yang ada tidak cukup");
                return;
            }
        }else{
            if(nom > totalBeras) {
                openMsgZakat("*Total beras yang ada tidak cukup");
                return;
            }
        }
        keluar.setIs_money((rbUang.isSelected())? "Y": "N");
        keluar.setKode(generateCodeZakat("ZD"));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.saveZakatKeluar(keluar);
                in_button =false;
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close(); }
                    else openMsgZakat(response.getMsg());
                }));
                tl.play();
            }
        }, 0);
    }
    private void openMsgZakat(String msg){
        lMsgFormZakat.setText(msg);
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            lMsgFormZakat.setText("");
        }));
        tl.setDelay(Duration.seconds(3.5));
        tl.play();
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
    @FXML
    private void batal(ActionEvent event) {
        close();
    }
    private void close(){
        if (in_button) {
            return;
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_ZAKAT_CHILD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_ZAKAT);
        row.setVisible(true);
    }
}
