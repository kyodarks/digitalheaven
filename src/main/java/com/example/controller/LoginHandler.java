package com.example.controller;

import java.io.IOException;

import com.example.App;
import com.example.model.FormView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class LoginHandler extends Pane implements FormView{
    @FXML public TextField input_user;
    @FXML public PasswordField input_password;
    @FXML public Button loginButton;
    @FXML public Hyperlink passwordRecoveryButton;
    @FXML public Hyperlink gotoSignup;
    @FXML public Label invalidUserLabel;
    @FXML public Label invalidPasswordLabel;

    public LoginHandler(){
        initUI();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/LoginHandler.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

    public void clearInput(){

    }
}
