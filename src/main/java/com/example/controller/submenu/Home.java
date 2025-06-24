package com.example.controller.submenu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.controller.MainApplication;
import com.example.controller.Product;
import com.example.utils.DataBase;
import com.example.utils.Grid;
import com.example.utils.Grid.GridItem;
import com.example.utils.ImageScalator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Home extends SubMenuController{
    @FXML private Pane pfpContainer;
    @FXML private ImageView pfp;
    @FXML private Label username;
    @FXML private Label liked;
    @FXML private Label bought;
    @FXML private Label status;
    @FXML private Label verified;

    @FXML AnchorPane likedProducts;
    @FXML AnchorPane recentProducts;

    private String getRecentProductsSQL;
    private String getLikedProductsSQL;
    private String getUserDataSQL;
    private String getLikedAmountSQL;
    private String getBoughtAmountSQL;

    private Grid likedProductsGrid;
    private Grid recentProductsGrid;

    private String user;

    public Home(){
        initUI("Home");
        initQuerys();
        initComponents();
    }

    private void initQuerys(){
        getUserDataSQL = "SELECT * FROM users WHERE userid = ?";

        getRecentProductsSQL = "SELECT p.productid, s.name AS suppliername, i.url AS icon, p.name, p.price, p.type \r\n" + //
                        "FROM productvisit pv JOIN product p ON pv.productid = p.productid JOIN supplier s ON p.supplierid = s.id JOIN\r\n" + //
                        "producticon i ON i.productid = p.productid WHERE pv.userid = ? AND i.icon_index = 0 AND i.resolution = '150' ORDER BY date DESC LIMIT 5";

        getLikedProductsSQL = "SELECT p.productid, s.name AS suppliername, i.url AS icon, p.name, p.price, p.type \r\n" + //
                        "FROM favorite f JOIN product p ON f.productid = p.productid JOIN supplier s ON p.supplierid = s.id \r\n" + //
                        "JOIN producticon i ON i.productid = p.productid \r\n" + //
                        "WHERE f.userid = ? AND i.icon_index = 0 AND i.resolution = '150' LIMIT 5";
        
        getLikedAmountSQL = "SELECT COUNT(*) AS amount FROM favorite WHERE userid = ?";
        
        getBoughtAmountSQL = "SELECT COUNT(*) AS amount FROM sell WHERE userid = ?";
    }

    public void setupUser(String userid){
        user = userid;
        update();
    }

    private void initComponents(){
        likedProductsGrid = new Grid(likedProducts);
        likedProductsGrid.setColumns(5);

        recentProductsGrid = new Grid(recentProducts);
        recentProductsGrid.setColumns(5);

        Rectangle clip = new Rectangle();
        clip.setWidth(60);
        clip.setHeight(60);
        double arc = 60 * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);

        pfpContainer.setClip(clip);
    }

    private void update(){
        try{
            PreparedStatement userStm = DataBase.getConnection()
                .prepareStatement(getUserDataSQL);
            PreparedStatement recentProductsStm = DataBase.getConnection()
                .prepareStatement(getRecentProductsSQL);
            PreparedStatement likedProductsStm = DataBase.getConnection()
                .prepareStatement(getLikedProductsSQL);
            PreparedStatement likedAmountStm = DataBase.getConnection()
                .prepareStatement(getLikedAmountSQL);
            PreparedStatement boughtAmountStm = DataBase.getConnection()
                .prepareStatement(getBoughtAmountSQL);
            
            userStm.setString(1, user);
            recentProductsStm.setString(1, user);
            likedProductsStm.setString(1, user);
            likedAmountStm.setString(1, user);
            boughtAmountStm.setString(1, user);

            ResultSet userResult = userStm.executeQuery();
            ResultSet recentResult = recentProductsStm.executeQuery();
            ResultSet likedResult = likedProductsStm.executeQuery();
            ResultSet likedAmount = likedAmountStm.executeQuery();
            ResultSet boughtAmount = boughtAmountStm.executeQuery();

            initUserInfo(userResult);
            fillContainer(recentResult, recentProductsGrid);
            fillContainer(likedResult, likedProductsGrid);

            if (likedAmount.next()){liked.setText(likedAmount.getString(1));}
            if (boughtAmount.next()){bought.setText(boughtAmount.getString(1));}
        }catch(SQLException e){e.printStackTrace();}
    }

    @Override
    public void onEnter(){
        update();
    }

    private void initUserInfo(ResultSet userData) throws SQLException{
        if (!userData.next()){return;}

        username.setText("@"+ userData.getString("displayname"));
        
        if (userData.getString("usertype").equals("customer")){
            status.setText("CLIENT");
        }else{
            status.setText("ADMIN");
        }
        
        if (userData.getBoolean("verified")){
            verified.setText("VERIFIED");
        }else{
            verified.setText("NOT VERIFIED");
        }

        String pfpPath = userData.getString("profilepic");
        pfp.setImage(ImageScalator.scale(
                new Image(pfpPath), 60));
    }

    private void fillContainer(ResultSet products, Grid container) throws SQLException{
        container.clear();
        boolean lol = products.next();
        while(lol){
            Product current = makeProduct(products);
            container.add(new GridItem(current));

            current.setOnMouseClicked(e->{
                MainApplication app = App.getMainApplication();
                Shop shop = (Shop) app.getSubMenu("shop");
                shop.openProduct(current);
                app.gotoSubmenu("shop");
            });
            lol = products.next();
        }
    }

    private Product makeProduct(ResultSet current) throws SQLException{
        int id = current.getInt(1);
        String supplier = current.getString(2);
        String icon = current.getString(3);
        String name = current.getString(4);
        double price = current.getDouble(5);
        String type = current.getString(6);

        return new Product(name,id, price, icon, type, supplier);
    }
}  
