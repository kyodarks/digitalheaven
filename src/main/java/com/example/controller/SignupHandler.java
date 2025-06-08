package com.example.controller;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.model.FormView;
import com.example.utils.PopupMessage;
import com.example.utils.DataBase;
import com.example.utils.EmailValidator;

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
    private PopupMessage errorMessage;
    private FormInputGroup[] inputs;

    private final String getUsersWithUserSQL;
    private final String getUsersWithEmailSQL;
    private final String addUserSQL;

    public SignupHandler(){
        init();
        initUI();
        inputs = new FormInputGroup[]{
            new FormInputGroup(input_user, invalidUserLabel),
            new FormInputGroup(input_email, invalidEmailLabel),
            new FormInputGroup(input_password, invalidPasswordLabel)
        };

        getUsersWithUserSQL = "SELECT userid FROM users WHERE username = ?";
        getUsersWithEmailSQL = "SELECT userid FROM users WHERE email = ?";
        addUserSQL = "INSERT INTO users (username, password, email) VALUES(?, ?, ?)";

        makeConnections();
    }

    private void init(){
        emailVerificator = new EmailVerificator();
    }

    private void makeConnections(){
        signupButton.setOnMouseClicked(e->{
            signupButton.setDisable(true);

            //Verification process
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
                signupButton.setDisable(false);
                return;
            }

            String user = input_user.getText();
            String password = input_password.getText();
            String email = input_email.getText();

            boolean validEmail = EmailValidator.isValid(email);
            if (!validEmail){
                input_email.getStyleClass().add("error-field");
                invalidEmailLabel.setText("invalid email*");
                invalidEmailLabel.setVisible(true);
                invalidInput = true;
            }
            
            boolean userAlreadyInUse = false;

            try{
                PreparedStatement statement = DataBase.getConnection().prepareStatement(getUsersWithUserSQL);
                statement.setString(1, user);
                ResultSet result = statement.executeQuery();
                userAlreadyInUse = result.next();
            }catch(SQLException ex){}

            if (userAlreadyInUse){
                input_user.getStyleClass().add("error-field");
                invalidUserLabel.setText("user already registered*");
                invalidUserLabel.setVisible(true);
                invalidInput = true;
            }

            boolean emailAlreadyInUse = false;

            try{
                PreparedStatement statement = DataBase.getConnection().prepareStatement(getUsersWithEmailSQL);
                statement.setString(1, email);
                ResultSet result = statement.executeQuery();
                emailAlreadyInUse = result.next();
            }catch(SQLException ex){}

            if (emailAlreadyInUse){
                input_email.getStyleClass().add("error-field");
                invalidEmailLabel.setText("email already registered*");
                invalidEmailLabel.setVisible(true);
                invalidInput = true;
            }

            if (invalidInput){
                signupButton.setDisable(false);
                return;
            }
            
            try{
                PreparedStatement statement = DataBase.getConnection().prepareStatement(addUserSQL);
                statement.setString(1, user);
                statement.setString(2, password);
                statement.setString(3, email);

                statement.executeUpdate();
            }catch(SQLException ex){}

            App.getLoginController().setView(emailVerificator);
            emailVerificator.setTarget(email);
            emailVerificator.sendCode();
        });

        errorMessage.addOption("Back", "", e->{
            errorMessage.hide();
        });
    }

    public void clearInput(){
        for (FormInputGroup x : inputs){x.clear();}

        signupButton.setDisable(false);
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

        errorMessage = new PopupMessage(this, "", 3);
    }

    private static class FormInputGroup {
        private Label label;
        private TextField input;

        public FormInputGroup(TextField input, Label message){
            this.input = input;
            this.label = message;
        }

        public Label getLabel(){return label;}
        public TextField getInput(){return input;}
        public void clear(){
            input.clear();
            input.getStyleClass().remove("error-field");
            label.setVisible(false);
        }
    }
}
