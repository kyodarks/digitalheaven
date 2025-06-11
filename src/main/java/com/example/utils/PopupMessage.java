package com.example.utils;

import java.io.IOException;
import java.util.ArrayList;

import com.example.App;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PopupMessage extends VBox{
    @FXML private Label label;
    @FXML private HBox buttonContainer;

    private Pane glassPane;
    private Pane container;
    private ArrayList<Button> actionButtons;
    private Timeline showAnimation;

    private final double borderRadius;

    public PopupMessage(Pane container, String message, double borderRadius){
        this.container = container;
        this.borderRadius = borderRadius;
        actionButtons = new ArrayList<>();

        initUI();
        initAnimations();

        label.setText(message);
    }

    public void show(){
        calcPosition();
        updateOptionsLayout();

        showAnimation.play();
    }

    public void hide(){
        glassPane.setVisible(false);
        setVisible(false);
    }

    public void setMessage(String message){
        label.setText(message);
    }

    public void addOption(String optionName, String buttonStyleClass, EventHandler<ActionEvent> handler){
        Button option = new Button(optionName);
        option.getStyleClass().add(buttonStyleClass);
        option.setOnAction(handler);
        option.setCursor(Cursor.HAND);
        option.getStyleClass().add("semibold-text");
        actionButtons.add(option);

        buttonContainer.getChildren().add(option);
    }

    private void calcPosition(){
        double widht = glassPane.getPrefWidth();
        double height = glassPane.getPrefHeight();

        setLayoutX(widht/2 - getPrefWidth()/2);
        setLayoutY(height/2 - getHeight()/2);
    }

    private void initAnimations(){
        showAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                e->{
                    glassPane.setVisible(true);
                    setVisible(true);
                }, 
                
                new KeyValue(glassPane.opacityProperty(), 0),
                new KeyValue(scaleXProperty(), .5),
                new KeyValue(scaleYProperty(), .5)
            ),

            new KeyFrame(Duration.millis(250), 
                new KeyValue(glassPane.opacityProperty(), 1, EasingStyle.OutSine),
                new KeyValue(scaleXProperty(), 1, EasingStyle.OutSine),
                new KeyValue(scaleYProperty(), 1, EasingStyle.OutSine)
            )
        );
    }

    private void updateOptionsLayout(){
        double width = getWidth();
        double height = buttonContainer.getHeight();

        int optionAmount = actionButtons.size();

        double buttonSize = width / optionAmount;

        for(Button button : actionButtons){
            button.setPrefSize(buttonSize, height + 2);
        }
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/PopupMessage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}

        glassPane = new Pane(this);
        glassPane.getStyleClass().add("glassPane");
        glassPane.setStyle("-fx-background-radius:"+borderRadius+"em;");

        container.getChildren().add(glassPane);
        glassPane.setVisible(false);
        glassPane.prefHeightProperty().bind(container.prefHeightProperty());
        glassPane.prefWidthProperty().bind(container.prefWidthProperty());        
    }
}
