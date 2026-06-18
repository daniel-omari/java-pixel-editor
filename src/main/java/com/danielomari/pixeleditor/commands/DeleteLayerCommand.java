package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.layers.Layer;
import com.danielomari.pixeleditor.layers.LayerStack;
import com.danielomari.pixeleditor.ui.CanvasPanel;

/**
 * Undoable deletion of a whole layer. Remembers the removed layer and its index
 * so undo re-inserts it exactly where it was (with its pixels), and redo removes
 * it again. Refreshes the Layers panel + canvas on every step.
 */
public class DeleteLayerCommand implements Command {
    private final CanvasPanel canvas;
    private final LayerStack stack;
    private final Layer layer;
    private final int index;

    public DeleteLayerCommand(CanvasPanel canvas, int index) {
        this.canvas = canvas;
        this.stack = canvas.getLayers();
        this.index = index;
        this.layer = stack.get(index);
    }

    @Override
    public void execute() {
        stack.removeAt(index);
        refresh();
    }

    @Override
    public void undo() {
        stack.insertAt(index, layer);
        refresh();
    }

    @Override
    public void redo() {
        stack.removeAt(index);
        refresh();
    }

    private void refresh() {
        canvas.notifyLayersChanged();
        canvas.repaint();
    }
}
