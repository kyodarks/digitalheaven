package com.example.utils;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageScalator {
    public static Image scale(Image img, int resolution){
        BufferedImage buff = SwingFXUtils.fromFXImage(img, null);
        BufferedImage scaled = Scalr.resize(buff, Scalr.Method.AUTOMATIC, resolution, resolution);

        return SwingFXUtils.toFXImage(scaled, null);
    }
}
