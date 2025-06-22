package com.example.controller;

import java.io.IOException;

import com.example.App;
import com.example.utils.DoubleToCop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Product extends Pane{
    @FXML ImageView productIcon;
    @FXML Label productName;
    @FXML Label productCost;
    @FXML Pane productIconContainer;

    private int id;
    private String name;
    private double price;
    private String icon;
    private String type;
    private String supplier;

    public Product(String name, int id, double price, String iconUrl, String type, String supplier){
        initUI();

        productName.setText(name);
        productCost.setText(DoubleToCop.get(price));
        productIcon.setImage(new Image(
            iconUrl.replace("src/main/resources", "")));
   
        this.name = name;
        this.price = price;
        this.icon = iconUrl;
        this.type = type;
        this.supplier = supplier;
        this.id = id;
    }

    public String getName(){return name;}
    public double getPrice(){return price;}
    public String getIcon(){return icon;}
    public String getType(){return type;}
    public String getSupplier(){return supplier;}
    public int getProductId(){return id;}

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/custom/Product.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}

        Rectangle clip = new Rectangle();
        clip.setWidth(130);
        clip.setHeight(130);
        double arc = 130 * .08 * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);

        productIconContainer.setClip(clip);
    }
}
