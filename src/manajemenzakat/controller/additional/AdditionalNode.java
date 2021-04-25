/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.additional;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import manajemenzakat.ManajemenZakat;
import manajemenzakat.controller.ConfirmController;
import manajemenzakat.controller.LoginController;
import manajemenzakat.controller.child.MustahiqAddController;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_ADMINISTRATOR;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL_ADD;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL_LIST;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_MUSTAHIQ_ADD;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_MUSTAHIQ_LIST;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_ZAKAT_CHILD;
import manajemenzakat.controller.child.AmilAddController;
import manajemenzakat.controller.child.AmilController;
import manajemenzakat.controller.child.LaporanController;
import manajemenzakat.controller.child.ZakatTerimaController;
import manajemenzakat.model.Log;
import manajemenzakat.model.Mustahiq;
import manajemenzakat.model.User;
import manajemenzakat.model.Zakat;
import manajemenzakat.model.ZakatKeluar;
import manajemenzakat.print.PrintUtil;
import manajemenzakat.util.Session;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

/**
 *
 * @author RIZAL
 */
public class AdditionalNode {
    private JasperPrint jPrint;
    public void openLogin(VBox parent){
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (ActionEvent e) -> {
            FXMLLoader fxmlLoader = new FXMLLoader(ManajemenZakat.class.getResource("views/login.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Stage stage = new Stage();
            stage.setOnCloseRequest((WindowEvent ev) -> {
                System.exit(0);
            });
            stage.setTitle("Manajemen Zakat - Login");
            stage.getIcons().add(new Image(ManajemenZakat.class.getResourceAsStream("favicon.png")));
            Scene scene = new Scene(root1);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
            Stage thisStage = (Stage) parent.getScene().getWindow();
            thisStage.close();
        }));
        tl.setCycleCount(1);
        tl.play();
    }
    public void openMenu(String load, HBox row){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream(load));
            row.setVisible(true);
            row.getChildren().clear();
            row.getChildren().add(p);   
        } catch (IOException e) {
            System.out.println("not found e:"+e.getMessage());
        }
    }
    public void getAlert(String msg, int type, Zakat zakat, Mustahiq mustahiq, User user, ZakatKeluar zakatKeluar){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ManajemenZakat.class.getResource("views/confirm.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            ConfirmController confirmController = fxmlLoader.getController();
            confirmController.setType(type);
            confirmController.setMustahiq(mustahiq);
            confirmController.setZakat(zakat);
            confirmController.setUser(user);
            confirmController.setZakatKeluar(zakatKeluar);
            confirmController.setMsg(msg);
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOnCloseRequest((WindowEvent event) -> {
                stage.close();
            });
            Scene scene = new Scene(root1);
            stage.getIcons().add(new Image(ManajemenZakat.class.getResourceAsStream("favicon.png")));
            stage.setTitle("Konfirmasi");
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
        }
        
    }
    
    private int getNumber(int page, int max_list_show){
        return ((page-1)*max_list_show)+1;
    }
    private void buildGridEmpty(GridPane grid){
        Label lBlank = new Label("Data tidak ditemukan/kosong");
        lBlank.setId("lBodyEmpty");
        lBlank.setPrefWidth(630);
        VBox vb = new VBox(lBlank);
        vb.setFillWidth(true);
        vb.setId("hbBodyEmpty");
        vb.setPadding(new Insets(0));
        grid.add(vb, 0, 1);
    }
    
    //MUSTAHIQ
    public Object[] buildMustahiqList(List<Mustahiq> list, int page, 
            int max_list_show, AnchorPane ap){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        DropShadow ds = new DropShadow(2, Color.BLACK);
        grid.setEffect(ds);
        grid.setVgap(0);
        grid.setHgap(15);
        grid.setStyle("-fx-alignment: TOP_CENTER;");
//            grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        grid.add(buildTableMustahiqHead(), 0, 0);
        List<JFXButton> buttons;
        if (list.isEmpty()) {
            buildGridEmpty(grid);
            buttons = new ArrayList<>();
        }else{
            buttons = buildGridMustahiq(grid, list, page, max_list_show, ap);
        }
        VBox vbox = new VBox(grid);
        vbox.setStyle("-fx-alignment: TOP_CENTER;");
        vbox.setPadding(new Insets(0));
        return new Object[]{vbox, buttons};
    }
    private List<JFXButton> buildGridMustahiq(GridPane grid, List<Mustahiq> list, 
            int page, int max_list_show, AnchorPane ap) {
        int row = 1;
        int col = 0;
        int i = (page == 1) ? 1 : getNumber(page, max_list_show);
        List<JFXButton> actionList = new ArrayList<>();
        for (Mustahiq m : list) {
            Label lNo = new Label((i++)+".");
            lNo.setId("lBody");
            lNo.setPrefWidth(80);
            lNo.setMinWidth(50);
            
            Label lNama = new Label(m.getNama());
            lNama.setId("lBodyLeft");
            lNama.setPrefWidth(450);
            lNama.setMinWidth(100);
            
            Label lAlamat = new Label(m.getAlamat());
            lAlamat.setId("lBodyLeft");
            lAlamat.setPrefWidth(630);
            
            Label lJenis = new Label(m.getJenisMustahiq().getNama_jenis());
            lJenis.setId("lBody");
            lJenis.setPrefWidth(450);
            lJenis.setMinWidth(100);
            
            FontAwesomeIconView vEdit = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            vEdit.setFill(Paint.valueOf("#ffffff"));
            JFXButton bEdit = new JFXButton("Ubah", vEdit);
            bEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bEdit.setTooltip(new Tooltip("Ubah"));
            bEdit.setId("bBodyEdit");
            bEdit.setPrefWidth(28);
            bEdit.setMinWidth(28);
            bEdit.setMinHeight(28);
            bEdit.setOnAction((e) -> {
                editMustahiq(m, ap);
            });
            
            FontAwesomeIconView vDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            vDelete.setFill(Paint.valueOf("#ffffff"));
            JFXButton bDelete = new JFXButton("Hapus", vDelete);
            bDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bDelete.setTooltip(new Tooltip("Hapus"));
            bDelete.setId("bBodyDelete");
            bDelete.setPrefWidth(28);
            bDelete.setMinWidth(28);
            bDelete.setMinHeight(28);
           
            actionList.add(bDelete);
            
            HBox hbAct = new HBox(8, bEdit, bDelete);
            hbAct.setStyle("-fx-alignment: CENTER_LEFT");
            hbAct.setPrefWidth(200);
            hbAct.setMinWidth(100);
            
            
            HBox hbox = new HBox(lNo, lNama, lAlamat, lJenis, hbAct);
            hbox.setFillHeight(false);
            hbox.setId("hbBody");
            hbox.setPadding(new Insets(0));
//            hbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
            grid.add(hbox, col, row++);
        }
        return actionList;
    }
    private HBox buildTableMustahiqHead(){
        Label lNo = new Label("NO");
        lNo.setId("lHead");
        lNo.setPrefWidth(80);
        lNo.setMinWidth(50);

        Label lNama = new Label("NAMA");
        lNama.setId("lHead");
        lNama.setPrefWidth(450);
        lNama.setMinWidth(100);

        Label lAlamat = new Label("ALAMAT");
        lAlamat.setId("lHead");
        lAlamat.setPrefWidth(630);

        Label lJenis = new Label("JENIS MUSTAHIQ");
        lJenis.setId("lHead");
        lJenis.setPrefWidth(450);
        lJenis.setMinWidth(100);
        
        Label lAksi = new Label("AKSI");
        lAksi.setId("lHead");
        lAksi.setPrefWidth(200);
        lAksi.setMinWidth(100);
        
        HBox hbox = new HBox(lNo, lNama, lAlamat, lJenis,lAksi);
        hbox.setId("hbHead");
        hbox.setFillHeight(false);
//          hbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        hbox.setPadding(new Insets(0));
        return hbox;
    }
    private void editMustahiq(Mustahiq mustahiq, AnchorPane ap){
        ap.getChildren().get(ROW_MUSTAHIQ_LIST).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_MUSTAHIQ_ADD);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/mustahiqAdd.fxml"));
            MustahiqAddController mustahiqAddController = fxmlLoader.getController();
            mustahiqAddController.setMustahiq(mustahiq);
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (IOException e) {
        }
        row.setOpacity(1);
        row.setVisible(true);
    }
    
    //ZAKAT
    public List<JFXButton> buildZakatParent(AnchorPane ap){
        String color = "#ffffff";
        String size = "85";
        FontAwesomeIconView vTerima = new FontAwesomeIconView(FontAwesomeIcon.SHOPPING_BAG);
        vTerima.setFill(Paint.valueOf(color));
        vTerima.setSize(size);
        FontAwesomeIconView vList = new FontAwesomeIconView(FontAwesomeIcon.BOOK);
        vList.setFill(Paint.valueOf(color));
        vList.setSize(size);
        FontAwesomeIconView vDist = new FontAwesomeIconView(FontAwesomeIcon.EXCHANGE);
        vDist.setFill(Paint.valueOf(color));
        vDist.setSize(size);
        JFXButton bTerimaZakat = new JFXButton("Penerimaan Zakat", vTerima);
        bTerimaZakat.setOnAction((e) -> {
            ap.getChildren().get(ROW_ZAKAT).setVisible(false);
            HBox row = (HBox) ap.getChildren().get(ROW_ZAKAT_CHILD);
            openMenu("views/zakatTerima.fxml", row);
        });
        JFXButton bListZakat = new JFXButton("Buku Zakat", vList);
        bListZakat.setOnAction((e) -> {
            ap.getChildren().get(ROW_ZAKAT).setVisible(false);
            HBox row = (HBox) ap.getChildren().get(ROW_ZAKAT_CHILD);
            openMenu("views/zakatBuku.fxml", row);
        });
        JFXButton bDistZakat = new JFXButton("Distribusi Zakat", vDist);
        bDistZakat.setOnAction((e) -> {
            ap.getChildren().get(ROW_ZAKAT).setVisible(false);
            HBox row = (HBox) ap.getChildren().get(ROW_ZAKAT_CHILD);
            openMenu("views/zakatDistribusi.fxml", row);
        });
        return new ArrayList<>(Arrays.asList(bTerimaZakat, bListZakat, bDistZakat));
    }
    public void zakatContentType(boolean vertx, List<JFXButton> list){
        list.forEach((b) -> {
            if (vertx) { 
                b.setContentDisplay(ContentDisplay.LEFT);
                b.setPrefWidth(350);
                b.setPrefHeight(120);
                b.setId("zakatparent");
            }
            else{
                b.setContentDisplay(ContentDisplay.TOP);
                b.setPrefWidth(250);
                b.setPrefHeight(170);
                b.setMaxHeight(200);
                b.setId("zakatparentc");
            }
        });
    }
    public Object[] buildZakatList(List<Zakat> list, int page, 
            int max_list_show, AnchorPane ap){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        DropShadow ds = new DropShadow(2, Color.BLACK);
        grid.setEffect(ds);
        grid.setVgap(0);
        grid.setHgap(15);
        grid.setStyle("-fx-alignment: TOP_CENTER;");
//            grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        grid.add(buildTableZakatHead(), 0, 0);
        List<JFXButton> buttons;
        if (list.isEmpty()) {
            buildGridEmpty(grid);
            buttons = new ArrayList<>();
        }else{
            buttons = buildGridZakat(grid, list, page, max_list_show, ap);
        }
        VBox vbox = new VBox(grid);
        vbox.setStyle("-fx-alignment: TOP_CENTER;");
        vbox.setPadding(new Insets(0));
        return new Object[]{vbox, buttons};
    }
    private List<JFXButton> buildGridZakat(GridPane grid, List<Zakat> list, 
            int page, int max_list_show, AnchorPane ap) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) nf).setDecimalFormatSymbols(dfs);
        nf.setMaximumFractionDigits(0);
        int row = 1;
        int col = 0;
        int i = (page == 1) ? 1 : getNumber(page, max_list_show);
        List<JFXButton> actionList = new ArrayList<>();
        int role = Session.getUser().getRole();
        for (Zakat z : list) {
            Label lNo = new Label((i++)+".");
            lNo.setId("lBody");
            lNo.setPrefWidth(80);
            lNo.setMinWidth(50);
            
            Label lNama = new Label(z.getNama());
            lNama.setId("lBodyLeft");
            lNama.setPrefWidth(250);
            lNama.setMinWidth(90);
            
            Label lTanggal = new Label(z.getTanggal());
            lTanggal.setId("lBodyLeft");
            lTanggal.setPrefWidth(220);
            lTanggal.setMinWidth(90);
            
            Label lJiwa = new Label(z.getJiwa()+" Orang");
            lJiwa.setId("lBodyLeft");
            lJiwa.setPrefWidth(150);
            lJiwa.setMinWidth(80);
            String nominal;
            nominal = (z.getJenisZakat().getIs_money().equalsIgnoreCase("Y")) ? 
                    z.getJenisZakat().getSatuan()+" "+nf.format(z.getNominal()) 
                    : z.getNominal()+" "+z.getJenisZakat().getKeterangan();
            Label lNominal = new Label(nominal);
            lNominal.setId("lBodyLeft");
            lNominal.setPrefWidth(240);
            lNominal.setMinWidth(100);
            
            Label lJenis = new Label(z.getJenisZakat().getNama_jenis());
            lJenis.setId("lBody");
            lJenis.setPrefWidth(300);
            lJenis.setMinWidth(100);
            
            JFXButton bEdit = null;
            if (role == ROLE_ADMINISTRATOR) {
                FontAwesomeIconView vEdit = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
                vEdit.setFill(Paint.valueOf("#ffffff"));
                bEdit = new JFXButton("Ubah", vEdit);
                bEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                bEdit.setTooltip(new Tooltip("Ubah"));
                bEdit.setId("bBodyEdit");
                bEdit.setPrefWidth(28);
                bEdit.setMinWidth(28);
                bEdit.setMinHeight(28);
                bEdit.setOnAction((e) -> {
                    editZakat(z, ap);
                });
            }
            FontAwesomeIconView vPrint = new FontAwesomeIconView(FontAwesomeIcon.PRINT);
            vPrint.setFill(Paint.valueOf("#ffffff"));
            JFXButton bPrint = new JFXButton("Print", vPrint);
            bPrint.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bPrint.setTooltip(new Tooltip("Print"));
            bPrint.setId("bBodyPrint");
            bPrint.setPrefWidth(28);
            bPrint.setMinWidth(28);
            bPrint.setMinHeight(28);
            bPrint.setOnAction((e) -> {
                printZakat(z);
            });
            
            FontAwesomeIconView vDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            vDelete.setFill(Paint.valueOf("#ffffff"));
            JFXButton bDelete = new JFXButton("Hapus", vDelete);
            bDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bDelete.setTooltip(new Tooltip("Hapus"));
            bDelete.setId("bBodyDelete");
            bDelete.setPrefWidth(28);
            bDelete.setMinWidth(28);
            bDelete.setMinHeight(28);
            actionList.add(bDelete);
            
            HBox hbAct;
            if (role == ROLE_ADMINISTRATOR) {
                hbAct = new HBox(5, bEdit, bPrint, bDelete);
            }else{
                hbAct = new HBox(8, bPrint, bDelete);
            }
             
            hbAct.setStyle("-fx-alignment: CENTER_LEFT");
            hbAct.setPrefWidth(200);
            hbAct.setMinWidth(100);
            
            
            HBox hbox = new HBox(lNo, lTanggal, lNama, lJiwa, lNominal, 
                    lJenis, hbAct);
            
            hbox.setFillHeight(false);
            hbox.setId("hbBody");
            hbox.setPadding(new Insets(0));
            grid.add(hbox, col, row++);
        }
        return actionList;
    }
    private HBox buildTableZakatHead(){
        Label lNo = new Label("NO");
        lNo.setId("lHead");
        lNo.setPrefWidth(80);
        lNo.setMinWidth(50);

        Label lNama = new Label("NAMA");
        lNama.setId("lHead");
        lNama.setPrefWidth(250);
        lNama.setMinWidth(90);

        Label lTanggal = new Label("TANGGAL");
        lTanggal.setId("lHead");
        lTanggal.setPrefWidth(220);
        lTanggal.setMinWidth(90);
        
        Label lJiwa = new Label("JIWA");
        lJiwa.setId("lHead");
        lJiwa.setPrefWidth(150);
        lJiwa.setMinWidth(80);
        
        Label lNominal = new Label("NOMINAL");
        lNominal.setId("lHead");
        lNominal.setPrefWidth(240);
        lNominal.setMinWidth(100);
        
        Label lJenis = new Label("JENIS ZAKAT");
        lJenis.setId("lHead");
        lJenis.setPrefWidth(300);
        lJenis.setMinWidth(100);
        
        Label lAksi = new Label("AKSI");
        lAksi.setId("lHead");
        lAksi.setPrefWidth(200);
        lAksi.setMinWidth(100);
        
        HBox hbox = new HBox(lNo, lTanggal, lNama, lJiwa, lNominal, lJenis, lAksi);
        hbox.setId("hbHead");
        hbox.setFillHeight(false);
        hbox.setPadding(new Insets(0));
        return hbox;
    }
    private void editZakat(Zakat zakat, AnchorPane ap){
//        ap.getChildren().get(ROW_).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_ZAKAT_CHILD);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/zakatTerima.fxml"));
            ZakatTerimaController zakatTerimaController = fxmlLoader.getController();
            zakatTerimaController.setZakat(zakat);
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (IOException e) {
        }
        row.setVisible(true);
    }
    private void printZakat(Zakat zakat){
        try {
            PrintUtil print = new PrintUtil();
            jPrint = print.getInvoicePenerimaan(zakat);
            JasperPrintManager.printReport(jPrint, true);
        } catch (Exception ex) {
            Logger.getLogger(LaporanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //LOG
    public VBox buildLogList(List<Log> list, int page, 
            int max_list_show, AnchorPane ap){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        DropShadow ds = new DropShadow(2, Color.BLACK);
        grid.setEffect(ds);
        grid.setVgap(0);
        grid.setHgap(15);
        grid.setStyle("-fx-alignment: TOP_CENTER;");
//            grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        grid.add(buildTableLogHead(), 0, 0);
        buildGridEmpty(grid);

        buildGridLog(grid, list, page, max_list_show, ap);
        
        VBox vbox = new VBox(grid);
        vbox.setStyle("-fx-alignment: TOP_CENTER;");
        vbox.setPadding(new Insets(0));
        return vbox;
    }
    private void buildGridLog(GridPane grid, List<Log> list, 
            int page, int max_list_show, AnchorPane ap) {
        int row = 1;
        int col = 0;
        int i = (page == 1) ? 1 : getNumber(page, max_list_show);
        for (Log l : list) {
            Label lNo = new Label((i++)+".");
            lNo.setId("lBody");
            lNo.setPrefWidth(80);
            lNo.setMinWidth(50);
            
            Label lWaktu = new Label(l.getWaktu());
            lWaktu.setId("lBodyLeft");
            lWaktu.setPrefWidth(450);
            lWaktu.setMinWidth(100);
            
            Label lText = new Label(l.getText());
            lText.setId("lBodyLeft");
            lText.setPrefWidth(830);
            lText.setMinWidth(150);
            
            HBox hbox = new HBox(lNo, lWaktu, lText);
            hbox.setFillHeight(false);
            hbox.setId("hbBody");
            hbox.setPadding(new Insets(0));
            grid.add(hbox, col, row++);
        }
    }
    private HBox buildTableLogHead(){
        Label lNo = new Label("NO");
        lNo.setId("lHead");
        lNo.setPrefWidth(80);
        lNo.setMinWidth(50);

        Label lWaktu = new Label("Waktu");
        lWaktu.setId("lHeadc");
        lWaktu.setPrefWidth(350);
        lWaktu.setMinWidth(100);

        Label lText = new Label("Aktifitas");
        lText.setId("lHeadc");
        lText.setPrefWidth(830);
        lText.setMinWidth(150);
        
        HBox hbox = new HBox(lNo, lWaktu, lText);
        hbox.setId("hbHead");
        hbox.setFillHeight(false);
        hbox.setPadding(new Insets(0));
        return hbox;
    }
    
    //USER AMIL
    public Object[] buildUserList(List<User> list, int page, 
            int max_list_show, AnchorPane ap){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        DropShadow ds = new DropShadow(2, Color.BLACK);
        grid.setEffect(ds);
        grid.setVgap(0);
        grid.setHgap(15);
        grid.setStyle("-fx-alignment: TOP_CENTER;");
//            grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        grid.add(buildTableUserHead(), 0, 0);
        List<JFXButton> buttons;
        if (list.isEmpty()) {
            buildGridEmpty(grid);
            buttons = new ArrayList<>();
        }else{
            buttons = buildGridUser(grid, list, page, max_list_show, ap);
        }
        VBox vbox = new VBox(grid);
        vbox.setStyle("-fx-alignment: TOP_CENTER;");
        vbox.setPadding(new Insets(0));
        return new Object[]{vbox, buttons};
    }
    private List<JFXButton> buildGridUser(GridPane grid, List<User> list, 
            int page, int max_list_show, AnchorPane ap) {
        int row = 1;
        int col = 0;
        int i = (page == 1) ? 1 : getNumber(page, max_list_show);
        List<JFXButton> actionList = new ArrayList<>();
        for (User u : list) {
            Label lNo = new Label((i++)+".");
            lNo.setId("lBody");
            lNo.setPrefWidth(80);
            lNo.setMinWidth(50);
            
            Label lNama = new Label(u.getNama());
            lNama.setId("lBodyLeft");
            lNama.setPrefWidth(450);
            lNama.setMinWidth(120);
            
            Tooltip tooltip = new Tooltip();
            VBox tBox = new VBox();
            if (u.getPath() != null) {
                try {
                    Image img =new Image(
                        new FileInputStream(new File(u.getPath())                        )
                    );
                    ImageView view = new ImageView(img);
                    view.setFitWidth(100);
                    view.setFitHeight(120);
                    tBox.getChildren().add(view);
                    tooltip.setGraphic(tBox);
                } catch (Exception e) {
                    tooltip.setText("No Photo");
                }
            }else tooltip.setText("No Photo");
            lNama.setTooltip(tooltip);
            
            Label lAlamat = new Label(u.getAlamat());
            lAlamat.setId("lBodyLeft");
            lAlamat.setPrefWidth(500);
            lAlamat.setMinWidth(100);
            Label lNoTelp = new Label((u.getNo_telp().isEmpty())? "-" : u.getNo_telp());
            lNoTelp.setId("lBody");
            lNoTelp.setPrefWidth(450);
            lNoTelp.setMinWidth(100);
            
            FontAwesomeIconView vPeek = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
            vPeek.setFill(Paint.valueOf("#ffffff"));
            JFXButton bPeek = new JFXButton("Lihat", vPeek);
            bPeek.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bPeek.setTooltip(new Tooltip("Lihat"));
            bPeek.setId("bBodyPeek");
            bPeek.setPrefWidth(28);
            bPeek.setMinWidth(28);
            bPeek.setMinHeight(28);
            bPeek.setOnAction((e) -> {
                peekUser(u, ap);
            });
            
            FontAwesomeIconView vEdit = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            vEdit.setFill(Paint.valueOf("#ffffff"));
            JFXButton bEdit = new JFXButton("Ubah", vEdit);
            bEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bEdit.setTooltip(new Tooltip("Ubah"));
            bEdit.setId("bBodyEdit");
            bEdit.setPrefWidth(28);
            bEdit.setMinWidth(28);
            bEdit.setMinHeight(28);
            bEdit.setOnAction((e) -> {
                editUser(u, ap);
            });
            
            FontAwesomeIconView vDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            vDelete.setFill(Paint.valueOf("#ffffff"));
            JFXButton bDelete = new JFXButton("Hapus", vDelete);
            bDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bDelete.setTooltip(new Tooltip("Hapus"));
            bDelete.setId("bBodyDelete");
            bDelete.setPrefWidth(28);
            bDelete.setMinWidth(28);
            bDelete.setMinHeight(28);
            
            actionList.add(bDelete);
            
            HBox hbAct = new HBox(5, bPeek, bEdit, bDelete);
            hbAct.setStyle("-fx-alignment: CENTER_LEFT");
            hbAct.setPrefWidth(200);
            hbAct.setMinWidth(100);
            
            
            HBox hbox = new HBox(lNo, lNama, lAlamat, lNoTelp, hbAct);
            
            hbox.setFillHeight(false);
            hbox.setId("hbBody");
            hbox.setPadding(new Insets(0));
            grid.add(hbox, col, row++);
        }
        return actionList;
    }
    private HBox buildTableUserHead(){
        Label lNo = new Label("NO");
        lNo.setId("lHead");
        lNo.setPrefWidth(80);
        lNo.setMinWidth(50);

        Label lNama = new Label("NAMA");
        lNama.setId("lHead");
        lNama.setPrefWidth(450);
        lNama.setMinWidth(120);

        Label lAlamat = new Label("ALAMAT");
        lAlamat.setId("lHead");
        lAlamat.setPrefWidth(500);
        lAlamat.setMinWidth(100);

        Label lJenis = new Label("NO TELP");
        lJenis.setId("lHead");
        lJenis.setPrefWidth(450);
        lJenis.setMinWidth(100);
        
        Label lAksi = new Label("AKSI");
        lAksi.setId("lHead");
        lAksi.setPrefWidth(200);
        lAksi.setMinWidth(100);
        
        HBox hbox = new HBox(lNo, lNama, lAlamat, lJenis,lAksi);
        hbox.setId("hbHead");
        hbox.setFillHeight(false);
        hbox.setPadding(new Insets(0));
        return hbox;
    }
    private void editUser(User user, AnchorPane ap){
        ap.getChildren().get(ROW_AMIL_LIST).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_AMIL_ADD);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/amilAdd.fxml"));
            AmilAddController amilAddController = fxmlLoader.getController();
            amilAddController.setUser(user);
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (IOException e) {
        }
        row.setVisible(true);
    }
    private void peekUser(User user, AnchorPane ap){
        ap.getChildren().get(ROW_AMIL_LIST).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_AMIL);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/amil.fxml"));
            AmilController amilController = fxmlLoader.getController();
            amilController.setUser(user);
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (IOException e) {
        }
        row.setVisible(true);
    }
    
    //LAPORAN    
    public Object[] buildDistribusiList(List<ZakatKeluar> list, int page, 
            int max_list_show, AnchorPane ap){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        DropShadow ds = new DropShadow(2, Color.BLACK);
        grid.setEffect(ds);
        grid.setVgap(0);
        grid.setHgap(15);
        grid.setStyle("-fx-alignment: TOP_CENTER;");
//            grid.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
        grid.add(buildTableDistribusiHead(), 0, 0);
        List<JFXButton> buttons;
        if (list.isEmpty()) {
            buildGridEmpty(grid);
            buttons = new ArrayList<>();
        }else{
            buttons = buildGridDistribusi(grid, list, page, max_list_show, ap);
        }
        VBox vbox = new VBox(grid);
        vbox.setStyle("-fx-alignment: TOP_CENTER;");
        vbox.setPadding(new Insets(0));
        return new Object[]{vbox, buttons};
    }
    private List<JFXButton> buildGridDistribusi(GridPane grid, List<ZakatKeluar> list, 
            int page, int max_list_show, AnchorPane ap) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) nf).setDecimalFormatSymbols(dfs);
        nf.setMaximumFractionDigits(0);
        int row = 1;
        int col = 0;
        int i = (page == 1) ? 1 : getNumber(page, max_list_show);
        List<JFXButton> actionList = new ArrayList<>();
        int role = Session.getUser().getRole();
        for (ZakatKeluar z : list) {
            Label lNo = new Label((i++)+".");
            lNo.setId("lBody");
            lNo.setPrefWidth(80);
            lNo.setMinWidth(50);
            
            Label lTanggal = new Label(z.getTanggal());
            lTanggal.setId("lBodyLeft");
            lTanggal.setPrefWidth(230);
            lTanggal.setMinWidth(90);
            
            Label lKode = new Label(z.getKode());
            lKode.setId("lBodyLeft");
            lKode.setPrefWidth(220);
            lKode.setMinWidth(90);
            
            Label lAmil = new Label(z.getAmil_nama());
            lAmil.setId("lBodyLeft");
            lAmil.setPrefWidth(220);
            lAmil.setMinWidth(90);
            
            Label lMustahiq = new Label(z.getMustahiq_nama());
            lMustahiq.setId("lBodyLeft");
            lMustahiq.setPrefWidth(220);
            lMustahiq.setMinWidth(90);
            
            String nominal = (z.getIs_money().equalsIgnoreCase("Y")) ? 
                    "Rp. "+nf.format(z.getNominal()) 
                    : z.getNominal()+" Liter";
            Label lNominal = new Label(nominal);
            lNominal.setId("lBodyLeft");
            lNominal.setPrefWidth(200);
            lNominal.setMinWidth(100);
            
            FontAwesomeIconView vEdit = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
            vEdit.setFill(Paint.valueOf("#ffffff"));
            JFXButton bEdit = new JFXButton("Ubah", vEdit);
            bEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bEdit.setTooltip(new Tooltip("Ubah"));
            bEdit.setId("bBodyEdit");
            bEdit.setPrefWidth(28);
            bEdit.setMinWidth(28);
            bEdit.setMinHeight(28);
            bEdit.setOnAction((e) -> {
//                editDistribusi(z, ap);
            });
            
            FontAwesomeIconView vDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            vDelete.setFill(Paint.valueOf("#ffffff"));
            JFXButton bDelete = new JFXButton("Hapus", vDelete);
            bDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            bDelete.setTooltip(new Tooltip("Hapus"));
            bDelete.setId("bBodyDelete");
            bDelete.setPrefWidth(28);
            bDelete.setMinWidth(28);
            bDelete.setMinHeight(28);
            actionList.add(bDelete);
            
            HBox hbAct = new HBox(5, bDelete);
             
            hbAct.setStyle("-fx-alignment: CENTER_LEFT");
            hbAct.setPrefWidth(200);
            hbAct.setMinWidth(100);
            
            
            HBox hbox = new HBox(lNo, lTanggal, lKode, lAmil, lMustahiq, lNominal, 
                     hbAct);
            
            hbox.setFillHeight(false);
            hbox.setId("hbBody");
            hbox.setPadding(new Insets(0));
            grid.add(hbox, col, row++);
        }
        return actionList;
    }
    private HBox buildTableDistribusiHead(){
        Label lNo = new Label("NO");
        lNo.setId("lHead");
        lNo.setPrefWidth(80);
        lNo.setMinWidth(50);

        Label lTanggal = new Label("TANGGAL");
        lTanggal.setId("lHead");
        lTanggal.setPrefWidth(230);
        lTanggal.setMinWidth(90);
        
        Label lKode = new Label("NOTA");
        lKode.setId("lHead");
        lKode.setPrefWidth(220);
        lKode.setMinWidth(90);
        
        Label lAmil = new Label("AMIL");
        lAmil.setId("lHead");
        lAmil.setPrefWidth(220);
        lAmil.setMinWidth(90);

        Label lMustahiq = new Label("MUSTAHIQ");
        lMustahiq.setId("lHead");
        lMustahiq.setPrefWidth(220);
        lMustahiq.setMinWidth(90);
        
        Label lNominal = new Label("NOMINAL");
        lNominal.setId("lHead");
        lNominal.setPrefWidth(200);
        lNominal.setMinWidth(100);
                
        Label lAksi = new Label("AKSI");
        lAksi.setId("lHead");
        lAksi.setPrefWidth(200);
        lAksi.setMinWidth(100);
        
        HBox hbox = new HBox(lNo, lTanggal, lKode, lAmil, lMustahiq, lNominal,  lAksi);
        hbox.setId("hbHead");
        hbox.setFillHeight(false);
        hbox.setPadding(new Insets(0));
        return hbox;
    }
    private void editDistribusi(ZakatKeluar zakatKeluar, AnchorPane ap){
        ap.getChildren().get(ROW_AMIL_LIST).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_AMIL_ADD);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent p = fxmlLoader.load(ManajemenZakat.class.getResourceAsStream("views/amilAdd.fxml"));
//            AmilAddController amilAddController = fxmlLoader.getController();
//            amilAddController.setUser(user);
            row.getChildren().clear();
            row.getChildren().add(p);
        } catch (Exception e) {
        }
        row.setVisible(true);
    }
}
