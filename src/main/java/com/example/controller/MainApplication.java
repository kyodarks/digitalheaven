package com.example.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.example.App;
import com.example.controller.submenu.*;
import com.example.utils.EasingStyle;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.embed.swing.SwingNode;
import javafx.util.Duration;

public class MainApplication extends Pane{
    @FXML private HBox profileButotn;
    @FXML private SwingNode profileIcon;
    @FXML private Label profileLabel;
    @FXML private Pane submenuContainer;

    private SubMenu selectedSubmenu;
    private SubMenu[] submenus;
    private Timeline initAnimation;

    private HashMap<String, SubMenu> submenuCatalog;

    private String user;
    private boolean firtJoin;

    public MainApplication(){
        firtJoin = true;
        submenuCatalog = new HashMap<>();

        initUI();
        initAnimation();
    }

    private void initAnimation(){
        initAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(opacityProperty(), 0),
                new KeyValue(layoutXProperty(), 100),
                new KeyValue(layoutYProperty(), 20)),
            new KeyFrame(Duration.millis(500), 
                new KeyValue(opacityProperty(), 1, EasingStyle.OutQuad),
                new KeyValue(layoutXProperty(), 0, EasingStyle.OutQuad),
                new KeyValue(layoutYProperty(), 0, EasingStyle.OutQuad))
        );
    }

    public void enter(String userid){
        user = userid;

        initAnimation.play();
        if (firtJoin){
            firtJoin = false;
            loadSubmenus();
        }
    }

    private void initUI(){
        FXMLLoader loader = new 
        FXMLLoader(App.class.getResource(
                "fxml/MainApplicationUI.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try{
            loader.load();
        }catch (IOException e){}
    }

    private void loadSubmenus(){
        submenus = new SubMenu[]{
            new SubMenu(this, new Home(), "home"),
            new SubMenu(this, new Shop(), "shop"),
            new SubMenu(this, new Dashboard(), "dashboard"),
            new SubMenu(this, new History(), "history"),
            new SubMenu(this, new Profile(), "profile"),
            new SubMenu(this, new Admin(), "admin")
        };

        submenuCatalog.put("home", submenus[0]);
        submenuCatalog.put("shop", submenus[1]);
        submenuCatalog.put("dashboard", submenus[2]);
        submenuCatalog.put("history", submenus[3]);
        submenuCatalog.put("profile", submenus[4]);
        submenuCatalog.put("admin", submenus[5]);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(submenuContainer.widthProperty());
        clip.heightProperty().bind(submenuContainer.heightProperty());
        clip.setArcWidth(20 * 2);
        clip.setArcHeight(20 * 2);
        submenuContainer.setClip(clip);

        for (SubMenu submenu : submenus){
            submenu.getController().setupUser(user);
            submenu.button.setOnMouseClicked(e->{
                submenu.getController().onEnter();
                switchMenu(submenu);
            });
        }

        switchMenu(submenus[0]);
    }

    public void setView(Node view){
        submenuContainer.getChildren().add(view);
    }

    public void removeView(Node view){
        submenuContainer.getChildren().remove(view);
    }
    
    public void gotoSubmenu(String submenu){
        SubMenu x = submenuCatalog.get(submenu);
        if (x != null){
            switchMenu(x);
        }
    }

    public SubMenuController getSubMenu(String submenu){
        SubMenu x = submenuCatalog.get(submenu);
        if (x != null){
            return x.getController();
        }

        return null;
    }

    private void switchMenu(SubMenu menu){
        if (menu == selectedSubmenu){return;}

        if (selectedSubmenu != null){
            selectedSubmenu.deselect();
        }
        selectedSubmenu = menu;
        selectedSubmenu.select();
    }

    private static class SubMenu {
        private String name;
        private Label label;
        private FlatSVGIcon icon;
        private SwingNode swingIcon;
        private HBox button;

        private MainApplication parent;
        private SubMenuController controller;

        private boolean selected;

        public SubMenu(MainApplication main, SubMenuController controller, String name){
            this.name = name;
            this.controller = controller;
            this.label = (Label) main.lookup("#"+name+"Label");
            this.swingIcon = (SwingNode) main.lookup("#"+name+"Icon");
            this.button = (HBox) main.lookup("#"+name+"Button");
            this.parent = main;

            initIcon();
            
            label.getStyleClass().add("label-deselected");
            button.getStyleClass().remove("submenu-button-selected");

            swingIcon.setOpacity(.5);
        }

        public void select(){
            if (selected){return;}
            selected = true;

            parent.setView(controller);
            controller.playSelectAnimation();
            button.getStyleClass().add("submenu-button-selected");
            label.getStyleClass().remove("label-deselected");

            swingIcon.setOpacity(1);
        }

        public void deselect(){
            if (!selected){return;}
            selected = false;

            parent.removeView(controller);
            label.getStyleClass().add("label-deselected");
            button.getStyleClass().remove("submenu-button-selected");

            swingIcon.setOpacity(.5);
        }
        
        private void initIcon(){
            SwingUtilities.invokeLater(()->{
                icon = new FlatSVGIcon(
                    "com/example/icons/"+name+".svg", 20, 20);
                JLabel label = new JLabel(icon);
                label.setOpaque(false);

                Platform.runLater(() -> swingIcon.setContent(label));
            });
        }
    
        public SubMenuController getController(){
            return controller;
        }
    }
}
