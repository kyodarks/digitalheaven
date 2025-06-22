package com.example.controller;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.model.FormView;
import com.example.utils.DataBase;
import com.example.utils.FormInputGroup;
import com.example.utils.PopupMessage;

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

    private FormInputGroup[] inputs;
    private String validateUserSQL;
            
    private final PopupMessage infoMessage;

    public LoginHandler(){
        initUI();
        initQuerys();
        
        infoMessage = new PopupMessage(this, "", 3);

        inputs = new FormInputGroup[]{
            new FormInputGroup(input_user, invalidUserLabel),
            new FormInputGroup(input_password, invalidPasswordLabel)
        };

        makeConnections();
    }

    private void initQuerys(){
        validateUserSQL =  "SELECT userid FROM users WHERE (username = ?) OR (email = ?) AND password = ?";
    }

    public void makeConnections(){
        loginButton.setOnMouseClicked(e->{
            loginButton.setDisable(true);

            boolean invalidInput = false;

            for (FormInputGroup x : inputs){
                TextField field = x.getInput();
                Label message = x.getLabel();

                field.getStyleClass().remove("error-field");
                message.getStyleClass().remove("error-label");
                message.setVisible(false);

                if (field.getText().isBlank()){
                    message.setText("required*");
                    field.getStyleClass().add("error-field");
                    message.getStyleClass().add("error-label");
                    message.setVisible(true);

                    invalidInput = true;
                }
            }

            if (invalidInput){
                loginButton.setDisable(false);
                return;
            }

            String user = input_user.getText();
            String password = input_password.getText();
           
            boolean userFound = false;
            String userid = "0";

            try{
                PreparedStatement statement = DataBase
                .getConnection().prepareStatement(validateUserSQL);

                statement.setString(1, user);
                statement.setString(2, user);
                statement.setString(3, password);

                ResultSet result = statement.executeQuery();
                userFound = result.next();
                if (userFound){userid = result.getString(1);}

            }catch(SQLException ex){ex.printStackTrace();}

            if (!userFound){
                infoMessage.setMessage("ⓘ Invalid user or password");
                infoMessage.show();

                for (FormInputGroup x : inputs){
                    TextField field = x.getInput();
                    Label message = x.getLabel();

                    field.getStyleClass().add("error-field");
                    message.getStyleClass().add("error-label");
                }

                loginButton.setDisable(false);
                return;
            }

            clearInput();
            App.enterProgram(userid);
        });
    
        infoMessage.addOption("Ok", "", e-> infoMessage.hide());
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
        for (FormInputGroup x : inputs){x.clear();}

        loginButton.setDisable(false);
    }
}
