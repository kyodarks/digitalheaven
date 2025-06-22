package com.example.utils;

import java.io.IOException;

import com.example.App;
import com.example.model.Event;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ImageCropper extends VBox{
    @FXML Pane container;
    @FXML Pane imageContainer;
    @FXML ImageView imageBack;
    @FXML ImageView imageFront;
    @FXML Button cancel;
    @FXML Button upload;

    private Timeline showAnimation;
    private FitType fitType;

    private Rectangle clip;
    private Image image;

    private double x;
    private double y;

    private double lastX;
    private double lastY;

    private double scale;
    private double ratio;

    double currentCropWidth;
    double currentCropHeight;

    private double dragSpeed;

    private Event onCropCancel;
    private Event onCropSuccess;

    public ImageCropper(){
        initUI();
        initAnimations();
        makeConnections();

        dragSpeed = 1;
    }

    public void show(){
        showAnimation.play();
    }

    public void hide(){
        setVisible(false);
        container.setVisible(false);
    }

    public void setRadius(double radius){
        double arc = 280 * radius * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);
    }

    public void setImage(Image image){
        this.image = image;

        x=0;
        y=0;
        scale=1;

        chooseFitType();

        redraw();
    }
    
    private void makeConnections(){
        imageContainer.setOnScroll(e->{
            if (image==null){return;}
            double delta = e.getDeltaY();

            if (delta>0){scale = clamp(scale-=.05, .2, 1);}
            else{scale = clamp(scale+=.05, .2, 1);}

            double cropWidth = getCropWidth();
            double cropHeight = getCropWidth();

            x = clamp(x-=(cropWidth-currentCropWidth) / 2, 0, image.getWidth() - cropWidth);
            y = clamp(y-=(cropHeight-currentCropHeight), 0, image.getHeight() - cropHeight);

            currentCropWidth = cropWidth;
            currentCropHeight = cropHeight;

            redraw();
        });
        imageContainer.setOnMousePressed(e->{
            lastX = e.getX();
            lastY= e.getY();
        });
        imageContainer.setOnMouseDragged(e->{
            double step = dragSpeed * ratio;
            
            double cx = e.getX();
            double cy = e.getY();

            double dx = (cx-lastX) * step;
            double dy = (cy-lastY) * step;

            lastX = cx;
            lastY = cy;

            x = clamp(x-dx, 0, image.getWidth()- currentCropWidth);
            y = clamp(y-dy, 0, image.getHeight()- currentCropHeight);

            redraw();
        });
        cancel.setOnMouseClicked(e->{
            hide();
            if (onCropCancel!=null){onCropCancel.call();}
        });
        upload.setOnMouseClicked(e->{
            hide();
            if (onCropSuccess!=null){onCropSuccess.call();}
        });
    }

    private void chooseFitType(){
        double width = image.getWidth();
        double height = image.getHeight();
        
        if (height >= width){
            ratio = width / height;
            fitType = FitType.Y;
        }else{
            ratio = height / width;
            fitType = FitType.X;
        }

        currentCropWidth = getCropWidth();
        currentCropHeight = getCropHeight();
    }

    private void redraw(){
        WritableImage cropPreview = getCroppedImage();
        imageFront.setImage(cropPreview);
        imageBack.setImage(cropPreview);
    }

    private double getCropWidth(){
        switch (fitType.toString()) {
            case "X" : return image.getWidth() * ratio * scale;
            default : return image.getWidth() * scale;
        }
    }

    private double getCropHeight(){
        switch (fitType.toString()) {
            case "Y" : return image.getHeight() * ratio * scale;
            default : return image.getHeight() * scale;
        }
    }

    public WritableImage getCroppedImage(){
        return new WritableImage(
            image.getPixelReader(),
            (int) x, 
            (int) y, 
            (int) getCropWidth(), 
            (int) getCropHeight());
    }

    public void setOnCropCancel(Event e){
        onCropCancel = e;
    }

    public void setOnCropSuccess(Event e){
        onCropSuccess = e;
    }

    private double clamp(double value, double min, double max) {
        if (value < min) {return min;} 
        else if (value > max){return max;}
        else{return value;}
    }

    private void initAnimations(){
        showAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                e->{
                    container.setVisible(true);
                    setVisible(true);
                }, 
                
                new KeyValue(opacityProperty(), 0),
                new KeyValue(container.scaleXProperty(), .5),
                new KeyValue(container.scaleYProperty(), .5)
            ),

            new KeyFrame(Duration.millis(250), 
                new KeyValue(opacityProperty(), 1, EasingStyle.OutSine),
                new KeyValue(container.scaleXProperty(), 1, EasingStyle.OutSine),
                new KeyValue(container.scaleYProperty(), 1, EasingStyle.OutSine)
            )
        );
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/ImageCropper.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}

        ColorAdjust x = new ColorAdjust();
        x.setBrightness(-.7);
        imageBack.setEffect(x);

        clip = new Rectangle();
        clip.setWidth(280);
        clip.setHeight(280);

        imageFront.setClip(clip);
    }

    private enum FitType{X,Y}
}
