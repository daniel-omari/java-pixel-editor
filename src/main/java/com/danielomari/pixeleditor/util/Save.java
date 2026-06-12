package com.danielomari.pixeleditor.util;

import com.danielomari.pixeleditor.PixelGraphicEditor;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class Save {
    private static boolean hasSaved = false;
    private static boolean hasSavedRecently = false;
    private static File lastSavedFile = null;
    private static String saveAs = "Save As";


    // Save the image to a file
    public static void saveImage() {
        if (lastSavedFile == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle(saveAs);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG files", "jpg", "jpeg"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP files", "bmp"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG files", "png"));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                lastSavedFile = fileChooser.getSelectedFile();
                File file = fileChooser.getSelectedFile();
                String defaultFileFormat = "png";

                if (fileChooser.getFileFilter().getDescription().equals("JPEG files")) {
                    defaultFileFormat = "jpg";
                    if (!file.getName().toLowerCase().endsWith(".jpg") && !file.getName().endsWith(".jpeg")) {
                        file = new File(file.getAbsolutePath() + ".jpg");
                    }
                } else if (fileChooser.getFileFilter().getDescription().equals("BMP files")) {
                    defaultFileFormat = "bmp";
                    if (!file.getName().toLowerCase().endsWith(".bmp")) {
                        file = new File(file.getAbsolutePath() + ".bmp");
                    }
                } else if (fileChooser.getFileFilter().getDescription().equals("PNG files")) {
                    defaultFileFormat = "png";
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                }

//                BufferedImage image = new BufferedImage(PixelGraphicEditor.getCanvas().getWidth(), PixelGraphicEditor.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
                CanvasPanel.getInstance().setZoom(1.0f); // Reset zoom to 1.0 before saving
                BufferedImage image = new BufferedImage(CanvasPanel.getInstance().getWidth(), CanvasPanel.getInstance().getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                PixelGraphicEditor.getCanvas().paint(g2d);
                g2d.dispose();

                try {
                    ImageIO.write(image, defaultFileFormat, file);
                    hasSaved = true;
                    lastSavedFile = file; // Update the last saved file after saving
                    System.out.println("Image saved to: " + file.getAbsolutePath());
                    hasSavedRecently = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // If the file has been previously saved, just overwrite it
            File file = lastSavedFile;
            String defaultFileFormat = "png";

            if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")) {
                defaultFileFormat = "jpg";
            } else if (file.getName().toLowerCase().endsWith(".bmp")) {
                defaultFileFormat = "bmp";
            } else if (file.getName().toLowerCase().endsWith(".png")) {
                defaultFileFormat = "png";
            }

            CanvasPanel.getInstance().setZoom(1.0F); // Reset zoom to 1.0 before saving

            BufferedImage image = new BufferedImage(PixelGraphicEditor.getCanvas().getWidth(),
                    PixelGraphicEditor.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            PixelGraphicEditor.getCanvas().paint(g2d);
            g2d.dispose();

            try {
                ImageIO.write(image, defaultFileFormat, file);
                System.out.println("Image saved to: " + file.getAbsolutePath());
                hasSavedRecently = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void resetSave() {
        lastSavedFile = null;
        hasSaved = false;
    }

    public static void getResetSave() {
        resetSave();
    }

    public static void saveImageAs() {
        lastSavedFile = null;
        hasSaved = false;
        saveImage();
    }

    public static boolean hasSaved() {
        return hasSaved;
    }


    public static boolean getHasSavedRecently() {
        return hasSavedRecently; // Temp
    }
}