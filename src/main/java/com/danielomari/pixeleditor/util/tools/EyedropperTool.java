package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Colour picker. Clicking the canvas samples the pixel under the cursor (from the
 * flattened/composited image) and makes it the current colour. While active it
 * shows a Photoshop-style circular loupe next to the cursor: a magnified grid of
 * the surrounding pixels with the exact sample pixel highlighted in the centre.
 */
public class EyedropperTool implements Tool {

    private static final int RADIUS = 5;                  // pixels each side of centre
    private static final int CELLS = RADIUS * 2 + 1;      // 11 pixels across
    private static final int CELL = 9;                    // on-screen size of each pixel
    private static final int LOUPE = CELLS * CELL;        // loupe diameter

    private CanvasPanel canvas;
    private BufferedImage cached;        // flattened snapshot used by the loupe
    private Point hover;                 // cursor position in panel coordinates
    private MouseMotionAdapter hoverListener;
    private Consumer<Graphics2D> loupeListener;

    // Called by CanvasPanel.setTool when this tool becomes active.
    public void activate(CanvasPanel canvas) {
        this.canvas = canvas;
        cached = canvas.getFlattenedImage(); // canvas doesn't change while picking
        if (hoverListener == null) {
            hoverListener = new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) { hover = e.getPoint(); canvas.repaint(); }
                @Override public void mouseDragged(MouseEvent e) { hover = e.getPoint(); canvas.repaint(); }
            };
        }
        if (loupeListener == null) {
            loupeListener = this::drawLoupe;
        }
        canvas.removeMouseMotionListener(hoverListener); // never stack duplicates
        canvas.addMouseMotionListener(hoverListener);
        canvas.addPaintListener(loupeListener);
    }

    public void deactivate() {
        if (canvas != null) {
            if (hoverListener != null) canvas.removeMouseMotionListener(hoverListener);
            if (loupeListener != null) canvas.removePaintListener(loupeListener);
            canvas.repaint();
        }
        hover = null;
        cached = null;
    }

    @Override
    public void onPress(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        CanvasPanel c = (canvas != null) ? canvas : CanvasPanel.getInstance();
        BufferedImage flat = c.getFlattenedImage(); // fresh, so the pick is accurate
        int x = e.getX();
        int y = e.getY();
        if (x < 0 || y < 0 || x >= flat.getWidth() || y >= flat.getHeight()) return;
        ColorTool.pickColor(new Color(flat.getRGB(x, y))); // RGB only (ignore alpha)
    }

    @Override
    public void onRelease(MouseEvent e) {
    }

    @Override
    public void onDrag(MouseEvent e) {
    }

    // Draws the magnifier loupe at the cursor (in panel/screen coordinates).
    private void drawLoupe(Graphics2D g0) {
        if (hover == null || cached == null || canvas == null) return;

        Graphics2D g = (Graphics2D) g0.create();
        g.setTransform(new AffineTransform()); // ignore the canvas zoom/centring; draw in panel space

        int pw = canvas.getWidth();
        int ph = canvas.getHeight();
        // Place the loupe up-and-right of the cursor, flipping if it would go off-panel.
        int lx = hover.x + 24;
        int ly = hover.y - LOUPE - 12;
        if (lx + LOUPE > pw) lx = hover.x - LOUPE - 24;
        if (ly < 0) ly = hover.y + 24;
        lx = Math.max(0, Math.min(lx, pw - LOUPE));
        ly = Math.max(0, Math.min(ly, ph - LOUPE));

        Point centre = canvas.screenToImage(hover.x, hover.y);
        int cx = centre.x;
        int cy = centre.y;

        Shape oldClip = g.getClip();
        Ellipse2D circle = new Ellipse2D.Double(lx, ly, LOUPE, LOUPE);
        g.setClip(circle);
        g.setColor(new Color(45, 45, 45)); // backdrop for out-of-bounds / transparent
        g.fillRect(lx, ly, LOUPE, LOUPE);

        for (int dy = -RADIUS; dy <= RADIUS; dy++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int ix = cx + dx;
                int iy = cy + dy;
                if (ix < 0 || iy < 0 || ix >= cached.getWidth() || iy >= cached.getHeight()) continue;
                Color c = new Color(cached.getRGB(ix, iy), true);
                if (c.getAlpha() == 0) continue; // transparent -> show backdrop
                g.setColor(c);
                g.fillRect(lx + (dx + RADIUS) * CELL, ly + (dy + RADIUS) * CELL, CELL, CELL);
            }
        }

        // Highlight the exact sample pixel in the centre.
        int hxp = lx + RADIUS * CELL;
        int hyp = ly + RADIUS * CELL;
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.BLACK);
        g.drawRect(hxp - 1, hyp - 1, CELL + 1, CELL + 1);
        g.setColor(Color.WHITE);
        g.drawRect(hxp, hyp, CELL - 1, CELL - 1);

        g.setClip(oldClip);

        // Ring border (black outside, white inside) for visibility on any background.
        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.BLACK);
        g.draw(circle);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.draw(new Ellipse2D.Double(lx + 1.5, ly + 1.5, LOUPE - 3, LOUPE - 3));

        g.dispose();
    }
}
