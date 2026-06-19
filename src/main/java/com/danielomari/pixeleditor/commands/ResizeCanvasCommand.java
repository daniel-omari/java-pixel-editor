package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.layers.Layer;
import com.danielomari.pixeleditor.layers.LayerStack;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Undoable canvas resize. Holds the per-layer images and document dimensions
 * from before and after the resize, and swaps them back/forward. The resize is
 * already applied before this command is pushed.
 */
public class ResizeCanvasCommand implements Command {
    private final CanvasPanel canvas;
    private final LayerStack stack;
    private final int oldW, oldH, newW, newH;
    private final BufferedImage[] before;
    private final BufferedImage[] after;

    public ResizeCanvasCommand(CanvasPanel canvas, int oldW, int oldH, int newW, int newH,
                               BufferedImage[] before, BufferedImage[] after) {
        this.canvas = canvas;
        this.stack = canvas.getLayers();
        this.oldW = oldW;
        this.oldH = oldH;
        this.newW = newW;
        this.newH = newH;
        this.before = before;
        this.after = after;
    }

    @Override
    public void execute() {
        // The resize was already applied before this command was pushed.
    }

    @Override
    public void undo() {
        apply(oldW, oldH, before);
    }

    @Override
    public void redo() {
        apply(newW, newH, after);
    }

    private void apply(int w, int h, BufferedImage[] images) {
        stack.setSize(w, h);
        List<Layer> layers = stack.layers();
        for (int i = 0; i < layers.size() && i < images.length; i++) {
            layers.get(i).setImage(images[i]);
        }
        canvas.notifyLayersChanged();
        canvas.revalidate();
        canvas.repaint();
    }
}
