package com.example.utils;

import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProductIcon {
    private ArrayList<IconContainer> icons;

    public ProductIcon(){
        icons = new ArrayList<>();
    }

    public void add(WritableImage newIcon){
        IconContainer container = new IconContainer();
        container.setIconIndex(icons.size());

        container.add(newIcon, 260);
        container.add(newIcon, 150);
        container.add(newIcon, 45);
        icons.add(container);
    }

    public int getIconAmount(){return icons.size();}

    public WritableImage getIcon(int iconIndex, int sizeIndex){
        return  SwingFXUtils
            .toFXImage(icons.get(iconIndex).getSize(sizeIndex), null);
    }

    public ArrayList<IconContainer> makeFiles(String filePath, String fileName){
        String home = "/"+fileName.replace(" ", "_").toLowerCase();

        for (int index=0;index<icons.size();index++){
            IconContainer x = icons.get(index);

            for (Icon icon : x.getIcons()){
                BufferedImage image = icon.getImage();
                icon.makePath(filePath, home);

                File dir = new File(icon.getPath());
                if (!dir.exists()){dir.mkdirs();}

                try{
                    ImageIO.write(image, "png", dir);
                }catch(IOException e){}
            }
        }

        return icons;
    }

    public static class IconContainer{
        private ArrayList<Icon> icons;
        private int iconIndex;

        public IconContainer(){
            icons = new ArrayList<>();
        }
        public void add(WritableImage newIcon, int resolution){
            Icon x = new Icon(resize(newIcon, resolution), ""+iconIndex);
            x.setResolution(resolution);
            icons.add(x);
        }
        public BufferedImage getSize(int sizeIndex){
            return icons.get(sizeIndex).getImage();
        }
    
        public ArrayList<Icon> getIcons(){
            return icons;
        }    
    
        private BufferedImage resize(WritableImage icon, int size){
            BufferedImage buff = SwingFXUtils.fromFXImage(icon, null);
        
            return Scalr.resize(buff, Scalr.Method.AUTOMATIC, size, size);
        }

        public void setIconIndex(int index){this.iconIndex = index;}
        public int getIconIndex(){return iconIndex;}
    }

    public static class Icon{
        private BufferedImage image;
        private String resolution;
        private String path;
        private String iconIndex;

        public Icon(BufferedImage image, String iconIndex){
            this.image = image;
            this.iconIndex = iconIndex;
        }
        public void setResolution(int resolution){
            this.resolution = ""+resolution;
        }
        public void setPath(String path){
            this.path = path;
        }

        public BufferedImage getImage(){return image;}
        public String getResolution(){return resolution;}
        public String getPath(){return path;}

        public void makePath(String filePath, String home){
            path= filePath+home+"/"+home+iconIndex+home+resolution + ".png";
        }
    }
}
