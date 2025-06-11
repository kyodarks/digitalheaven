package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

import com.example.controller.AppContainer;
import com.example.controller.Login;
import com.example.controller.MainApplication;
import com.example.controller.WelcomeMessage;
import com.example.utils.DataBase;
import com.example.utils.EmailSender;

public class App extends Application {
    private static Scene scene;
    private static AppContainer appContainer;
    private static Login login;
    private static MainApplication mainApplication;
    private static WelcomeMessage welcomeMessage;
    private static EmailSender emailSender;

    @Override
    public void start(Stage stage) throws IOException {
        initStatic();
        initUI(stage);
        makeConnections();
    }

    private static void initUI(Stage stage){
        appContainer = new AppContainer();
        login = new Login();

        scene = new Scene(appContainer);
        scene.setFill(Color.TRANSPARENT);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        appContainer.setView(login);
    }
    
    private static void initStatic(){
        emailSender = new EmailSender();
        welcomeMessage = new WelcomeMessage();
        mainApplication = new MainApplication();
        new DataBase();
    }

    private static void makeConnections(){
        welcomeMessage.setOnFinished(e->{
            appContainer.setView(mainApplication);
            mainApplication.enter();
        });
    }

    public static void enterProgram(String userid){
        welcomeMessage.setUserID(userid);
        appContainer.setView(welcomeMessage);
        welcomeMessage.play();
    }

    public static Login getLoginController(){return login;}
    public static EmailSender getSender(){return emailSender;}

    public static void main(String[] args) {
        launch();
    }
}