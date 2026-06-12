package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MoveCommand implements Command {
    private final CanvasPanel canvas;
    private BufferedImage beforeState;
    private BufferedImage afterState;

    public MoveCommand(CanvasPanel canvas, int originalX, int originalY) {
        this.canvas = canvas;
    }

    @Override
    public void execute() {
        storeAfterState();
    }

    public void storeBeforeState() {
        beforeState = deepCopy(canvas.getCanvasImage());
    }

    public void storeAfterState() {
        afterState = deepCopy(canvas.getCanvasImage());
    }

    @Override
    public void undo() {
        if (beforeState != null) {
//            Graphics2D g = canvas.getCanvasImage().createGraphics();
//            g.drawImage(beforeState, 0, 0, null);
//            g.dispose();
//            canvas.repaint();
            canvas.setCanvasImage(deepCopy(beforeState));
        }
    }

    public void redo() { // This is never used..
        if (afterState != null) {
//            Graphics2D g = canvas.getCanvasImage().createGraphics();
//            g.drawImage(afterState, 0, 0, null);
//            g.dispose();
//            canvas.repaint();
            canvas.setCanvasImage(deepCopy(afterState));
        }
    }

    private BufferedImage deepCopy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
}
