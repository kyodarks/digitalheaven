package com.example.controller;

import java.io.IOException;

import com.example.App;
import com.example.model.FormView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class PasswordRecoveryHandler extends Pane implements FormView{
    @FXML public TextField input_email;
    @FXML public Button findAccountButton;
    @FXML public Label invalidEmailLabel;
    @FXML public Hyperlink gotoLogin;

    public PasswordRecoveryHandler(){
        initUI();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/PasswordRecoveryHandler.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

    public void clearInput(){

    }
}