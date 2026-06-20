package com.danielomari.pixeleditor.commands;

// A reversible editor action (execute / undo / redo) for the undo-redo stack.
public interface Command {
    void execute();

    void undo();
    void redo();
}
