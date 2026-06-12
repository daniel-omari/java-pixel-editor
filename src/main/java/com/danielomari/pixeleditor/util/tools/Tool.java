// interface for all tools
package com.danielomari.pixeleditor.util.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface Tool {
    void onPress(MouseEvent e);
    void onDrag(MouseEvent e);
    void onRelease(MouseEvent e);
}