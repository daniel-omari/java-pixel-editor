package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import com.danielomari.pixeleditor.util.tools.BrushTool.BrushSize;
import com.danielomari.pixeleditor.util.tools.EraserTool.EraserSize;
import com.danielomari.pixeleditor.util.tools.Tool;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EraserTool implements Tool {
    private static EraserSize selectedEraserSize = EraserSize.MEDIUM; // default eraser size
    private final List<Point> points = new ArrayList<>();
    private int size; // eraser size
    private int lastX, lastY; // store last mouse coordinates
    private Drawcommand currentCommand;
    private boolean isDrawing = false;

    public EraserTool() {
        // Constructor method for the eraser tool
    }

    public static void setEraserSize(EraserSize eraserSize) {
        selectedEraserSize = eraserSize;
        System.out.println("Selected size: " + selectedEraserSize);
    }

    @Override
    public void onPress(MouseEvent e) {
        // Start a new command for undo/redo
        currentCommand = new Drawcommand(CanvasPanel.getInstance());
        points.clear();
        points.add(e.getPoint());
        lastX = e.getX();
        lastY = e.getY();

//        Point lastPoint = new Point(e.getX(), e.getY());
        isDrawing = true;
    }

    @Override
    public void onDrag(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        points.clear();
        points.add(e.getPoint());

        erase(lastX, lastY, x, y);

        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void onRelease(MouseEvent e) {
        if (!isDrawing) return;

        int x = e.getX();
        int y = e.getY();

        // Perform final erasing if needed
        if (lastX != -1 && lastY != -1) {
            erase(lastX, lastY, x, y);
        }

        // Reset coordinates
        lastX = -1;
        lastY = -1;

        // For undo/redo functionality
        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;
        }
    }

    public void erase(int x1, int y1, int x2, int y2) {
        CanvasPanel canvasPanel = CanvasPanel.getInstance();
        Graphics2D g2d = canvasPanel.getCanvasImage().createGraphics();
//        g2d.fillRect(lastX - size / 2, lastY - size / 2, size, size); // erasing as a small square
//        g2d.fillOval(x1 - size / 2, y1 - size / 2, size, size); // erasing as a small circle

        switch (selectedEraserSize) {
            case SMALL -> size = 5;
            case MEDIUM -> size = 15;
            case LARGE -> size = 30;
            case EXTREME -> size = 100;
            default -> size = 15;
        }

        g2d.setColor(Color.WHITE);  // Eraser color is white

        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(lastX, lastY, x2, y2); // erasing as a line

        g2d.dispose();
        canvasPanel.repaint();
    }

    public void setSize(int newSize) {
        this.size = newSize;
    }

    public enum EraserSize {SMALL, MEDIUM, LARGE, EXTREME}
}