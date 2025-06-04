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

public class SignupHandler extends Pane implements FormView{
    @FXML public TextField input_user;
    @FXML public PasswordField input_password;
    @FXML public TextField input_email;
    @FXML public Button signupButton;
    @FXML public Label invalidUserLabel;
    @FXML public Label invalidPasswordLabel;
    @FXML public Label invalidEmailLabel;
    @FXML public Hyperlink gotoLogin;

    private EmailVerificator emailVerificator;

    public SignupHandler(){
        init();
        initUI();
        makeConnections();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/SignupHandler.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

    private void init(){
        emailVerificator = new EmailVerificator();
    }

    private void makeConnections(){
        signupButton.setOnMouseClicked(e->{
            App.getLoginController().setView(emailVerificator);

            //emailVerificator.setTarget(input_email.getText());
            //emailVerificator.sendCode();
        });
    }

    public void clearInput(){

    }
}
