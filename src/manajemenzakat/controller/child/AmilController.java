/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_ADMINISTRATOR;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL_LIST;
import manajemenzakat.model.Log;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class AmilController implements Initializable {

    @FXML
    private Label lNama;
    @FXML
    private Label lTypeUser;
    @FXML
    private JFXButton bLogout;
    @FXML
    private JFXButton bKembaliAdmin;
    @FXML
    private ScrollPane spLogList;
    @FXML
    private JFXButton bPrevAmil;
    @FXML
    private Label lPageAmil;
    @FXML
    private JFXButton bNextAmil;
    @FXML
    private VBox parent;
    @FXML
    private VBox boxPhoto;
    @FXML
    private Label lPhoto;
    @FXML
    private JFXButton bChange;
    private boolean in_button;
    @FXML
    private JFXTextField tfOptionLogSearch;
    private int page, max_page, max_list_show;
    private String search;
    private List<Log> logList;
    private List<Log> pageList;
    private AdditionalNode additionalNode;
    private User user;
    public void setUser(User user){
        this.user = user;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        additionalNode = new AdditionalNode();
        in_button = false;
        bChange.setVisible(false);
        bLogout.setVisible(false);
        bKembaliAdmin.setVisible(false);
        HBox.setHgrow(parent, Priority.ALWAYS);
        boxPhoto.setOnMouseEntered((e) -> {
            bChange.setVisible(true);
        });
        boxPhoto.setOnMouseExited((e) -> {
            bChange.setVisible(false);
        });
        bNextAmil.setDisable(true);
        bPrevAmil.setDisable(true);
        Platform.runLater(() -> {
            boolean admin = (Session.getUser().getRole() == ROLE_ADMINISTRATOR);
            bLogout.setVisible(!admin);
            bKembaliAdmin.setVisible(admin);
            
            if (user == null) { user = new User(); lTypeUser.setText("Amil"); }
            else lTypeUser.setText(((user.getRole() == ROLE_ADMINISTRATOR))? "Administrator" : "Amil");
            
            lNama.setText((admin)? user.getNama() : Session.getUser().getNama());
            String path = (admin)? user.getPath(): Session.getUser().getPath();
            if (path != null) { makePhoto(new File(path)); }
            max_list_show = 20;
            search = "";
            getLogList();
        });
    }    
    private void getLogList(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int id = (user.getId_user() <= 0)? 
                        Session.getUser().getId_user() : user.getId_user();
                Response response = service.getLogList(search, id);
                if (response.isStatus()) {
                    logList = (ArrayList<Log>) response.getData();
                    Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                        page = 1;
                        setLayoutLogList();
                    }));
                    tl.play();
                }
            }
        }, 0);
    }
    private void setLayoutLogList(){
        if (logList.size() > max_list_show) {
            int m = logList.size()/max_list_show;
            int mx = logList.size()%max_list_show;
            if (mx > 0) {
                max_page = m + 1;
            }else{
                max_page = m;
            }
            int from, to;
            if (page == 1) {
                from = 0;
                to = max_list_show;
                bPrevAmil.setDisable(true);
                bNextAmil.setDisable(!(max_page > page));
            }else{
                from = (page-1) * max_list_show;
                to = page * max_list_show;
                if (to > logList.size()) {
                    to = logList.size();
                }
            }
            System.out.println("index from:"+from+", to:"+to);
            pageList = logList.subList(from, to);
            lPageAmil.setText(page+"/"+max_page);
        }else{
            bPrevAmil.setDisable(true);
            bNextAmil.setDisable(true);
            page = 1;
            lPageAmil.setText(1+"/"+1);
            pageList = logList;
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        VBox vbox  = additionalNode.buildLogList(pageList, page, max_list_show, ap);
        spLogList.setContent(vbox);
        spLogList.setVvalue(0);
    }
    
    @FXML
    private void changePhoto(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih photo");
        List<String> ext = new ArrayList<>(
                Arrays.asList(
                        "*.jpg", 
                        "*.png", 
                        "*.jpeg", 
                        "*.gif", 
                        "*.bmp"
                )
        );
        ExtensionFilter extFilter = new ExtensionFilter("Picture format", ext);
        fc.getExtensionFilters().add(extFilter);
        Stage thisStage = (Stage) parent.getScene().getWindow();
        File file = fc.showOpenDialog(thisStage);
        if (makePhoto(file)) {
            String path = savePhoto(file);
            if (user.getId_user() <= 0) {
               Session.getUser().setPath(path);
                service.saveUser(Session.getUser());
            }else{
                user.setPath(path);
                service.saveUser(user);
            }
        }
    }
    private boolean makePhoto(File file){
        if (file != null) {
            Image img= null;
            try {
                img = new Image(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
//                Logger.getLogger(AmilController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (img != null) {
                ImageView view =new ImageView(img);
                view.setFitWidth(100);
                view.setFitHeight(120);
                Rectangle r = new Rectangle(100, 120);
                r.setArcWidth(10);
                r.setArcHeight(10);
                view.setClip(r);
                lPhoto.setGraphic(view);
                lPhoto.setText("");
                return true;
            }
        }
        return false;
    }
    private String savePhoto(File file){
        new File("resource").mkdir();
        int i = file.getName().lastIndexOf(".");
        String ext = file.getName().substring(i);
        String path = "resource/"+Session.getUser().getNama()+ext;
        File dest = new File(path);
        InputStream inStream = null;
	OutputStream outStream = null;
        try {
            inStream = new FileInputStream(file);
    	    outStream = new FileOutputStream(dest);

    	    byte[] buffer = new byte[1024];

    	    int length;
    	    //copy the file content in bytes
    	    while ((length = inStream.read(buffer)) > 0){
    	    	outStream.write(buffer, 0, length);
    	    }
    	    inStream.close();
    	    outStream.close();
        } catch (Exception e) {
            Logger.getLogger(AmilController.class.getName()).log(Level.SEVERE, null, e);
        }
        return path;
    }
    @FXML
    private void logout(ActionEvent event) {
        if (in_button) {
            return;
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.logout(Session.getUser());
                if (response.isStatus()) { Session.setUser(null); service.close(); }
                additionalNode.openLogin(parent);
                in_button = false;
            }
        }, 0);
        in_button = true;
    }
    
    @FXML
    private void backToAdmin(ActionEvent event) {
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_AMIL).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_AMIL_LIST);
        additionalNode.openMenu("views/amilList.fxml", row);
    }

    @FXML
    private void getPrevPage(ActionEvent event) {
        if (page > 1) {
            page--;
            setLayoutLogList();
            bNextAmil.setDisable(false);
            if (page == 1) {
                 bPrevAmil.setDisable(true);
            }
        }
    }

    @FXML
    private void getNextPage(ActionEvent event) {
        if (page < max_page) {
            page++;
            setLayoutLogList();
             bPrevAmil.setDisable(false);
             if (page >= max_page) {
                 bNextAmil.setDisable(true);
            }
        }
    }

    @FXML
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            search = tfOptionLogSearch.getText();
            getLogList();
        }
    }

    @FXML
    private void optionLogSearch(ActionEvent event) {
        search = tfOptionLogSearch.getText();
        getLogList();
    }
    
}
