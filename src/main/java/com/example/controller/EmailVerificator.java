package com.example.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.App;
import com.example.model.FormView;
import com.example.utils.DataBase;
import com.example.utils.IntFieldGroup;
import com.example.utils.PopupMessage;
import com.example.utils.TOTPAuth;

public class EmailVerificator extends Pane implements FormView{
    @FXML private Label label;
    @FXML private TextField d1;
    @FXML private TextField d2;
    @FXML private TextField d3;
    @FXML private TextField d4;
    @FXML private TextField d5;
    @FXML private TextField d6;

    @FXML private Button verifyButton;
    @FXML private Button skipButton;
    @FXML private Hyperlink resendCode;

    private final TOTPAuth authenticator;
    private final IntFieldGroup codeInput;
    private final PopupMessage warnMessage;
    private final PopupMessage infoMessage;
    private final PopupMessage verificationSuccessMessage;

    private final String verifyEmailSQL;
    private final String getUserFromEmailSQL;

    private Task<Void> codeCooldown;

    private String emailTarget;
    private String htmlContent;

    public EmailVerificator(){
        try{
        formatContent();}catch(FileNotFoundException e){}
        initUI();

        authenticator = new TOTPAuth(180);
        codeInput = new IntFieldGroup(new TextField[]{d1,d2,d3,d4,d5,d6});
        warnMessage = new PopupMessage(this, "Are you sure? you wont be able to recover your account", 3);
        infoMessage = new PopupMessage(this, "", 3);
        verificationSuccessMessage = new PopupMessage(this, "Account registered!", 3);

        verifyEmailSQL = "UPDATE users SET verified = TRUE, authURI = ? WHERE userid = ?";
        getUserFromEmailSQL = "SELECT userid, username FROM users WHERE email = ?";

        makeConnections();
    }
    
    public void setTarget(String email){
        this.emailTarget = email;
        label.setText(String.format(
            "We've sent a code to %s, enter it below to verify your account.", 
            emailTarget));
    }

    private void makeConnections(){
        verifyButton.setOnMouseClicked(e->{
            boolean correct = authenticator.verify(codeInput.getValue()+"");
            if(correct){
                codeInput.removeErrorStyle();

                try{
                    PreparedStatement getUserStatement = DataBase
                    .getConnection()
                    .prepareStatement(getUserFromEmailSQL);

                    getUserStatement.setString(1, emailTarget);
                    ResultSet result = getUserStatement.executeQuery();
                    boolean success = result.next();

                    if (!success){
                        infoMessage.setMessage("Sorry, something went wrong :( ");
                        infoMessage.show();
                        
                        return;
                    }

                    PreparedStatement verifyStatement = DataBase
                    .getConnection().
                    prepareStatement(verifyEmailSQL);
                    String auth = "";

                    try{
                        auth = authenticator.getAuthURI(result.getString(2));
                    }catch(URISyntaxException x){}

                    verifyStatement.setString(1, auth);
                    verifyStatement.setString(2, result.getString(1));
                    verifyStatement.executeUpdate();
                }catch(SQLException ex){}

                verificationSuccessMessage.setMessage("Verification success!");
                verificationSuccessMessage.show();
            }else{
                infoMessage.setMessage("Sorry, that's not the code :(");
                infoMessage.show();
                codeInput.setErrorStyle();
            }
        });

        skipButton.setOnMouseClicked(e->{
            warnMessage.show();
        });

        resendCode.setOnMouseClicked(e->{
            resendCode.setDisable(true);
            sendCode();
        });

        warnMessage.addOption("Yes", "button-cancel", e->{
            warnMessage.hide();
            verificationSuccessMessage.show();
        });

        verificationSuccessMessage.addOption("Go to login", "", e->{
            Login loginController = App.getLoginController();

            loginController.setView(loginController.getLoginHandler());
        });
        warnMessage.addOption("Cancel", "", e->warnMessage.hide());
        infoMessage.addOption("Ok", "", e-> infoMessage.hide());
    }

    private void formatContent() throws FileNotFoundException{
        StringBuilder builder = new StringBuilder();

        FileReader reader = new FileReader(App.class.getResource("html/VerificationCodeContent.html").getFile());
        BufferedReader br = new BufferedReader(reader);

        try{
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                builder.append(currentLine);
            }

            htmlContent = builder.toString();
            br.close();
        }catch(IOException e){}
    }

    private void startCooldown(){
        codeCooldown = new Task<>(){
            @Override
            protected Void call() throws Exception{
                for (int i = 30; i>= 1; i--){
                    if (isCancelled()){break;}
                    updateMessage(String.format("Resend code (%ds)", i));
                    Thread.sleep(1000);
                }
                
                if (!isCancelled()){
                    System.out.println("countdown finished!!!!!!");
                    Platform.runLater(()->{
                        resendCode.textProperty().unbind();
                        resendCode.setText("Resend Code");
                        resendCode.setDisable(false);
                    });
                }

                return null;
            }
        };

        resendCode.textProperty().bind(codeCooldown.messageProperty());

        Thread thread = new Thread(codeCooldown);
        thread.setDaemon(true);
        thread.start();
    }

    public void sendCode(){
        String code = authenticator.getCode();
        if (codeCooldown != null && codeCooldown.isRunning()) {
             codeCooldown.cancel();
        }

        startCooldown();
        resendCode.setDisable(true);

        App.getSender().send(
            emailTarget, "Digital Heaven Verification [" + code + "]", 
            String.format(htmlContent, code));
        
    }

    public boolean verify(String code){
        return authenticator.verify(code);
    }

    public void clearInput(){
        emailTarget = "";
        verificationSuccessMessage.hide();
        codeInput.resetInput();
        codeInput.removeErrorStyle();
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/EmailVerificator.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }
}