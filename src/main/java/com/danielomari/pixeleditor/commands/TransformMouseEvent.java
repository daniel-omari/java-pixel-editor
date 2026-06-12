package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class TransformMouseEvent {
    public static MouseEvent transform(MouseEvent e, CanvasPanel canvasPanel) {
        BufferedImage canvasImage = canvasPanel.getCanvasImage();
        float zoomLevel = canvasPanel.getZoom();

        int scaledWidth = (int) (canvasImage.getWidth() * zoomLevel);
        int scaledHeight = (int) (canvasImage.getHeight() * zoomLevel);
        int offsetX = (canvasPanel.getWidth() - scaledWidth) / 2;
        int offsetY = (canvasPanel.getHeight() - scaledHeight) / 2;

        int originalX = (int) ((e.getX() - offsetX) / zoomLevel);
        int originalY = (int) ((e.getY() - offsetY) / zoomLevel);

        // Ensure the coordinates are within the bounds of the canvas image
        originalX = Math.max(0, Math.min(originalX, canvasImage.getWidth() - 1));
        originalY = Math.max(0, Math.min(originalY, canvasImage.getHeight() - 1));

        return new MouseEvent(
                e.getComponent(),
                e.getID(),
                e.getWhen(),
                e.getModifiersEx(),
                originalX,
                originalY,
                e.getXOnScreen(),
                e.getYOnScreen(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton()
        );
    }

    public static Point transformPoint(Point p) {
        double scaleFactor = CanvasPanel.getInstance().getZoom();
        return new Point(
                (int) (p.x / scaleFactor),
                (int) (p.y / scaleFactor)
        );
    }
}
