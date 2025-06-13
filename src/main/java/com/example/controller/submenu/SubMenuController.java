package com.example.controller.submenu;

import java.io.IOException;

import com.example.App;
import com.example.utils.EasingStyle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class SubMenuController extends StackPane{
    private Timeline selectAnimation;

    public SubMenuController(){
        initAnimation();
    }

    public void playSelectAnimation(){
        selectAnimation.play();
    }

    private void initAnimation(){
        selectAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(opacityProperty(), 0),
                new KeyValue(layoutXProperty(), 50),
                new KeyValue(layoutYProperty(), 10)),
            new KeyFrame(Duration.millis(250), 
                new KeyValue(opacityProperty(), 1, EasingStyle.OutSine),
                new KeyValue(layoutXProperty(), 0, EasingStyle.OutSine),
                new KeyValue(layoutYProperty(), 0, EasingStyle.OutSine))
        );
    }

    public void initUI(String index){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/submenu/"+index+".fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}
