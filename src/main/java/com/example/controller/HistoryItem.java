package com.example.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.utils.DoubleToCop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class HistoryItem extends HBox{
    @FXML private Pane iconContainer;
    @FXML private ImageView icon;
    @FXML private Label productName;
    @FXML private Label amount;
    @FXML private Label date;
    @FXML private Label spent;

    public HistoryItem(ResultSet result) throws SQLException{
        initUI();
        icon.setImage(new Image(
            result.getString(1).replace("src/main/resources", "")));
        productName.setText(result.getString(2));
        amount.setText(result.getString(3));
        date.setText(result.getString(4));
        spent.setText(DoubleToCop.get(result.getDouble(5)));
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/custom/HistoryItem.fxml"));
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

        iconContainer.setClip(clip);
    }
}
