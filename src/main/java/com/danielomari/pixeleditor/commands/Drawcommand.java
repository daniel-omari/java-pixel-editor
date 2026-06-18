package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.layers.Layer;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Undoable pixel edit, scoped to ONE layer (the layer that was active when the
 * command was created). Restoring replaces that layer's image, so undo/redo work
 * correctly regardless of which layer is active later, and even if the edit
 * changed the image's dimensions (e.g. a rotation).
 */
public class Drawcommand implements Command {
    private final CanvasPanel canvas;
    private final Layer layer;
    private final BufferedImage beforeState;
    private BufferedImage afterState;

    public Drawcommand(CanvasPanel canvas) {
        this.canvas = canvas;
        this.layer = canvas.getActiveLayer();
        this.beforeState = copyImage(layer.getImage());
    }

    @Override
    public void execute() {
        if (afterState != null) {
            layer.setImage(copyImage(afterState));
            canvas.repaint();
        }
    }

    @Override
    public void undo() {
        layer.setImage(copyImage(beforeState));
        canvas.repaint();
    }

    @Override
    public void redo() {
        if (afterState != null) {
            layer.setImage(copyImage(afterState));
            canvas.repaint();
        }
    }

    public void storeAfterState() {
        this.afterState = copyImage(layer.getImage());
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return copy;
    }
}
