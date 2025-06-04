package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.example.App;
import com.example.model.FormView;
import com.example.utils.IntFieldGroup;
import com.example.utils.TOTPAuth;

import jakarta.mail.MessagingException;

public class EmailVerificator extends Pane implements FormView{
    @FXML private Label label;
    @FXML private TextField d1;
    @FXML private TextField d2;
    @FXML private TextField d3;
    @FXML private TextField d4;
    @FXML private TextField d5;
    @FXML private TextField d6;

    private final TOTPAuth authenticator;
    private final IntFieldGroup codeInput;
    private String emailTarget;
    private String htmlContent;

    public EmailVerificator(){
        try{
        formatContent();}catch(FileNotFoundException e){}
        initUI();

        authenticator = new TOTPAuth(180);
        codeInput = new IntFieldGroup(new TextField[]{d1,d2,d3,d4,d5,d6});
    }
    
    public void setTarget(String email){
        this.emailTarget = email;
        label.setText(String.format(
            "We've sent a code to %s, enter it below to verify your account.", 
            emailTarget));
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

    public void sendCode(){
        try{
            String code = authenticator.reserveCode();
            App.getSender().send(
                emailTarget, "Digital Heaven Verification [" + code + "]", 
                String.format(htmlContent, code));
        }catch(MessagingException e){e.printStackTrace();}
    }

    public boolean verify(String code){
        return authenticator.verify(code);
    }

    public void clearInput(){

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