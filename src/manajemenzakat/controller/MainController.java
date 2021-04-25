/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller;

import com.fxexperience.javafx.animation.FadeInUpBigTransition;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_ADMINISTRATOR;
import manajemenzakat.model.Log;
import manajemenzakat.model.Response;
import manajemenzakat.service.Service;
import manajemenzakat.service.impl.ServiceJdbc;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class MainController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private String VERSION;
    private final String APP_NAME = "Manajemen Zakat ";
    private LoginController loginController;
    @FXML
    private Button bWinState;
    @FXML
    private AnchorPane ap;
    @FXML
    private Button bDashboard;
    @FXML
    private Button bZakat;
    @FXML
    private Button bMustahiq;
    @FXML
    private Button bLaporan;
    @FXML
    private Button bAmilZakat;
    @FXML
    private HBox rowDashboard;
    @FXML
    private JFXButton bVersion;
    @FXML
    private VBox vbNotifParent;
    @FXML
    private VBox vbNotifChild;
    @FXML
    private JFXButton bRefresh;
    @FXML
    private JFXButton bNotif;
    @FXML
    private Button bFloat;
    @FXML
    private HBox rowMustahiqList;
    @FXML
    private HBox rowMustahiqAdd;
    @FXML
    private AnchorPane apChild;
    @FXML
    private HBox rowZakat;
    @FXML
    private HBox rowZakatChild;
    @FXML
    private HBox rowAmil;
    @FXML
    private Button bFloatAdmin;
    @FXML
    private HBox rowAmilList;
    @FXML
    private HBox rowAmilAdd;
    @FXML
    private HBox rowLaporan;
    public void setLoginController(LoginController loginController){
        this.loginController = loginController;
    }
    public static Service service;
    private AdditionalNode additionalNode;
    private List<JFXButton> zakatMenu;
    private boolean isWide;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        VERSION = "v1.0.3 rev d";
        isWide = true;
        additionalNode = new AdditionalNode();
        if (service == null) {
//            service = new ServiceTemp();
            service = new ServiceJdbc();
            service.create();
        }
        initiateNode();
    }    

    
    @FXML
    private void openDashboard(ActionEvent event) {
        if (rowDashboard.isVisible()) {
            return;
        }
        chooseBar(bDashboard, rowDashboard);
        additionalNode.openMenu("views/dashboard.fxml", rowDashboard);
    }
    
    @FXML
    private void openZakat(ActionEvent event) {
        if (rowZakat.isVisible()) {
            return;
        }
        chooseBar(bZakat, rowZakat);
    }

    @FXML
    private void openMustahiq(ActionEvent event) {
        if (rowMustahiqList.isVisible()) {
            return;
        }
        chooseBar(bMustahiq, rowMustahiqList);
        additionalNode.openMenu("views/mustahiq.fxml", rowMustahiqList);
    }

    @FXML
    private void openLaporan(ActionEvent event) {
        if (rowLaporan.isVisible()) {
            return;
        }
        chooseBar(bLaporan, rowLaporan);
        additionalNode.openMenu("views/laporan.fxml", rowLaporan);
    }

    @FXML
    private void openAmilZakat(ActionEvent event) {
        if (Session.getUser().getRole() == ROLE_ADMINISTRATOR) {
            if (rowAmilList.isVisible()) {
                return;
            }
            chooseBar(bAmilZakat, rowAmilList);
            additionalNode.openMenu("views/amilList.fxml", rowAmilList);
        }else{
            if (rowAmil.isVisible()) {
                return;
            }
            chooseBar(bAmilZakat, rowAmil);
            additionalNode.openMenu("views/amil.fxml", rowAmil);
        }
    }
    
    @FXML
    private void refresh(ActionEvent event) {
        chooseBar(bDashboard, rowDashboard);
        additionalNode.openMenu("views/dashboard.fxml", rowDashboard);
    }
    
    @FXML
    private void openNotif(ActionEvent event) {
        boolean visible = vbNotifParent.isVisible();
        if (!visible) {
            //async get data notif
            getNotif();
            vbNotifChild.getChildren().clear();
        }
        vbNotifParent.setVisible(!visible);
    }
    private void getNotif(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.getLogNotif();
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) {
                        buildNotif((List<Log>) response.getData());
                    }
                }));
                tl.play();
            }
        }, 0);
    }
    private void buildNotif(List<Log> list){
        if (list.size() >0) {
            for (Log log : list) {
                Label l = new Label(log.getText());
                l.setPrefWidth(180);
                l.setId("lNotif");
                vbNotifChild.getChildren().add(l);
            }
        }else{
            Label l = new Label("Notifikasi kosong");
            l.setPrefWidth(180);
            l.setId("lNotif");
            vbNotifChild.getChildren().add(l);
        }
    }
    @FXML
    private void closeNotif(ActionEvent event) {
        vbNotifParent.setVisible(false);
    }
    
    @FXML
    private void openAdd(ActionEvent event) {
        if (rowMustahiqAdd.isVisible()) {
            return;
        }
        rowMustahiqList.setVisible(false);
        rowMustahiqAdd.setOpacity(1);
        rowMustahiqAdd.setVisible(true);
        additionalNode.openMenu("views/mustahiqAdd.fxml", rowMustahiqAdd);
    }
    
    @FXML
    private void openAddAmil(ActionEvent event) {
        if (rowAmilAdd.isVisible()) {
            return;
        }
        rowAmilList.setVisible(false);
        rowAmilAdd.setOpacity(1);
        rowAmilAdd.setVisible(true);
        additionalNode.openMenu("views/amilAdd.fxml", rowAmilAdd);
    }
    
    private void openMsgNotif(String msg){
        Label l = new Label(msg);
        l.setPrefWidth(180);
        l.setId("lNotif");
        vbNotifChild.getChildren().clear();
        vbNotifChild.getChildren().add(l);
        vbNotifParent.setVisible(true);
    }
    /* START handle logic UI */
    
    private void initiateNode(){
        ap.setBackground(Background.EMPTY);
        vbNotifParent.setVisible(false);
        bFloat.setVisible(false);
        bFloatAdmin.setVisible(false);
        chooseBar(bDashboard, rowDashboard);
        additionalNode.openMenu("views/dashboard.fxml", rowDashboard);
        bNotif.setTooltip(new Tooltip("Notifikasi"));
        bRefresh.setTooltip(new Tooltip("Refresh Data"));
        bVersion.setText(APP_NAME+VERSION);
        bVersion.setTooltip(new Tooltip(APP_NAME+VERSION));
        bFloat.visibleProperty().bind(rowMustahiqList.visibleProperty());
        rowMustahiqList.visibleProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                bFloat.setOpacity(0);
                new FadeInUpBigTransition(bFloat).play();
            }
        });
        bFloatAdmin.visibleProperty().bind(rowAmilList.visibleProperty());
        rowAmilList.visibleProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                bFloatAdmin.setOpacity(0);
                new FadeInUpBigTransition(bFloatAdmin).play();
            }
        });
        zakatMenu = additionalNode.buildZakatParent(apChild);
        HBox b = new HBox();
        b.setFillHeight(true);
        b.setSpacing(65);
        HBox.setHgrow(b, Priority.ALWAYS);
        b.setId("center");
        b.getChildren().addAll(zakatMenu);
        rowZakat.getChildren().clear();
        rowZakat.getChildren().addAll(b);
        additionalNode.zakatContentType(false, zakatMenu);
        Platform.runLater(() -> {
            Stage stage = (Stage) ap.getScene().getWindow();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() > 600) {
                    if (!isWide) {
                        isWide = true;
                        HBox box = new HBox();
                        box.setFillHeight(true);
                        HBox.setHgrow(box, Priority.ALWAYS);
                        box.setSpacing(65);
                        box.setId("center");
                        additionalNode.zakatContentType(false, zakatMenu);
                        box.getChildren().addAll(zakatMenu);
                        rowZakat.getChildren().clear();
                        rowZakat.getChildren().addAll(box);
                    }
                }else{
                    if (isWide) {
                        isWide = false;
                        rowZakat.getChildren().clear();
                        VBox box = new VBox();
                        box.setFillWidth(true);
                        box.setSpacing(25);
                        box.setId("center");
                        additionalNode.zakatContentType(true, zakatMenu);
                        box.getChildren().addAll(zakatMenu);
                        rowZakat.getChildren().clear();
                        rowZakat.getChildren().addAll(box);
                    }
                }
            });
        });
    }
    
    
    /* START handle choose menu */
    private void chooseBar(Button b, HBox row){
        bDashboard.setId("bToolbar");
        bZakat.setId("bToolbar");
        bMustahiq.setId("bToolbar");
        bLaporan.setId("bToolbar");
        bAmilZakat.setId("bToolbar");
        if (b != null) {
            b.setId("bToolbarChoosed");
            rowDashboard.setVisible(false);
            rowMustahiqList.setVisible(false);
            rowMustahiqAdd.setVisible(false);
            rowZakat.setVisible(false);
            rowZakatChild.setVisible(false);
            rowAmil.setVisible(false);
            rowAmilAdd.setVisible(false);
            rowAmilList.setVisible(false);
            rowLaporan.setVisible(false);
            if (row != null) {
                row.setOpacity(1);
                row.setVisible(true);
            }
        }
    }
    /* END handle choose menu */
    
    /* START handle window state */
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
            bWinState.setId("bMaxim");
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
    private void windowState(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
            stage.setY(0);
            bWinState.setId("bMaxim");
        }else{
            stage.setMaximized(true);
            bWinState.setId("bRestore");
        }
    }

    @FXML
    private void onRelease(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
        int tolerance = 3;
        if (event.getScreenY() <= 0) {
            if (!stage.isMaximized()) {
                stage.setMaximized(true);
                bWinState.setId("bRestore");
            }
        }else if(event.getScreenX() <= tolerance){
            double width = scSize.getWidth()/2;
            double height = scSize.getHeight();
            stage.setX(0);
            stage.setY(0);
            stage.setWidth(width);
            stage.setHeight(height);
        }else if(event.getScreenX() >= (scSize.getWidth() - tolerance)){
            double width = scSize.getWidth()/2;
            double height = scSize.getHeight();
            stage.setX(width);
            stage.setY(0);
            stage.setWidth(width);
            stage.setHeight(height);
        }
        
    }
    /* END handle window state */
    
    /* END handle logic UI*/

}
