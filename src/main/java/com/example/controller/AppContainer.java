package com.example.controller;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import com.example.App;

public class AppContainer extends Pane{
    public AppContainer(){
        initUI();
    }

    public void setView(Node view){
        this.getChildren().clear();
        this.getChildren().add(view);
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/AppContainer.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}
