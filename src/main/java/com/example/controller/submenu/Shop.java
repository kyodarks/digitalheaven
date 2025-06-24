package com.example.controller.submenu;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.controller.Product;
import com.example.controller.submenuView.ProductRegister;
import com.example.controller.submenuView.ProductView;
import com.example.utils.DataBase;
import com.example.utils.Grid;
import com.example.utils.Grid.GridItem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class Shop extends SubMenuController {
    @FXML private AnchorPane productHolder;
    @FXML private Button allCategory;
    @FXML private Button giftcardCategory;
    @FXML private Button skinCategory;
    @FXML private Button productCategory;
    @FXML private Button licenceCategory;
    @FXML private Button gameCategory;

    private Grid productGrid;
    private GridItem newProductViewTrigger;
    private ProductRegister productRegister;
    private ProductView productView;

    private String getUserTypeSQL;
    private String getProductsSQL;

    public Shop() {
        initUI("Shop");
        initQuerys();
        initComponents();
        makeConnections();

        //updateProducts();
    }

    private void updateProducts(){
        productGrid.clear();

        try{
            ResultSet result = DataBase.executeQuery(getProductsSQL);
            boolean current = result.next();

            while(current){
                int id = result.getInt(1);
                String supplier = result.getString(2);
                String icon = result.getString(3);
                String name = result.getString(4);
                double price = result.getDouble(5);
                String type = result.getString(6);

                Product product = new Product(name,id, price, icon, type, supplier);
                productGrid.add(new GridItem(product));

                product.setOnMouseClicked(e->{
                    openProduct(product);
                });

                current = result.next();
            }
        }catch(SQLException e){e.printStackTrace();}
    }

    public void openProduct(Product product){
        productView.setupProduct(product.getProductId());
        setView(productView);
    }

    private void initQuerys(){
        getProductsSQL = "SELECT p.productid, s.name AS suppliername, i.url AS icon, p.name, p.price, p.type \r\n" + //
                        "FROM product p JOIN supplier s ON p.supplierid = s.id\r\n" + //
                        "JOIN producticon i ON (i.productid = p.productid AND i.icon_index = 0 AND i.resolution = '150')";    

        getUserTypeSQL = "SELECT usertype FROM users WHERE userid = ?";
    }

    private void makeConnections(){
        newProductViewTrigger.getNode().setOnMouseClicked(e->{
            productRegister.reloadSuppliers();
            setView(productRegister); 
        });

        productRegister.setOnProductAdded(()->{
            removeView();
            updateProducts();
        });
    }

    private void initComponents(){    
        productGrid = new Grid(productHolder);
        productRegister = new ProductRegister();
        productView = new ProductView();

        try{
            newProductViewTrigger = new GridItem(FXMLLoader.load(App.class.getResource(
                "fxml/custom/NewProductButton.fxml")));
        }catch (IOException e){e.printStackTrace();}
    
        CategoryFilter.setGrid(productGrid);
        CategoryFilter.add(allCategory, "all");
        CategoryFilter.add(giftcardCategory, "Giftcard");
        CategoryFilter.add(skinCategory, "Skin");
        CategoryFilter.add(productCategory, "Product");
        CategoryFilter.add(licenceCategory, "Licence");
        CategoryFilter.add(gameCategory, "Game_Key");

        CategoryFilter.select(allCategory, "all");
    }

    public void setupUser(String userid){
        productGrid.clear();
        productGrid.removeStatic(newProductViewTrigger);

        try{
            PreparedStatement stm = DataBase.getConnection()
                .prepareStatement(getUserTypeSQL);
            stm.setString(1, userid);

            ResultSet resutl = stm.executeQuery();

            if (resutl.next()){
                String usertype = resutl.getString(1);
                if (usertype.equals("admin")){
                    productGrid.addStatic(newProductViewTrigger);
                }
            }
        }catch(SQLException e){}

        updateProducts();
    }

    private static class CategoryFilter{
        private static Button selectedCategory;
        private static Grid grid;

        public static void add(Button trigger, String categoryName){
            trigger.setOnMouseClicked(e->{
                if (trigger == selectedCategory){return;}
                deselectCurrent();
                
                selectedCategory = trigger;
                selectedCategory.getStyleClass().add("category-button-selected");

                filter(categoryName);
            });
        }

        public static void select(Button trigger, String categoryName){
            deselectCurrent();
            selectedCategory = trigger;
            selectedCategory.getStyleClass().add("category-button-selected");
        }

        public static void setGrid(Grid grid){
            CategoryFilter.grid = grid;
        }

        private static void deselectCurrent(){
            if (selectedCategory != null){
                selectedCategory.getStyleClass().remove("category-button-selected");
            }
        }

        private static void filter(String category){
            for (GridItem x : grid.getItems()){
                if (category.equals("all")){x.show(); continue;}

                Product a = (Product) x.getNode();
                if (!a.getType().equals(category)){
                    x.hide();
                }else{
                    x.show();
                }
            }

            grid.update();
        }
    }
}
