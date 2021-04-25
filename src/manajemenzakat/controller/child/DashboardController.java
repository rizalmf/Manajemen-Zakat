/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import manajemenzakat.controller.additional.DoughnutChart;
import manajemenzakat.model.JenisZakat;
import manajemenzakat.model.Response;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class DashboardController implements Initializable {

    @FXML
    private VBox parent;
    @FXML
    private Label lZakatUang;
    @FXML
    private Label lZakatBeras;
    @FXML
    private Label lZakatMal;
    @FXML
    private Label lInfaq;
    @FXML
    private Label lTotal;
    @FXML
    private HBox hbox;
    private AdditionalNode additionalNode;
    private NumberFormat nf;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        HBox.setHgrow(parent, Priority.ALWAYS);
        additionalNode = new AdditionalNode();
        initiateCurr();
        initiateTotal();
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            initiateChart();
        }));
        tl.setDelay(Duration.millis(600));
        tl.play();
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
                Response response = service.getDashboardZakat();
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) {
                        double[] data = (double[]) response.getData();
                        lZakatBeras.setText(data[0]+" Liter");
                        lZakatUang.setText("Rp. "+nf.format(data[1]));
                        lZakatMal.setText("Rp. "+nf.format(data[2]));
                        lInfaq.setText("Rp. "+nf.format(data[3]));
                        double total = data[1]+data[2]+data[3];
                        lTotal.setText("Rp. "+nf.format(total));
                    }
                }));
                tl.play();
            }
        }, 0);
    }
    private void initiateChart(){
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        DoughnutChart chart = new DoughnutChart(list);
        hbox.getChildren().add(chart);
        Response response = service.getDashboardChart();
        if (!response.isStatus()) {
            return;
        }
        double[] data = (double[]) response.getData();
        List<JenisZakat> jz =(List<JenisZakat>) service.getJenisZakat().getData();
        for (int i = 0; i<data.length; i++) {
            list.add(new PieChart.Data(jz.get(i).getNama_jenis(),1));
        }
        chart.setLabelLineLength(10);
        chart.setLegendSide(Side.BOTTOM);
        chart.setTitle("Data Jumlah Minat Zakat ");
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            int j = 0;
            for (PieChart.Data d : list) {
                d.setPieValue(data[j]);
                j++;
            }
        }));
        tl.setDelay(Duration.millis(150));
        tl.play();
        
    }

}
