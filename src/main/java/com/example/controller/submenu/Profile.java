package com.example.controller.submenu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.example.App;
import com.example.utils.DataBase;
import com.example.utils.ImageCropper;
import com.example.utils.ImageScalator;
import com.example.utils.QRGenerator;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class Profile extends SubMenuController{
    @FXML private Pane pfpContainer;
    @FXML private ImageView pfp;
    @FXML private Button changePfp;
    @FXML private Label displayPublicName;
    @FXML private Label displayName;
    @FXML private Label displayStatus;
    @FXML private Label displayId;
    @FXML private TextField publicName;
    @FXML private Button changePublicName;
    @FXML private TextField verifiedStatus;
    @FXML private Button verify;
    @FXML private Label email;
    @FXML private Label created;
    @FXML private Button changePassword;
    @FXML private Button deleteAccount;
    @FXML private ImageView authQR;

    private String getUserDataSQL;
    private String updatePfpSQL;

    private final String pathToPfpFolder;

    private ImageCropper pfpCropper;
    private FileChooser fileChooser;

    private String user;

    public Profile(){
        pathToPfpFolder = "src/main/resources/com/example/storage/pfps";
       

        initUI("Profile");
        initQuerys();
        initComponents();
        makeConnections();
    }

    private void initQuerys(){
        getUserDataSQL = "SELECT * FROM users WHERE userid = ?";
        updatePfpSQL = "UPDATE users SET profilepic = ? WHERE userid = ?";
    }

    private void makeConnections(){
        setOnMouseClicked(e->{requestFocus();});

        changePfp.setOnMouseClicked(e->{
            File imageFile = fileChooser.showOpenDialog(App.getStage());

            if (imageFile==null){return;}
            Image image = new Image(imageFile.toURI().toString());

            pfpCropper.setImage(image);
            pfpCropper.show();
        });

        pfpCropper.setOnCropSuccess(()-> {
            WritableImage image = pfpCropper.getCroppedImage();
            BufferedImage imageBuff = SwingFXUtils.fromFXImage(image, null);
            String path = pathToPfpFolder+"/user_"+user+"/profilepicture.png";

            File dir = new File(path);
            if (!dir.exists()){dir.mkdirs();}

            pfp.setImage(ImageScalator.scale(image, 115));

            try{
                ImageIO.write(imageBuff, "png", dir);

                String dbpath = "/com/example/storage/pfps/user_"+user+"/profilepicture.png";
                try{
                    PreparedStatement st = DataBase.getConnection()
                        .prepareStatement(updatePfpSQL);
                    st.setString(1, dbpath);
                    st.setString(2, user);
                    st.executeUpdate();
                }catch(SQLException e){}
            }catch(IOException e){}
        });
    }

    private void initComponents(){
        pfpCropper = new ImageCropper();
        pfpCropper.hide();
        pfpCropper.setRadius(1);
        getChildren().add(pfpCropper);

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files, ","*.png", "*.jpg", "*.jpeg")
            );

        Rectangle clip = new Rectangle();
        clip.setWidth(115);
        clip.setHeight(115);
        double arc = 115 * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);

        pfpContainer.setClip(clip);
    }

    public void setupUser(String userid){
        this.user = userid;
        try{
            PreparedStatement stm = DataBase.getConnection()
                .prepareStatement(getUserDataSQL);
            stm.setString(1, userid);

            ResultSet data = stm.executeQuery();
            if (!data.next()){return;}

            displayPublicName.setText(data.getString("displayname"));
            displayName.setText("@"+ data.getString( "username"));
            displayStatus.setText(data.getString("usertype"));
            displayId.setText("id: "+ data.getString("userid"));
            publicName.setText(data.getString("displayName"));
            email.setText(data.getString("email"));
            created.setText(data.getString("created"));

            if(data.getBoolean("verified")){
                verifiedStatus.setText("VERIFIED");
            }else{
                verifiedStatus.setText("NOT VERIFIED");
            }

            setPfp(data.getString("profilepic"));            
            try{
                authQR.setImage(QRGenerator.generate(
                    data.getString("authURI"), 200, 200));
            }catch(Exception e){}
        }catch(SQLException e){e.printStackTrace();}
    }

    private void setPfp(String path){
        pfp.setImage(ImageScalator.scale(
                new Image(path), 115));
        
    }
}
