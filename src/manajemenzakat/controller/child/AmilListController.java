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
import javafx.stage.Stage;
import javafx.util.Duration;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.TYPE_USER;
import manajemenzakat.model.Response;
import manajemenzakat.model.User;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class AmilListController implements Initializable {

    @FXML
    private VBox parent;
    @FXML
    private VBox boxPhoto;
    @FXML
    private Label lPhoto;
    @FXML
    private JFXButton bChange;
    @FXML
    private Label lNama;
    @FXML
    private Label lTypeUser;
    @FXML
    private JFXButton bLogout;
    @FXML
    private JFXTextField tfOptionAmilSearch;
    @FXML
    private ScrollPane spAmilList;
    @FXML
    private JFXButton bPrevAdmin;
    @FXML
    private Label lPageAdmin;
    @FXML
    private JFXButton bNextAdmin;
    private AdditionalNode additionalNode;
    private boolean in_button;
    private int page, max_page, max_list_show;
    private String search;
    private List<User> userList;
    private List<User> pageList;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        HBox.setHgrow(parent, Priority.ALWAYS);
        additionalNode = new AdditionalNode();
        in_button = false;
        boxPhoto.setOnMouseEntered((e) -> {
            bChange.setVisible(true);
        });
        boxPhoto.setOnMouseExited((e) -> {
            bChange.setVisible(false);
        });
        bNextAdmin.setDisable(true);
        bPrevAdmin.setDisable(true);
        Platform.runLater(() -> {
            String path = Session.getUser().getPath();
            if (path != null) { makePhoto(new File(path)); }
            max_list_show = 20;
            search = "";
            getAmilList();
            lNama.setText(Session.getUser().getNama());
            lTypeUser.setText("Administrator" );
            bChange.setVisible(false);
            lPageAdmin.requestFocus();
        });
    }    
    private void getAmilList(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.getUserList(search);
                if (response.isStatus()) {
                    userList = (ArrayList<User>) response.getData();
                    Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                        page = 1;
                        setLayoutUserList();
                    }));
                    tl.play();
                }
            }
        }, 0);
    }
    private void setLayoutUserList(){
        if (userList.size() > max_list_show) {
            int m = userList.size()/max_list_show;
            int mx = userList.size()%max_list_show;
            if (mx > 0) {
                max_page = m + 1;
            }else{
                max_page = m;
            }
            int from, to;
            if (page == 1) {
                from = 0;
                to = max_list_show;
                bPrevAdmin.setDisable(true);
                bNextAdmin.setDisable(!(max_page > page));
            }else{
                from = (page-1) * max_list_show;
                to = page * max_list_show;
                if (to > userList.size()) {
                    to = userList.size();
                }
            }
            System.out.println("index from:"+from+", to:"+to);
            pageList = userList.subList(from, to);
            lPageAdmin.setText(page+"/"+max_page);
        }else{
            bPrevAdmin.setDisable(true);
            bNextAdmin.setDisable(true);
            page = 1;
            lPageAdmin.setText(1+"/"+1);
            pageList = userList;
        }
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        Object[] obj = additionalNode.buildUserList(pageList, page, max_list_show, ap);
        VBox vbox  = (VBox) obj[0];
        setActionMustahiqList((List<JFXButton>) obj[1]);
        spAmilList.setContent(vbox);
        spAmilList.setVvalue(0);
    }
    private void setActionMustahiqList(List<JFXButton> list){
        int i = 0;
        for (User u: pageList) {
            list.get(i).setOnAction((e) -> {
                String msg = "Hapus Amil bernama: "+
                        u.getNama()+" - "+(u.getRole()==1?"Administrator":"Amil")+" ?";
                additionalNode.getAlert(msg, TYPE_USER, null, null, u, null);
                getAmilList();
            });
            i++;
        }
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Picture format", ext);
        fc.getExtensionFilters().add(extFilter);
        Stage thisStage = (Stage) parent.getScene().getWindow();
        File file = fc.showOpenDialog(thisStage);
        if (makePhoto(file)) {
            String path = savePhoto(file);
            Session.getUser().setPath(path);
            service.saveUser(Session.getUser());
        }
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
    private void onKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            search = tfOptionAmilSearch.getText();
            getAmilList();
        }
    }

    @FXML
    private void optionAmilSearch(ActionEvent event) {
        search = tfOptionAmilSearch.getText();
        getAmilList();
    }

    @FXML
    private void getPrevPage(ActionEvent event) {
        if (page > 1) {
            page--;
            setLayoutUserList();
            bNextAdmin.setDisable(false);
            if (page == 1) {
                 bPrevAdmin.setDisable(true);
            }
        }
    }

    @FXML
    private void getNextPage(ActionEvent event) {
        if (page < max_page) {
            page++;
            setLayoutUserList();
             bPrevAdmin.setDisable(false);
             if (page >= max_page) {
                 bNextAdmin.setDisable(true);
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
}
