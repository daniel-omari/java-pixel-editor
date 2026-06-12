package com.danielomari.pixeleditor.commands;

public interface Command {
    void execute();

    void undo();
    void redo();
}
