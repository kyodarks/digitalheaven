package com.example.controller;

import java.io.IOException;

import com.example.App;

import javafx.fxml.FXMLLoader;
import javafx.scene.shape.SVGPath;

public class ReturnButton extends SVGPath{
    public ReturnButton(){
        initUI();
        initStyle();
    }

    private void initStyle(){
        setStyle("-fx-fill : rgb(200, 200, 200)");
        setOnMouseEntered(e->{setStyle("-fx-fill : rgb(0, 0, 0)");});
        setOnMouseExited(e->{setStyle("-fx-fill : rgb(200, 200, 200)");});
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/custom/ReturnButton.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}
