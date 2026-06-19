package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Eraser: clears pixels to transparent on the active layer (revealing the layers
 * beneath / the checkerboard), rather than painting white. Size is configurable.
 */
public class EraserTool implements Tool {
    private static int sizePx = 15;

    private int lastX = -1, lastY = -1;
    private Drawcommand currentCommand;
    private boolean isDrawing = false;

    public static void setSize(int px) { if (px > 0) sizePx = px; }
    public static int getSize() { return sizePx; }

    @Override
    public void onPress(MouseEvent e) {
        currentCommand = new Drawcommand(CanvasPanel.getInstance());
        lastX = e.getX();
        lastY = e.getY();
        erase(lastX, lastY, lastX, lastY); // erase a dab at the press point
        isDrawing = true;
    }

    @Override
    public void onDrag(MouseEvent e) {
        if (!isDrawing) return;
        erase(lastX, lastY, e.getX(), e.getY());
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void onRelease(MouseEvent e) {
        if (!isDrawing) return;
        erase(lastX, lastY, e.getX(), e.getY());
        lastX = -1;
        lastY = -1;
        isDrawing = false;
        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;
        }
    }

    // Clear the stroke to transparent on the active layer.
    private void erase(int x1, int y1, int x2, int y2) {
        CanvasPanel canvasPanel = CanvasPanel.getInstance();
        Graphics2D g2d = canvasPanel.getCanvasImage().createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.Clear); // remove pixels instead of painting over them
        g2d.setStroke(new BasicStroke(sizePx, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(x1, y1, x2, y2);
        g2d.dispose();
        canvasPanel.repaint();
    }
}
