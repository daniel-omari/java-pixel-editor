package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.util.tools.Tool;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import com.danielomari.pixeleditor.util.tools.SelectTool;
import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.awt.event.*;

// Holds the current drawing colour and recent colours, and flood-fills on click.
public class ColorTool implements Tool, ActionListener {
    //Default color is set to black
    private static Color currentColor = Color.BLACK;
    private static List<Color> recentColors = new ArrayList<>();
    private static final List<Runnable> changeListeners = new ArrayList<>();
    private BufferedImage canvasImage;
    private Drawcommand currentCommand;
    private CanvasPanel canvas;

    public static Color getColor() {
        return currentColor;
    }

    public static void setColor(Color color) {
        currentColor = color;

        // Check if there's an active selection to recolor
        SelectTool selectTool = SelectTool.getInstance();
        if (selectTool != null) {
            selectTool.recolourSelection(color);
        }
        addRecentColour(color);
        fireColorChanged();
    }
    // Set the current colour WITHOUT recolouring an active selection (eyedropper).
    public static void pickColor(Color color) {
        currentColor = color;
        addRecentColour(color);
        fireColorChanged();
    }

    // Live update while dragging a picker: set the colour + notify, but don't
    // spam the recent-colours list (that happens on commit via pickColor).
    public static void setCurrentColorLive(Color color) {
        currentColor = color;
        fireColorChanged();
    }

    private static void addRecentColour(Color color) {
        if (recentColors.contains(color)) {
            recentColors.remove(color);
        }
        recentColors.add(0, color);
        if (recentColors.size() > 5) {
            recentColors.remove(5);
        }
    }

    public static List<Color> getRecentColors() {
        return recentColors;
    }

    // Notify UI (e.g. the Colour panel) when the current colour changes.
    public static void addColorChangeListener(Runnable r) {
        changeListeners.add(r);
    }

    private static void fireColorChanged() {
        for (Runnable r : changeListeners) r.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JColorChooser colorChooser = new JColorChooser(currentColor);
        RecentColours recentColoursPanel = new RecentColours();
        colorChooser.addChooserPanel(recentColoursPanel);
        Color selectedColor = JColorChooser.showDialog(null, "Pick a color", currentColor);
        //Set new color
        if (selectedColor != null) {
            setColor(selectedColor); // Updates the current color and adds to recent list
            addRecentColour(selectedColor); // Ensures the color is saved

            // Force update of RecentColours panel
            recentColoursPanel.updateChooser();
        }
    }

    @Override
    public void onPress(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) { // left or right click fills
            canvas = CanvasPanel.getInstance();
            currentCommand = new Drawcommand(canvas);

            canvasImage = CanvasPanel.getInstance().getCanvasImage();
            Point clickPoint = e.getPoint();
            
            if (isWithinBounds(clickPoint)) {
                floodFill(clickPoint.x, clickPoint.y, currentColor);
                CanvasPanel.getInstance().repaint();
            }

            // Complete command
            if (currentCommand != null) {
                currentCommand.storeAfterState();
                CommandManager.getInstance().executeCommand(currentCommand);
                currentCommand = null;
            }
        }
    }

    private boolean isWithinBounds(Point p) {
        return p.x >= 0 && p.x < canvasImage.getWidth() && 
               p.y >= 0 && p.y < canvasImage.getHeight();
    }

    private void floodFill(int x, int y, Color newColor) {
        int targetRGB = canvasImage.getRGB(x, y);
        int newRGB = newColor.getRGB();
        
        if (targetRGB == newRGB) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if (shouldFill(p.x, p.y, targetRGB)) {
                canvasImage.setRGB(p.x, p.y, newRGB);
                addNeighbors(queue, p);
            }
        }
    }

    private boolean shouldFill(int x, int y, int targetRGB) {
        return x >= 0 && x < canvasImage.getWidth() &&
               y >= 0 && y < canvasImage.getHeight() &&
               canvasImage.getRGB(x, y) == targetRGB;
    }

    private void addNeighbors(Queue<Point> queue, Point p) {
        queue.add(new Point(p.x + 1, p.y));
        queue.add(new Point(p.x - 1, p.y));
        queue.add(new Point(p.x, p.y + 1));
        queue.add(new Point(p.x, p.y - 1));
    }

    // Empty implementations for other Tool methods
    @Override public void onRelease(MouseEvent e) {}
    @Override public void onDrag(MouseEvent e) {}
}
