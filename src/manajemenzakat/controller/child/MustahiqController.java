/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_MUSTAHIQ;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.print.PrintUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class MustahiqController implements Initializable {

    @FXML
    private JFXTextField tfOptionMustahiqSearch;
    @FXML
    private ScrollPane spMustahiqList;
    @FXML
    private JFXButton bPrevMustahiq;
    @FXML
    private Label lPageMustahiq;
    @FXML
    private JFXButton bNextMustahiq;
    private List<Mustahiq> mustahiqList;
    private List<Mustahiq> pagelist;
    private AdditionalNode additionalNode;
    @FXML
    private VBox parent;
    private int page, max_page, max_list_show;
    public Mustahiq mustahiq;
    private JasperPrint jPrint;
    private SimpleDateFormat sdf;
    private String search;
    @FXML
    private JFXButton bPrint;
    @FXML
    private JFXButton bExcel;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        HBox.setHgrow(parent, Priority.ALWAYS);
        additionalNode = new AdditionalNode();
        max_list_show = 20;
//        bPrint.setVisible(false);
//        bExcel.setVisible(false);
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        tfOptionMustahiqSearch.clear();
        bNextMustahiq.setDisable(true);
        bPrevMustahiq.setDisable(true);
        search = "";
        getMustahiqList();
    }    

    @FXML
    private void optionMustahiqPrint(ActionEvent event) {
        try {
            PrintUtil print = new PrintUtil();
            jPrint = print.getLaporanMustahiq(mustahiqList);
            JasperPrintManager.printReport(jPrint, true);
        } catch (Exception ex) {
            Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void optionMustahiqExcel(ActionEvent event) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Pilih folder export excel");
        Stage thisStage = (Stage) parent.getScene().getWindow();
        File folder = fc.showDialog(thisStage);
        if (folder != null) {
            try {
                PrintUtil print = new PrintUtil();
                jPrint = print.getLaporanMustahiq(mustahiqList);
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jPrint));
                System.out.println("here");
                exporter.setExporterOutput(
                        new SimpleOutputStreamExporterOutput(folder.getAbsolutePath()
                                +"laporan mustahiq-"+df.format(new Date())+".xls"));
                SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
                SimpleXlsExporterConfiguration xlsExporterConfiguration = new SimpleXlsExporterConfiguration();
                xlsReportConfiguration.setOnePagePerSheet(true);
                xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
                xlsReportConfiguration.setDetectCellType(true);
                xlsReportConfiguration.setWhitePageBackground(false);
                exporter.setConfiguration(xlsExporterConfiguration);
                exporter.exportReport();
                Desktop.getDesktop().open(folder);
            } catch (Exception ex) {
                Logger.getLogger(MustahiqController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void optionMustahiqSearch(ActionEvent event) {
        search = tfOptionMustahiqSearch.getText();
        getMustahiqList();
    }
    
    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            search = tfOptionMustahiqSearch.getText();
            getMustahiqList();
        }
    }
    
    public void getMustahiqList(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                System.out.println("getMustahiqList()");
                Response response = service.getMustahiqList(search);
                if (response.isStatus()) {
                    mustahiqList = (ArrayList<Mustahiq>) response.getData();
                    Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                        page = 1;
                        setLayoutMustahiqList();
                    }));
                    tl.play();
                }
            }
        }, 0);
        
    }
    private void setLayoutMustahiqList(){
        if (mustahiqList.size() > max_list_show) {
            int m = mustahiqList.size()/max_list_show;
            int mx = mustahiqList.size()%max_list_show;
            if (mx > 0) {
                max_page = m + 1;
            }else{
                max_page = m;
            }
            int from, to;
            if (page == 1) {
                from = 0;
                to = max_list_show;
                bPrevMustahiq.setDisable(true);
                bNextMustahiq.setDisable(!(max_page > page));
            }else{
                from = (page-1) * max_list_show;
                to = page * max_list_show;
                if (to > mustahiqList.size()) {
                    to = mustahiqList.size();
                }
            }
            System.out.println("index from:"+from+", to:"+to);
            pagelist = mustahiqList.subList(from, to);
            lPageMustahiq.setText(page+"/"+max_page);
//           hbPage.setVisible(true);
        }else{
            bPrevMustahiq.setDisable(true);
            bNextMustahiq.setDisable(true);
            page = 1;
            lPageMustahiq.setText(1+"/"+1);
            pagelist = mustahiqList;
//             hbPage.setVisible(true);
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        Object[] obj = additionalNode.buildMustahiqList(pagelist, page, max_list_show, ap);
        VBox vbox = (VBox) obj[0];
        setActionMustahiqList((List<JFXButton>) obj[1]);
        spMustahiqList.setContent(vbox);
        spMustahiqList.setVvalue(0);
    }
    private void setActionMustahiqList(List<JFXButton> list){
        int i = 0;
        for (Mustahiq m: pagelist) {
            list.get(i).setOnAction((e) -> {
                String msg = "Hapus mustahiq bernama: "+
                        m.getNama()+" - "+m.getJenisMustahiq().getNama_jenis()+" ?";
                additionalNode.getAlert(msg, TYPE_MUSTAHIQ, null, m, null, null);
                getMustahiqList();
            });
            i++;
        }
    }

    @FXML
    private void getPrevPage(ActionEvent event) {
        if (page > 1) {
            page--;
            setLayoutMustahiqList();
            bNextMustahiq.setDisable(false);
            if (page == 1) {
                 bPrevMustahiq.setDisable(true);
            }
        }
    }

    @FXML
    private void getNextPage(ActionEvent event) {
        if (page < max_page) {
            page++;
            setLayoutMustahiqList();
             bPrevMustahiq.setDisable(false);
             if (page >= max_page) {
                 bNextMustahiq.setDisable(true);
            }
        }
    }

    
}
