package com.example.controller;

import java.io.IOException;

import com.example.App;
import com.example.utils.EasingStyle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MainApplication extends Pane{
    private Timeline initAnimation;

    public MainApplication(){
        initUI();
        initAnimation();
    }

    private void initAnimation(){
        initAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(opacityProperty(), 0),
                new KeyValue(layoutXProperty(), 100),
                new KeyValue(layoutYProperty(), 20)),
            new KeyFrame(Duration.millis(500), 
                new KeyValue(opacityProperty(), 1, EasingStyle.OutQuad),
                new KeyValue(layoutXProperty(), 0, EasingStyle.OutQuad),
                new KeyValue(layoutYProperty(), 0, EasingStyle.OutQuad))
        );
    }

    public void enter(){
        initAnimation.play();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/MainApplicationUI.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

}
