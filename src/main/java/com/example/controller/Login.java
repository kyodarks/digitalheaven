package com.example.controller;

import java.io.IOException;
import com.example.App;
import com.example.model.FormView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Login extends Pane{
    @FXML private Pane container;

    private LoginHandler loginHandler;
    private SignupHandler signupHandler;
    private PasswordRecoveryHandler passwordRecoveryHandler;

    public Login(){
        initUI();
        makeConnections();
        setView(loginHandler);
    }

    public void setView(FormView view){
        container.getChildren().clear();
        view.clearInput();
        container.getChildren().add((Pane) view);
    }

    private void makeConnections(){
        loginHandler.gotoSignup.setOnMouseClicked(e->{setView(signupHandler);});
        loginHandler.passwordRecoveryButton.setOnMouseClicked(e->{setView(passwordRecoveryHandler);});
        signupHandler.gotoLogin.setOnMouseClicked(e->{setView(loginHandler);});
        passwordRecoveryHandler.gotoLogin.setOnMouseClicked(e->{setView(loginHandler);});
    }

    public LoginHandler getLoginHandler(){return loginHandler;}
    public SignupHandler getSignupHandler(){return signupHandler;}
    public PasswordRecoveryHandler getPasswordRecoveryHandler(){return passwordRecoveryHandler;}
    
    private void initUI(){
        loginHandler = new LoginHandler();
        signupHandler = new SignupHandler();
        passwordRecoveryHandler = new PasswordRecoveryHandler();

        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/Login.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}
