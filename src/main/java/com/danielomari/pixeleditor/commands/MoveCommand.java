package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.layers.Layer;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Undoable whole-image edit scoped to ONE layer (the one active when created).
 * Used by the selection/rotate flows, which replace the layer image wholesale.
 * The originalX/originalY parameters are kept for call-site compatibility.
 */
public class MoveCommand implements Command {
    private final CanvasPanel canvas;
    private final Layer layer;
    private BufferedImage beforeState;
    private BufferedImage afterState;

    public MoveCommand(CanvasPanel canvas, int originalX, int originalY) {
        this.canvas = canvas;
        this.layer = canvas.getActiveLayer();
    }

    @Override
    public void execute() {
        storeAfterState();
    }

    public void storeBeforeState() {
        beforeState = deepCopy(layer.getImage());
    }

    public void storeAfterState() {
        afterState = deepCopy(layer.getImage());
    }

    @Override
    public void undo() {
        if (beforeState != null) {
            layer.setImage(deepCopy(beforeState));
            canvas.repaint();
        }
    }

    @Override
    public void redo() {
        if (afterState != null) {
            layer.setImage(deepCopy(afterState));
            canvas.repaint();
        }
    }

    private BufferedImage deepCopy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
}
