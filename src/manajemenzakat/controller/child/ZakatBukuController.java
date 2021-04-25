/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT_CHILD;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_ZAKAT;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.Zakat;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class ZakatBukuController implements Initializable {

    @FXML
    private VBox parent;
    @FXML
    private JFXTextField tfOptionZakatSearch;
    @FXML
    private ScrollPane spZakatList;
    @FXML
    private JFXButton bPrevZakat;
    @FXML
    private Label lPageZakat;
    @FXML
    private JFXButton bNextZakat;
    private boolean in_button;
    private AdditionalNode additionalNode;
    private int page, max_page, max_list_show;
    public Mustahiq mustahiq;
    private JasperPrint jPrint;
    private SimpleDateFormat sdf;
    private String search;
    private List<Zakat> zakatList;
    private List<Zakat> pageList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        in_button = false;
        HBox.setHgrow(parent, Priority.ALWAYS);
        additionalNode = new AdditionalNode();
        max_list_show = 20;
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        tfOptionZakatSearch.clear();
        bNextZakat.setDisable(true);
        bPrevZakat.setDisable(true);
        search = "";
        getZakatList();
    }    

    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            search = tfOptionZakatSearch.getText();
            getZakatList();
        }
    }
    public void getZakatList(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                System.out.println("getMustahiqList()");
                Response response = service.getZakatList(search);
                if (response.isStatus()) {
                    zakatList = (ArrayList<Zakat>) response.getData();
                    Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                        page = 1;
                        setLayoutZakatList();
                    }));
                    tl.play();
                }
            }
        }, 0);
        
    }
    private void setLayoutZakatList(){
        if (zakatList.size() > max_list_show) {
            int m = zakatList.size()/max_list_show;
            int mx = zakatList.size()%max_list_show;
            if (mx > 0) {
                max_page = m + 1;
            }else{
                max_page = m;
            }
            int from, to;
            if (page == 1) {
                from = 0;
                to = max_list_show;
                bPrevZakat.setDisable(true);
                bNextZakat.setDisable(!(max_page > page));
            }else{
                from = (page-1) * max_list_show;
                to = page * max_list_show;
                if (to > zakatList.size()) {
                    to = zakatList.size();
                }
            }
            System.out.println("index from:"+from+", to:"+to);
            pageList = zakatList.subList(from, to);
            lPageZakat.setText(page+"/"+max_page);
//           hbPage.setVisible(true);
        }else{
            bPrevZakat.setDisable(true);
            bNextZakat.setDisable(true);
            page = 1;
            lPageZakat.setText(1+"/"+1);
            pageList = zakatList;
//             hbPage.setVisible(true);
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        Object[] obj = additionalNode.buildZakatList(pageList, page, max_list_show, ap);
        VBox vbox = (VBox) obj[0];
        setActionZakatList((List<JFXButton>) obj[1]);
        spZakatList.setContent(vbox);
        spZakatList.setVvalue(0);
    }
    private void setActionZakatList(List<JFXButton> list){
        int i = 0;
        for (Zakat z: pageList) {
            list.get(i).setOnAction((e) -> {
                String msg = "Hapus pembayaran zakat atas nama: "+
                        z.getNama()+" ?";
                additionalNode.getAlert(msg, TYPE_ZAKAT, z, null, null, null);
                getZakatList();
            });
            i++;
        }
    }
    @FXML
    private void optionZakatSearch(ActionEvent event) {
        search = tfOptionZakatSearch.getText();
        getZakatList();
    }

    @FXML
    private void getPrevPage(ActionEvent event) {
        if (page > 1) {
            page--;
            setLayoutZakatList();
            bNextZakat.setDisable(false);
            if (page == 1) {
                 bPrevZakat.setDisable(true);
            }
        }
    }

    @FXML
    private void getNextPage(ActionEvent event) {
        if (page < max_page) {
            page++;
            setLayoutZakatList();
             bPrevZakat.setDisable(false);
             if (page >= max_page) {
                 bNextZakat.setDisable(true);
            }
        }
    }

    @FXML
    private void kembaliMenuZakat(ActionEvent event) {
        close();
    }
    private void close(){
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_ZAKAT_CHILD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_ZAKAT);
        row.setVisible(true);
    }
}
