package com.example.controller;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.utils.DataBase;
import com.example.utils.EasingStyle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class WelcomeMessage extends Pane{
    @FXML private Label welcomeMessage;
    @FXML private Label status;
    @FXML private HBox submessage;

    private Timeline welcomeAnimation;
    private String userid;
    
    private final String getUserDetailsSQL;

    public WelcomeMessage(){
        initUI();
        initAnimation();

        getUserDetailsSQL = "SELECT username, displayname, usertype FROM users WHERE userid = ?";
    }

    public void setUserID(String userid){
        this.userid = userid;

        try{
            PreparedStatement statement = DataBase
            .getConnection().prepareStatement(getUserDetailsSQL);
            statement.setString(1, this.userid);

            ResultSet result = statement.executeQuery();
            if (result.next()){
                String username = result.getString(1);
                String displayName = result.getString(2);
                String userType = result.getString(3);

                welcomeMessage.setText("Welcome @"+(displayName!=null?displayName:username));
                status.setText(userType);
            }
        }catch(SQLException e){e.printStackTrace();}
    }

    public String getUserID(){return userid;}

    private void initAnimation(){
        welcomeAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(welcomeMessage.opacityProperty(), 0),
                new KeyValue(submessage.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(1500), 
                new KeyValue(submessage.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(4000), 
                new KeyValue(welcomeMessage.opacityProperty(), 1, EasingStyle.OutBounce)),
            new KeyFrame(Duration.millis(5000),
                new KeyValue(welcomeMessage.opacityProperty(), 1),
                new KeyValue(submessage.opacityProperty(), 1, EasingStyle.OutElastic)),
            new KeyFrame(Duration.millis(5500),
                new KeyValue(welcomeMessage.opacityProperty(), 0, EasingStyle.OutSine),
                new KeyValue(submessage.opacityProperty(), 0, EasingStyle.OutSine))
        );
    }

    public void setOnFinished(EventHandler<ActionEvent> e){
        welcomeAnimation.setOnFinished(e);
    }

    public void play(){
        welcomeAnimation.play();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/WelcomeMessage.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}
