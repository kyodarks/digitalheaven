package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

import com.example.controller.AppContainer;
import com.example.controller.Login;
import com.example.utils.EmailSender;

public class App extends Application {
    private static Scene scene;
    private static AppContainer appContainer;
    private static Login login;
    private static EmailSender emailSender;

    @Override
    public void start(Stage stage) throws IOException {
        initStatic();
        initUI(stage);
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
    }
    
    public static Login getLoginController(){return login;}
    public static EmailSender getSender(){return emailSender;}

    public static void main(String[] args) {
        launch();
    }
}