package com.example.controller.submenuView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.App;
import com.example.controller.Product;
import com.example.utils.DataBase;
import com.example.utils.DoubleToCop;
import com.example.utils.Grid;
import com.example.utils.PopupMessage;
import com.example.utils.Grid.GridItem;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class ProductView extends SubmenuView{
    @FXML private Pane iconContainer;
    @FXML private ImageView icon;
    @FXML private TextField price;
    @FXML private TextArea name;
    @FXML private Label supplier;
    @FXML private Label type;
    @FXML private Label id;
    @FXML private Label summaryName;
    @FXML private Label total;
    @FXML private Circle less;
    @FXML private Circle more;
    @FXML private TextField amount;
    @FXML private StackPane favorite;
    @FXML private Button buy;
    @FXML private HBox subicons;
    @FXML private AnchorPane similarProducts;
    @FXML private SVGPath favoriteIcon;

    private SubIcon selectedIcon;
    private Grid similarProductsGrid;
    private PopupMessage purchaseMessage;

    private String getProductInfoSQL;
    private String getProductIconsSQL;
    private String similarProductsSQL;
    private String isProductFavoriteSQL;
    private String addFavoriteSQL;
    private String removeFavoriteSQL;
    private String addSellSQL;

    private int currentProduct;
    private double productBasePrice;

    private int productAmount;
    private double sellTotal;
    private boolean productIsFavorite;

    public ProductView(){
        initUI("ProductView");

        initComponents();
        makeConnections();
        initQuerys();
    }

    public void setupProduct(int productId){
        currentProduct = productId;

        try{
            productIsFavorite = isProductFavorite(productId);
            reset();

            PreparedStatement infostm = DataBase.getConnection()
                .prepareStatement(getProductInfoSQL);
            infostm.setInt(1, productId);

            PreparedStatement iconsstm = DataBase.getConnection()
                .prepareStatement(getProductIconsSQL);
            iconsstm.setInt(1, productId);
            
            ResultSet info = infostm.executeQuery();
            ResultSet icons = iconsstm.executeQuery();

            loadProductInfo(info);
            loadSubIcons(icons);

            PreparedStatement similarstm = DataBase.getConnection()
                .prepareStatement(similarProductsSQL);
            similarstm.setString(1, info.getString("type"));
            similarstm.setInt(2, productId);
            loadSimilarProducts(similarstm.executeQuery());

        }catch(SQLException e){}
    }

    private void loadProductInfo(ResultSet info) throws SQLException{
        if (info.next()){
            id.setText(info.getString(1));
            supplier.setText(info.getString(2));
            name.setText(info.getString(3));
            summaryName.setText(info.getString(3));
            price.setText(DoubleToCop.get(info.getDouble(4)));
            type.setText(info.getString(5));

            productBasePrice = info.getDouble(4);
            sellTotal = productBasePrice;
            updateSellVariables();
        }
    }

    private void loadSubIcons(ResultSet icons) throws SQLException{
        boolean c = false;
        c = icons.next();

        if (c){
            ArrayList<String> res45 = new ArrayList<>();
            ArrayList<String> res260 = new ArrayList<>();
            
            while (c) {
                String resolution = icons.getString(1);
                String url = icons.getString(2);

                if (resolution.equals("45")){
                    res45.add(url);
                }else{
                    res260.add(url);
                }

                c = icons.next();
            }   
            
            for (int i=0;i<res45.size();i++){
                String a = res45.get(i);
                String b = res260.get(i);

                SubIcon subicon = new SubIcon(
                    b.replace("src/main/resources", ""),  
                    a.replace("src/main/resources", ""));

                subicons.getChildren().add(subicon);
                subicon.setOnMouseClicked(e->{
                    selectSubIcon(subicon);
                });
            
                if (i==0){
                    selectSubIcon(subicon);
                }
            }
        }
    }

    private void loadSimilarProducts(ResultSet products) throws SQLException{
        boolean c = false;
        c = products.next();
        while (c) {
            int id = products.getInt(1);
            String supplier = products.getString(2);
            String icon = products.getString(3);
            String name = products.getString(4);
            double price = products.getDouble(5);
            String type = products.getString(6);

            Product product = new Product(name,id, price, icon, type, supplier);

            similarProductsGrid.add(new GridItem(product));
            product.setOnMouseClicked(e->{
                setupProduct(product.getProductId());
            });
            c = products.next();
        }
    }

    private boolean isProductFavorite(int productid) throws SQLException{
        PreparedStatement stm = DataBase.getConnection()
            .prepareStatement(isProductFavoriteSQL);
        stm.setInt(1, productid);
        stm.setString(2, App.getUser());

        ResultSet isFavorite = stm.executeQuery();
        return isFavorite.next();
    }

    private void initComponents(){
        Rectangle clip = new Rectangle();
        clip.setWidth(260);
        clip.setHeight(260);
        double arc = 260 * .08 * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);

        iconContainer.setClip(clip);

        similarProductsGrid = new Grid(similarProducts);
        similarProductsGrid.setColumns(5);

        purchaseMessage = new PopupMessage(this, "Compra realizada!", 1.5);
    }

    private void initQuerys(){
        getProductInfoSQL = "SELECT p.productid, s.name AS supplier, p.name, p.price, p.type FROM product p JOIN supplier s ON p.supplierid = s.id WHERE p.productid = ?";
        
        getProductIconsSQL = "SELECT resolution, url FROM producticon WHERE (resolution = '45' OR resolution = '260') AND productid = ?;";
        
        similarProductsSQL = "SELECT p.productid, s.name AS suppliername, i.url AS icon, p.name, p.price, p.type \r\n" + //
                        "FROM product p JOIN supplier s ON p.supplierid = s.id\r\n" + //
                        "JOIN producticon i ON (i.productid = p.productid AND i.icon_index = 0 AND i.resolution = '150') WHERE p.type = ? AND p.productid != ? LIMIT 5";
        
        isProductFavoriteSQL = "SELECT * FROM favorite WHERE productid = ? AND userid = ?";
    
        addFavoriteSQL = "INSERT INTO favorite (userid, productid) VALUES (?, ?)";
        removeFavoriteSQL = "DELETE FROM favorite WHERE userid = ? AND productid = ?";

        addSellSQL = "INSERT INTO sell (product, userid, amount, total) VALUES (?, ?, ?, ?)";
    }
    
    private void makeConnections(){
        setOnMouseClicked(e->{requestFocus();});
        favorite.setOnMouseClicked(e->{
            try{
                if (productIsFavorite){
                    PreparedStatement x = DataBase.getConnection()
                        .prepareStatement(removeFavoriteSQL);
                    x.setString(1, App.getUser());
                    x.setInt(2, currentProduct);

                    x.executeUpdate();
                }else{
                    PreparedStatement x = DataBase.getConnection()
                        .prepareStatement(addFavoriteSQL);
                    x.setString(1, App.getUser());
                    x.setInt(2, currentProduct);

                    x.executeUpdate();
                }
            }catch(SQLException ex){}

            updateFavoriteButton();
        });
        amount.focusedProperty().addListener((a,b,c)->{
            try{
                productAmount = Math.max(1, Integer.parseInt(amount.getText())); 
                updateSellVariables();
            }catch(NumberFormatException e){updateSellVariables();}
        });
        less.setOnMouseClicked(e->{
            if (productAmount <= 1){return;}
            productAmount--;
            updateSellVariables();
        });
        more.setOnMouseClicked(e->{
            productAmount++;
            updateSellVariables();
        });
        buy.setOnMouseClicked(e->{
            try{
                PreparedStatement stm = DataBase.getConnection()
                    .prepareStatement(addSellSQL);
                stm.setInt(1, currentProduct);
                stm.setString(2, App.getUser());
                stm.setInt(3, productAmount);
                stm.setDouble(4, sellTotal);

                stm.executeUpdate();

                purchaseMessage.show();
            }catch(SQLException ex){}
        });
        purchaseMessage.addOption("Cerrar", "", e->{
            purchaseMessage.hide();
        });
    }

    private void updateFavoriteButton(){
        try{
            productIsFavorite = isProductFavorite(currentProduct);
            if (productIsFavorite){
                favorite.setStyle("-fx-background-color : #fd6767; -fx-background-radius: 100%;");
            }else{
                favorite.setStyle("-fx-background-color : lightgray; -fx-background-radius: 100%;");
            }
        }catch(SQLException e){}
    }

    private void updateSellVariables(){
        if (productAmount > 1){
            less.setStyle("-fx-fill : #b19797");
        }else{
            less.setStyle("-fx-fill : #d1d1d1");
        }

        amount.setText(productAmount+"");
        sellTotal = productBasePrice * productAmount;
        total.setText(DoubleToCop.get(sellTotal));
    }

    private void reset(){
        subicons.getChildren().clear();
        similarProductsGrid.clear();
        productAmount = 1;
        updateFavoriteButton();
        updateSellVariables();
    }

    private void selectSubIcon(SubIcon x){
        if (selectedIcon != null){
            selectedIcon.deselect();
        }

        selectedIcon = x;
        x.select();
        icon.setImage(new Image(x.getMainResolution()));
    }

    private static class SubIcon extends Pane{
        private ImageView icon;
        private String mainResolution;

        public SubIcon(String mainResolution, String altResolution){
            setPrefSize(45, 45);
            this.mainResolution = mainResolution;

            icon = new ImageView();
            icon.setFitHeight(45);
            icon.setFitWidth(45);
            getChildren().add(icon);
            icon.setImage(new Image(altResolution));

            Rectangle clip = new Rectangle();
            clip.setWidth(45);
            clip.setHeight(45);
            double arc = 45 * .08 * 2;

            clip.setArcWidth(arc);
            clip.setArcHeight(arc);

            setClip(clip);
            setOnMouseEntered(e -> setCursor(Cursor.HAND));
            setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
            setPadding(new Insets(5));
        }

        public String getMainResolution(){return mainResolution;}

        public void deselect(){
            setStyle("""
                    -fx-background-radius : 8%;
                    -fx-border-radius : 8%;
                    -fx-border-width : 0;
                    """);
        }

        public void select(){
            setStyle("""
                    -fx-background-radius : 8%;
                    -fx-border-radius : 8%;
                    -fx-border-width : 3px;
                    -fx-border-color : rgb(142, 142, 142);
                    -fx-border-insets: -3px;
                    """);
        }
    }
}
