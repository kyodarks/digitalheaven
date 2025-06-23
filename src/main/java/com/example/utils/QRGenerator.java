package com.example.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRGenerator {
    public static Image generate(String data, int width, int height) throws WriterException, IOException{
        String charset = new String(data.getBytes("UTF-8"), "UTF-8");

        BitMatrix matrix = new MultiFormatWriter()
            .encode(charset,BarcodeFormat.QR_CODE,width,height);

        BufferedImage swingImage = MatrixToImageWriter.toBufferedImage(matrix);
        Image image = SwingFXUtils.toFXImage(swingImage, null);
        
        return image;
    }
}
