// interface for all tools
package com.danielomari.pixeleditor.util.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

// Common interface for every drawing tool: receives mouse press / drag / release events.
public interface Tool {
    void onPress(MouseEvent e);
    void onDrag(MouseEvent e);
    void onRelease(MouseEvent e);
}