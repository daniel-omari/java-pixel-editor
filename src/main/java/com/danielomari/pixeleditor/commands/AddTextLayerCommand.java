package com.danielomari.pixeleditor.commands;

import com.danielomari.pixeleditor.layers.Layer;
import com.danielomari.pixeleditor.layers.LayerStack;
import com.danielomari.pixeleditor.ui.CanvasPanel;

/**
 * Undoable "place text on its own layer". The text is drawn onto a freshly
 * created layer before this command is pushed; undo removes that layer (and
 * keeps the previous working layer active), redo puts it back.
 */
public class AddTextLayerCommand implements Command {
    private final CanvasPanel canvas;
    private final LayerStack stack;
    private final Layer layer;
    private final int index;      // where the text layer sits
    private final int prevActive; // working layer to keep selected

    public AddTextLayerCommand(CanvasPanel canvas, Layer layer, int index, int prevActive) {
        this.canvas = canvas;
        this.stack = canvas.getLayers();
        this.layer = layer;
        this.index = index;
        this.prevActive = prevActive;
    }

    @Override
    public void execute() {
        // The layer was already created and drawn before this command was pushed.
    }

    @Override
    public void undo() {
        int i = stack.indexOf(layer);
        if (i >= 0) stack.removeAt(i);
        stack.setActive(Math.min(prevActive, stack.getSize() - 1));
        refresh();
    }

    @Override
    public void redo() {
        stack.insertAt(Math.min(index, stack.getSize()), layer);
        stack.setActive(Math.min(prevActive, stack.getSize() - 1));
        refresh();
    }

    private void refresh() {
        canvas.notifyLayersChanged();
        canvas.repaint();
    }
}
