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
                // Save the flattened document (all visible layers), not the panel.
                BufferedImage image = renderForSave(defaultFileFormat);

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

            // Save the flattened document (all visible layers), not the panel.
            BufferedImage image = renderForSave(defaultFileFormat);

            try {
                ImageIO.write(image, defaultFileFormat, file);
                System.out.println("Image saved to: " + file.getAbsolutePath());
                hasSavedRecently = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Flatten the layers for export. PNG keeps transparency; JPEG/BMP have no
    // alpha channel, so those are composited onto a white background.
    private static BufferedImage renderForSave(String format) {
        BufferedImage flat = CanvasPanel.getInstance().getFlattenedImage();
        if ("png".equals(format)) {
            return flat;
        }
        BufferedImage rgb = new BufferedImage(flat.getWidth(), flat.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(flat, 0, 0, null);
        g.dispose();
        return rgb;
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