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
        g2d.setColor(setColor());

        // Natural pencil is one connected stroke, so it already fills any gaps.
        if (selectedBrushType == BrushType.option1) {
            g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x1, y1, x2, y2);
            g2d.dispose();
            canvasPanel.repaint();
            return;
        }

        // Stamp-based brushes: walk along this segment at a fixed spacing and
        // stamp at every step, so the stroke stays continuous no matter how fast
        // the mouse moves (sparse drag events no longer leave gaps).
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.hypot(dx, dy);
        double spacing = switch (selectedBrushType) {
            case option3 -> Math.max(2.0, size * 0.8); // dotted: even, visible gaps
            case option5 -> Math.max(2.0, size * 0.9); // stars: spaced out
            default -> Math.max(1.0, size * 0.25);      // spray / oil: dense fill
        };
        int steps = (int) Math.max(1, Math.round(distance / spacing));
        for (int s = 0; s <= steps; s++) {
            double t = (double) s / steps;
            int px = (int) Math.round(x1 + dx * t);
            int py = (int) Math.round(y1 + dy * t);
            stamp(g2d, px, py);
        }

        g2d.dispose();
        canvasPanel.repaint();
    }

    // Paints a single brush stamp at one point for the stamp-based styles.
    // drawOnCanvas calls this repeatedly along the stroke.
    private void stamp(Graphics2D g2d, int px, int py) {
        switch (selectedBrushType) {
            case option2 -> { // spray: a small cluster of random dots
                for (int j = 0; j < 3; j++) {
                    int offsetX = (int) (Math.random() * size - size / 2.0);
                    int offsetY = (int) (Math.random() * size - size / 2.0);
                    int dotSize = (int) (size * 0.5 + Math.random() * 2);
                    g2d.fill(new Ellipse2D.Double(px + offsetX, py + offsetY, dotSize, dotSize));
                }
            }
            case option3 -> { // dotted line: one small dot per step
                int dotSize = (int) (size * 0.3);
                g2d.fill(new Ellipse2D.Double(px - dotSize / 2.0, py - dotSize / 2.0, dotSize, dotSize));
            }
            case option4 -> { // oil: translucent bristle cluster
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                Color baseColor = ColorTool.getColor();
                for (int i = 0; i < 10; i++) {
                    int offsetX = (int) (Math.random() * size - size / 2.0);
                    int offsetY = (int) (Math.random() * size - size / 2.0);
                    int alpha = (int) (Math.random() * 150 + 100);
                    g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(),
                            baseColor.getBlue(), alpha));
                    g2d.fillOval(px + offsetX, py + offsetY, size, size);
                }
            }
            case option5 -> { // star
                AffineTransform original = g2d.getTransform();
                try {
                    double offsetX = (Math.random() - 0.5) * size * 0.3;
                    double offsetY = (Math.random() - 0.5) * size * 0.3;
                    AffineTransform at = new AffineTransform();
                    at.translate(px + offsetX, py + offsetY);
                    g2d.setTransform(at);
                    g2d.fill(createStar(5, size * 0.6, size * 0.6 * 0.4));
                } finally {
                    g2d.setTransform(original);
                }
            }
            default -> { }
        }
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