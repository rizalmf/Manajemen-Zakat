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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_DISTRIBUSI_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_ZAKAT;
import manajemenzakat.model.Response;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;
import manajemenzakat.print.PrintUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class LaporanController implements Initializable {

    @FXML
    private VBox parent;
    @FXML
    private Label lUangMasuk;
    @FXML
    private Label lUangKeluar;
    @FXML
    private Label lBerasMasuk;
    @FXML
    private Label lBerasKeluar;
    @FXML
    private JFXButton bPrint;
    @FXML
    private JFXButton bExcel;
    @FXML
    private JFXTextField tfOptionSearch;
    @FXML
    private ScrollPane spList;
    @FXML
    private JFXButton bPrev;
    @FXML
    private Label lPage;
    @FXML
    private JFXButton bNext;
    private boolean in_button;
    private AdditionalNode additionalNode;
    private int page, max_page, max_list_show;
    private String search;
    private List<ZakatKeluar> keluarList;
    private List<ZakatKeluar> pageList;
    private NumberFormat nf;
    private JasperPrint jPrint;
//    private double totalUang, totalBeras;
    @FXML
    private JFXButton bSearch;
    
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
        bSearch.setVisible(false);
        tfOptionSearch.setVisible(false);
//        bExcel.setVisible(false);
        tfOptionSearch.clear();
        bNext.setDisable(true);
        bPrev.setDisable(true);
        initiateCurr();
        search = "";
        getDistribusiList();
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
    public void getDistribusiList(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response r1 = service.getDashboardZakat();
                Response r2 = service.getZakatKeluarList();
                if (r2.isStatus()) {
                    Map<String, Object> map = (Map<String, Object>) r2.getData();
                    keluarList = (ArrayList<ZakatKeluar>) map.get("list");
                    Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                        page = 1;
                        setLayoutDistribusiList();
                        double[] data = (double[]) r1.getData();
                        double[] dataMinus = (double[]) map.get("total");
                        double totalUangMasuk = data[1]+data[2]+data[3];
                        lUangMasuk.setText("Rp. "+nf.format(totalUangMasuk));
                        lBerasMasuk.setText(data[0]+" Liter");
                        
                        lUangKeluar.setText("Rp. "+nf.format(dataMinus[0]));
                        lBerasKeluar.setText(dataMinus[1]+" Liter");
                    }));
                    tl.play();
                }
            }
        }, 0);
        
    }
    private void setLayoutDistribusiList(){
        if (keluarList.size() > max_list_show) {
            int m = keluarList.size()/max_list_show;
            int mx = keluarList.size()%max_list_show;
            if (mx > 0) {
                max_page = m + 1;
            }else{
                max_page = m;
            }
            int from, to;
            if (page == 1) {
                from = 0;
                to = max_list_show;
                bPrev.setDisable(true);
                bNext.setDisable(!(max_page > page));
            }else{
                from = (page-1) * max_list_show;
                to = page * max_list_show;
                if (to > keluarList.size()) {
                    to = keluarList.size();
                }
            }
            System.out.println("index from:"+from+", to:"+to);
            pageList = keluarList.subList(from, to);
            lPage.setText(page+"/"+max_page);
        }else{
            bPrev.setDisable(true);
            bNext.setDisable(true);
            page = 1;
            lPage.setText(1+"/"+1);
            pageList = keluarList;
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        Object[] obj = additionalNode.buildDistribusiList(pageList, page, max_list_show, ap);
        VBox vbox = (VBox) obj[0];
        setActionDistribusiList((List<JFXButton>) obj[1]);
        spList.setContent(vbox);
        spList.setVvalue(0);
    }
    private void setActionDistribusiList(List<JFXButton> list){
        int i = 0;
        for (ZakatKeluar z: pageList) {
            list.get(i).setOnAction((e) -> {
                String msg = "Hapus distribusi zakat nota "+
                        z.getKode()+" ?";
                additionalNode.getAlert(msg, TYPE_DISTRIBUSI_ZAKAT, null, null, null, z);
                getDistribusiList();
            });
            i++;
        }
    }
    @FXML
    private void optionPrint(ActionEvent event) {
        try {
            PrintUtil print = new PrintUtil();
            jPrint = print.getLaporanDistribusiZakat(keluarList);
            JasperPrintManager.printReport(jPrint, true);
        } catch (Exception ex) {
            Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void optionExcel(ActionEvent event) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Pilih folder export excel");
        Stage thisStage = (Stage) parent.getScene().getWindow();
        File folder = fc.showDialog(thisStage);
        if (folder != null) {
            try {
                PrintUtil print = new PrintUtil();
                jPrint = print.getLaporanDistribusiZakat(keluarList);
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jPrint));
                exporter.setExporterOutput(
                        new SimpleOutputStreamExporterOutput(folder.getAbsolutePath()
                                +"laporan distribusi zakat-"+sdf.format(new Date())+".xls"));
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
                Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            search = tfOptionSearch.getText();
            getDistribusiList();
        }
    }

    @FXML
    private void optionSearch(ActionEvent event) {
        search = tfOptionSearch.getText();
        getDistribusiList();
    }

    @FXML
    private void getPrevPage(ActionEvent event) {
        if (page > 1) {
            page--;
            setLayoutDistribusiList();
            bNext.setDisable(false);
            if (page == 1) {
                 bPrev.setDisable(true);
            }
        }
    }

    @FXML
    private void getNextPage(ActionEvent event) {
        if (page < max_page) {
            page++;
            setLayoutDistribusiList();
             bPrev.setDisable(false);
             if (page >= max_page) {
                 bNext.setDisable(true);
            }
        }
    }
    
}
