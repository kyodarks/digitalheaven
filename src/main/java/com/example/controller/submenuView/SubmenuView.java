package com.example.controller.submenuView;

import java.io.IOException;

import com.example.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public abstract class SubmenuView extends StackPane{
    @FXML private VBox contentManager;

    public void initUI(String index){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/submenuView/"+index+".fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

    public VBox getContent(){return contentManager;}
}