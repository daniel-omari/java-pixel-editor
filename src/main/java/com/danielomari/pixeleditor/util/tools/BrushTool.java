package com.danielomari.pixeleditor.util.tools;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.danielomari.pixeleditor.util.tools.Tool;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import java.awt.geom.GeneralPath;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import com.danielomari.pixeleditor.util.tools.ColorTool;
import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import java.awt.Shape;  
import java.awt.geom.Path2D; 

public class BrushTool implements Tool {
    private static int prevX = -1, prevY = -1; // Store previous positions
    private static BrushType selectedBrushType = BrushType.option1; // Default brush
    private static BrushSize selectedBrushSize = BrushSize.MEDIUM; //default brush size
    private final List<Point> points = new ArrayList<>();
    private int size; // Brush size
    private Drawcommand currentCommand;
    private boolean isDrawing = false;

    public static void setBrushType(BrushType brushType) {
        selectedBrushType = brushType;
        System.out.println("Selected Brush: " + selectedBrushType);
    }

    public static void setBrushSize(BrushSize brushSize) {
        selectedBrushSize = brushSize;
        System.out.println("Selected size: " + selectedBrushSize);
    }

    @Override
    public void onPress(MouseEvent e) {
        isDrawing = true;
        currentCommand = new Drawcommand(CanvasPanel.getInstance());

        points.clear();
        points.add(e.getPoint());
        prevX = e.getX();
        prevY = e.getY();
        drawOnCanvas(prevX, prevY, prevX, prevY);
    }

    @Override
    public void onDrag(MouseEvent e) {
        if (!isDrawing) return;
        int x = e.getX();
        int y = e.getY();

        points.add(e.getPoint());
        drawOnCanvas(prevX, prevY, x, y); // Draw continuous line

        prevX = x;
        prevY = y;
    }

    @Override
    public void onRelease(MouseEvent e) {
        if (!isDrawing) return;

        int x = e.getX();
        int y = e.getY();

        // Draw final segment if needed
        if (prevX != -1 && prevY != -1) {
            points.add(e.getPoint());
            drawOnCanvas(prevX, prevY, x, y);
        }

        // Reset previous coordinates
        prevX = -1;
        prevY = -1;

        // For undo/redo functionality
        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;
        }

        isDrawing = false;
    }

    //develop a method to get color from colorTool
    private Color setColor() {
        Color color = ColorTool.getColor();
        return color;
    }

    private void drawOnCanvas(int x1, int y1, int x2, int y2) {
        CanvasPanel canvasPanel = CanvasPanel.getInstance();
        Graphics2D g2d = canvasPanel.getCanvasImage().createGraphics();

        switch (selectedBrushSize) {
            case SMALL -> size = 5;
            case MEDIUM -> size = 10;
            case LARGE -> size = 15;
            default -> size = 5;
        }
//        System.out.println("Selected Brush size: " + selectedBrushSize);
        g2d.setColor(setColor());

        // Apply different styles based on brush type
        switch (selectedBrushType) {
            case option1 -> {//natural pencil
                g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x1, y1, x2, y2);
            }
            case option2 -> {//sprays
                for (int i = 0; i < points.size(); i += 3) { // Adjust dot spacing
                    Point p = points.get(i);
                    for (int j = 0; j < 3; j++) { // Add small clusters of dots
                        int offsetX = (int) (Math.random() * size - size / 2.0);
                        int offsetY = (int) (Math.random() * size - size / 2.0);
                        int dotSize = (int) (size * 0.5 + Math.random() * 2); // Slightly varied dot sizes
                        g2d.fill(new Ellipse2D.Double(p.x + offsetX, p.y + offsetY, dotSize, dotSize));
                    }
                }
            }

            case option3 -> {//dotted lines
                for (int i = 0; i < points.size(); i += 3) { // Skip some points for better spacing
                    Point p = points.get(i);
                    int dotSize = (int) (size * 0.3); // Adjust dot size
                    g2d.fill(new Ellipse2D.Double(p.x - dotSize / 2.0, p.y - dotSize / 2.0, dotSize, dotSize));
                }
            }

            case option4 -> {//oil brush
                // Semi-transparent strokes for blending effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // 30% opacity

                //get base color
                Color baseColor = ColorTool.getColor();

                // Simulate oil brush texture with random bristle effect
                for (int i = 0; i < 10; i++) { // More iterations for richer strokes
                    int offsetX = (int) (Math.random() * size - size / 2.0);
                    int offsetY = (int) (Math.random() * size - size / 2.0);

                    // Generate a random opacity value between 100 and 250
                    int alpha = (int) (Math.random() * 150 + 100);

                    // Create a new color with the same RGB but different opacity
                    Color newColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);

                    g2d.setColor(newColor);
                    g2d.fillOval(prevX + offsetX, prevY + offsetY, size, size); // Soft rounded strokes
                }
            }

            case option5 ->{
                final int pointStep = 8; 
                final int starPoints = 5; // Fixed 5-pointed stars
                final double starSize = size * 0.6;
                final double positionJitter = 0.3;
                
                // Store original transform
                AffineTransform originalTransform = g2d.getTransform();
                
                try {
                    for(int i = 0; i < points.size(); i += pointStep) {
                        Point p = points.get(i);

                        // Add slight random position variation
                        double offsetX = (Math.random() - 0.5) * size * positionJitter;
                        double offsetY = (Math.random() - 0.5) * size * positionJitter;
                        
                        // Create transformation for this star
                        AffineTransform at = new AffineTransform();
                        at.translate(p.x + offsetX, p.y + offsetY); // Move to mouse position
                        
                        // Apply the transformation
                        g2d.setTransform(at);
                        
                        // Create and draw star
                        Shape star = createStar(starPoints, starSize, starSize * 0.4);
                        g2d.fill(star);
                    }
                } finally {
                    // Reset to original transform
                    g2d.setTransform(originalTransform);
                }
            }
        }
        g2d.dispose();
        canvasPanel.repaint();
    }
    
    // helper method to create star shapes
    private Shape createStar(int points, double outerRadius, double innerRadius) {
        GeneralPath path = new GeneralPath();
        double angle = Math.PI / points;
        
        for (int i = 0; i < 2 * points; i++) {
            double r = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = r * Math.cos(i * angle);
            double y = r * Math.sin(i * angle);
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        
        path.closePath();
        return path;
    }

    public enum BrushType {option1, option2, option3, option4, option5}

    public enum BrushSize {SMALL, MEDIUM, LARGE}
}