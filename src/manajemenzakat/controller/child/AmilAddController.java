/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemenzakat.controller.child;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import static manajemenzakat.controller.MainController.service;
import manajemenzakat.controller.additional.AdditionalNode;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_ADMINISTRATOR;
import static manajemenzakat.controller.additional.RowTypeRole.ROLE_AMIL;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL_ADD;
import static manajemenzakat.controller.additional.RowTypeRole.ROW_AMIL_LIST;
import manajemenzakat.model.JenisMustahiq;
import manajemenzakat.model.Response;
import manajemenzakat.model.Role;
import manajemenzakat.model.User;
import manajemenzakat.util.Session;

/**
 * FXML Controller class
 *
 * @author RIZAL
 */
public class AmilAddController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private User user;
    @FXML
    private VBox parent;
    @FXML
    private JFXTextField tfFormUsername;
    @FXML
    private JFXPasswordField tfFormPassword;
    @FXML
    private JFXTextField tfFormUserNama;
    @FXML
    private JFXTextField tfFormUserAlamat;
    @FXML
    private JFXTextField tfFormUserNomor;
    @FXML
    private JFXComboBox<Role> cbRole;
    @FXML
    private Label lPhoto;
    @FXML
    private Label lMsgForm;
    public void setUser(User user){
        this.user = user;
    }
    private AdditionalNode additionalNode;
    private String path;
    private boolean in_button;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        in_button = false;
        additionalNode = new AdditionalNode();
        cbRole.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role object) {
                return object.getRole_name();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
        cbRole.setItems(getRoles());
        Platform.runLater(() -> {
            if (user != null) {
                tfFormUsername.setText(user.getUsername());
                tfFormPassword.setPromptText("(kosongi bila tidak dirubah)");
                tfFormUserNama.setText(user.getNama());
                tfFormUserAlamat.setText(user.getAlamat());
                tfFormUserNomor.setText(user.getNo_telp());
                int role = (user.getRole()== ROLE_ADMINISTRATOR)? 0 : 1;
                System.out.println(role);
                cbRole.getSelectionModel().select(role);
                path = user.getPath();
                if (path != null) { makePhoto(new File(path)); }
            }
        });
    }    
    private ObservableList<Role> getRoles(){
        ObservableList<Role> list =  FXCollections.observableArrayList();
        list.addAll(
                new Role(ROLE_ADMINISTRATOR, "Administrator"),
                new Role(ROLE_AMIL, "Amil")
        );
        return list;
    }
    @FXML
    private void simpanUser(ActionEvent event) {
        if (in_button) {
            return;
        }
        String username = tfFormUsername.getText();
        String pwd = tfFormPassword.getText();
        String nama = tfFormUserNama.getText();
        String alamat = tfFormUserAlamat.getText();
        String nomor = tfFormUserNomor.getText();
        Role role = cbRole.getSelectionModel().getSelectedItem();
        
        if (username.isEmpty() || nama.isEmpty() || role == null) {
            openMsg("*Nama, username dan role tidak boleh kosong");
            return;
        }
        if (user == null) {
            if (pwd.isEmpty()) {
                openMsg("*Password untuk amil baru tidak boleh kosong");
                return;
            }
        }
        try {
            if (user == null) { 
                user = new User(); 
                user.setPassword(pwd);
            }else{
                if (!pwd.replaceAll(" ", "").isEmpty()) {//new password
                    user.setPassword(hashPwd(pwd));
                }
            }
            user.setUsername(username);
            user.setNama(nama);
            user.setAlamat(alamat);
            user.setNo_telp(nomor);
            user.setPath(path);
            user.setRole(role.getRole());
            in_button = true;
            new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Response response = service.saveUser(user);
                in_button = false;
                Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
                    if (response.isStatus()) { close();}
                    else openMsg(response.getMsg());
                }));
                tl.play();
            }
        }, 0);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("handle hash fail e:"+e.getMessage());
        }
    }
    private String hashPwd(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private void openMsg(String msg){
        lMsgForm.setText(msg);
        Timeline tl = new Timeline(new KeyFrame(Duration.ONE, (e) -> {
            lMsgForm.setText("");
        }));
        tl.setDelay(Duration.seconds(2.5));
        tl.play();
    }
    @FXML
    private void batalSimpanUser(ActionEvent event) {
        close();
    }
    private void close(){
        AnchorPane ap= (AnchorPane) parent
                .getParent().getParent();
        ap.getChildren().get(ROW_AMIL_ADD).setVisible(false);
        HBox row =(HBox) ap.getChildren().get(ROW_AMIL_LIST);
        additionalNode.openMenu("views/amilList.fxml", row);
    }
    @FXML
    private void uploadFoto(ActionEvent event) {
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
            path = savePhoto(file);
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
                view.setFitWidth(170);
                view.setFitHeight(190);
                Rectangle r = new Rectangle(160, 190);
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
