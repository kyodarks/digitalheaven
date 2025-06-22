package com.example.utils;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.model.Event;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Grid {
    private Pane container;

    private Vector2 itemSize;
    private Vector2 itemGap;

    private int columns;

    private ArrayList<Vector2> positions;
    private ArrayList<GridItem> items;
    private ArrayList<GridItem> staticItems;

    public Grid(Pane container){
        columns = 4;
        itemSize = new Vector2(140,190);
        itemGap = new Vector2(8,8);

        items = new ArrayList<>();
        positions = new ArrayList<>();
        staticItems = new ArrayList<>();

        this.container = container;
    }

    private void calcPositions(){
        positions.clear();
        int totalItems = items.size() + staticItems.size();
        int rows = (int) Math.ceil((double) totalItems / columns);

        for (int y=0; y<rows;y++){
            for (int x=0; x<columns;x++){
                positions.add(new Vector2(
                    (itemSize.getX()+itemGap.getX())*x,
                    (itemSize.getY()+itemGap.getY())*y
                 ));
            }
        }
    }

    private void draw(){
        int index = 0;

        for (GridItem item : staticItems){
            if (item.isHidden()){continue;}
            item.gotTo(positions.get(index));

            index++;
        }
        for (GridItem item : items){
            if (item.isHidden()){continue;}
            item.gotTo(positions.get(index));

            index++;
        }
    }

    private void onItemAdded(GridItem newItem){
        //newItem.setOnHide(()->{
            //update();
        //});
        //newItem.setOnShow(()->{
            //update();
        //});
    }

    public void add(GridItem newItem){
        container.getChildren().add(newItem.getNode());
        items.add(newItem);
        onItemAdded(newItem);
        update();
    }

    public void addStatic(GridItem newItem){
        container.getChildren().add(newItem.getNode());
        staticItems.add(newItem);
        onItemAdded(newItem);
        update();
    }

    public void remove(GridItem item){
        container.getChildren().remove(item.getNode());
        items.remove(item);

        update();
    }

    public void removeStatic(GridItem item){
        container.getChildren().remove(item.getNode());
        staticItems.remove(item);

        update();
    }

    private void update(){
        calcPositions();
        draw();
    }

    public void clear(){
        positions.clear();
        Iterator<GridItem> it = items.iterator();
        while(it.hasNext()){
            GridItem x = it.next();
            container.getChildren().remove(x.getNode());
        }

        items.clear();
    }

    public void setColumns(int columns){
        this.columns = columns;
        update();
    }

    public Vector2 getItemSize(){return itemSize;}
    public Vector2 getItemGap(){return itemGap;}

    public static class Vector2{
        private double x;
        private double y;

        public Vector2(){x=0;y=0;}
        public Vector2(double x, double y){this.x=x;this.y=y;}

        public double getX(){return x;}
        public double getY(){return y;}

        @Override
        public String toString(){
            return x + ":" + y;
        }
    }
    public static class GridItem{
        private final Node item;
        private boolean hidden;

        private double animationLength;
        
        private Timeline scaleAnimation;
        private Timeline positionAnimation;

        private Event onHide;
        private Event onShow;

        public GridItem(Node item){
            animationLength = 250;
            this.item = item;
        }

        private void stopScaleAnimation(){
            if (scaleAnimation != null && 
                scaleAnimation.getStatus() == Timeline.Status.RUNNING)
            {scaleAnimation.stop();}
        }

        private void stopPositionAnimation(){
            if (positionAnimation != null && 
                positionAnimation.getStatus() == Timeline.Status.RUNNING)
            {positionAnimation.stop();}
        }

        public void hide(){
            if (hidden){return;}
            hidden = true;
            if (onHide!=null){onHide.call();}

            stopScaleAnimation();
            scaleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(item.scaleXProperty(), item.getScaleX()),
                    new KeyValue(item.scaleYProperty(), item.getScaleY())), 
                new KeyFrame(Duration.millis(animationLength), 
                    new KeyValue(item.scaleXProperty(), 0, EasingStyle.OutSine),
                    new KeyValue(item.scaleYProperty(), 0, EasingStyle.OutSine))
            );

            scaleAnimation.setOnFinished(e->{
                item.setVisible(false);
            });

            scaleAnimation.play();
        }
        public void show(){
            if (!hidden){return;}

            hidden = false;
            item.setVisible(true);
            if (onShow!=null){onShow.call();}
            stopScaleAnimation();

            scaleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(item.scaleXProperty(), item.getScaleX()),
                    new KeyValue(item.scaleYProperty(), item.getScaleY())), 
                new KeyFrame(Duration.millis(animationLength), 
                    new KeyValue(item.scaleXProperty(), 1, EasingStyle.OutSine),
                    new KeyValue(item.scaleYProperty(), 1, EasingStyle.OutSine))
            );

            scaleAnimation.play();
        }
        public void gotTo(Vector2 position){
            if(item.getLayoutX()==position.getX() 
            && item.getLayoutY()==position.getY()){return;}

            stopPositionAnimation();

            positionAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(item.layoutXProperty(), item.getLayoutX()),
                    new KeyValue(item.layoutYProperty(), item.getLayoutY())), 
                new KeyFrame(Duration.millis(animationLength), 
                    new KeyValue(item.layoutXProperty(), position.getX(), EasingStyle.OutSine),
                    new KeyValue(item.layoutYProperty(), position.getY(), EasingStyle.OutSine))
            );

            positionAnimation.play();
        }

        public Node getNode(){return item;}

        public boolean isHidden(){return hidden;}

        public void setOnHide(Event e){this.onHide = e;}
        public void setOnShow(Event e){this.onShow = e;}

        public void setAnimationLength(double length){
            animationLength = length;
        }
    }
}
