package com.example.controller.submenu;

import java.io.IOException;
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
import javafx.scene.layout.AnchorPane;

public class Shop extends SubMenuController {
    @FXML private AnchorPane productHolder;
    
    private Grid productGrid;
    private GridItem newProductViewTrigger;
    private ProductRegister productRegister;
    private ProductView productView;

    private String getProductsSQL;

    public Shop() {
        initUI("Shop");
        initQuerys();
        initComponents();
        makeConnections();

        updateProducts();
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
                    productView.setupProduct(product.getProductId());
                    setView(productView);
                });

                current = result.next();
            }
        }catch(SQLException e){e.printStackTrace();}
    }

    private void initQuerys(){
        getProductsSQL = "SELECT p.productid, s.name AS suppliername, i.url AS icon, p.name, p.price, p.type \r\n" + //
                        "FROM product p JOIN supplier s ON p.supplierid = s.id\r\n" + //
                        "JOIN producticon i ON (i.productid = p.productid AND i.icon_index = 0 AND i.resolution = '150')";    
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

        if (newProductViewTrigger != null){
            productGrid.addStatic(newProductViewTrigger);
        }
    }
}
