package com.example.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FormInputGroup{
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