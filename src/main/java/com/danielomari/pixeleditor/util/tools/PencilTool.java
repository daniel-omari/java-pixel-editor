package com.danielomari.pixeleditor.util.tools;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.danielomari.pixeleditor.util.tools.Tool;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import com.danielomari.pixeleditor.util.tools.ColorTool;

public class PencilTool implements Tool {
    private static int prevX = -1, prevY = -1; // Store previous positions
    private final int size = 2; // Pencil size
    private final List<Point> points = new ArrayList<>();
    private CanvasPanel canvas;
    private Drawcommand currentCommand;
    private boolean isDrawing = false;
//    private Point lastPoint;

    @Override
    public void onPress(MouseEvent e) {
        isDrawing = true;
        currentCommand = new Drawcommand(CanvasPanel.getInstance());
        points.clear(); // starts a new stroke
        points.add(e.getPoint());

        prevX = e.getX();
        prevY = e.getY();

        drawOnCanvas(prevX, prevY, prevX, prevY); // Draw initial dot

//        Point lastPoint = new Point(e.getX(), e.getY());
        isDrawing = true;
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

        // Complete command
        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;
        }

        isDrawing = false;
    }

    private void drawOnCanvas(int x1, int y1, int x2, int y2) {
        CanvasPanel canvasPanel = CanvasPanel.getInstance();
        Graphics2D g2d = canvasPanel.getCanvasImage().createGraphics();

        g2d.setColor(ColorTool.getColor());
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Smoother lines
        g2d.drawLine(x1, y1, x2, y2);

        g2d.dispose();
        canvasPanel.repaint();
    }

    public void setCanvas(CanvasPanel canvas) {
        this.canvas = canvas;
    }
}
