package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Drawcommand implements Command {
    private final CanvasPanel canvas;
    private final BufferedImage beforeState;
    private BufferedImage afterState;

    public Drawcommand(CanvasPanel canvas) {
        this.canvas = canvas;
        this.beforeState = copyImage(canvas.getCanvasImage());
    }

    @Override
    public void execute() {
        if (afterState != null) {
            //clear canvas first
            canvas.clearCanvas();

            drawImage(afterState, canvas.getCanvasImage());
            canvas.repaint();
        }
    }

    @Override
    public void undo() {
        //change: clear canvas before applying undo
        canvas.clearCanvas();
        drawImage(beforeState, canvas.getCanvasImage());
        canvas.repaint();
    }

    @Override
    public void redo() {
        //change: clear canvas before applying redo
        canvas.clearCanvas();
        drawImage(afterState, canvas.getCanvasImage());
        canvas.repaint();
    }

    public void storeAfterState() {
        this.afterState = copyImage(canvas.getCanvasImage());
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                source.getType()
        );
        drawImage(source, copy);
        return copy;
    }

    private void drawImage(BufferedImage source, BufferedImage target) {
        Graphics2D g2d = target.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
    }
}
