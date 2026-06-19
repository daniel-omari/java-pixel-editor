package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import com.danielomari.pixeleditor.ui.CanvasPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Brush tool: five styles (natural, spray, dotted, oil, stars), a continuous
 * size, and a stroke opacity. The whole stroke is painted to an off-screen
 * buffer at full strength and composited onto the active layer once, at the
 * chosen opacity, so overlapping segments don't darken at the joints.
 */
public class BrushTool implements Tool {
    public enum BrushType { option1, option2, option3, option4, option5 }

    private static BrushType selectedBrushType = BrushType.option1;
    private static int sizePx = 10;
    private static float opacity = 1f;

    private int prevX = -1, prevY = -1;
    private CanvasPanel canvas;
    private BufferedImage strokeBuffer; // the in-progress stroke (full strength)
    private Graphics2D strokeG;
    private Drawcommand currentCommand;
    private boolean isDrawing = false;

    // Live preview: draw the buffer over the canvas at the chosen opacity.
    private final Consumer<Graphics2D> previewListener = g -> {
        if (strokeBuffer == null || canvas == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        double zoom = canvas.getZoom();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clampOpacity()));
        g2.drawImage(strokeBuffer,
                (int) (canvas.getRenderOffsetX() / zoom),
                (int) (canvas.getRenderOffsetY() / zoom), null);
        g2.dispose();
    };

    // ---- settings ----
    public static void setBrushType(BrushType t) { selectedBrushType = t; }
    public static BrushType getBrushType() { return selectedBrushType; }
    public static void setSizePx(int px) { if (px > 0) sizePx = px; }
    public static int getSizePx() { return sizePx; }
    public static void setOpacity(float o) { opacity = Math.max(0f, Math.min(1f, o)); }
    public static float getOpacity() { return opacity; }
    private static float clampOpacity() { return Math.max(0f, Math.min(1f, opacity)); }

    @Override
    public void onPress(MouseEvent e) {
        canvas = CanvasPanel.getInstance();
        currentCommand = new Drawcommand(canvas);
        BufferedImage layer = canvas.getCanvasImage();
        strokeBuffer = new BufferedImage(layer.getWidth(), layer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        strokeG = strokeBuffer.createGraphics();
        strokeG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.addPaintListener(previewListener);
        isDrawing = true;
        prevX = e.getX();
        prevY = e.getY();
        paintSegment(prevX, prevY, prevX, prevY);
        canvas.repaint();
    }

    @Override
    public void onDrag(MouseEvent e) {
        if (!isDrawing) return;
        paintSegment(prevX, prevY, e.getX(), e.getY());
        prevX = e.getX();
        prevY = e.getY();
        canvas.repaint();
    }

    @Override
    public void onRelease(MouseEvent e) {
        if (!isDrawing) return;
        paintSegment(prevX, prevY, e.getX(), e.getY());

        // Composite the finished stroke onto the active layer once, at the opacity.
        Graphics2D lg = canvas.getCanvasImage().createGraphics();
        lg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clampOpacity()));
        lg.drawImage(strokeBuffer, 0, 0, null);
        lg.dispose();

        canvas.removePaintListener(previewListener);
        if (strokeG != null) strokeG.dispose();
        strokeBuffer = null;
        strokeG = null;
        prevX = prevY = -1;
        isDrawing = false;

        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;
        }
        canvas.repaint();
    }

    // Paint one stroke segment onto the buffer at full strength.
    private void paintSegment(int x1, int y1, int x2, int y2) {
        if (strokeG == null) return;
        int size = sizePx;
        strokeG.setColor(ColorTool.getColor());

        if (selectedBrushType == BrushType.option1) { // natural: one connected line
            strokeG.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            strokeG.drawLine(x1, y1, x2, y2);
            return;
        }

        // Stamp-based styles: walk the segment at a fixed spacing so fast strokes
        // stay continuous.
        double dx = x2 - x1, dy = y2 - y1;
        double distance = Math.hypot(dx, dy);
        double spacing = switch (selectedBrushType) {
            case option3 -> Math.max(2.0, size * 0.8); // dotted
            case option5 -> Math.max(2.0, size * 0.9); // stars
            default -> Math.max(1.0, size * 0.25);      // spray / oil
        };
        int steps = (int) Math.max(1, Math.round(distance / spacing));
        for (int s = 0; s <= steps; s++) {
            double t = (double) s / steps;
            stamp(strokeG, (int) Math.round(x1 + dx * t), (int) Math.round(y1 + dy * t), size);
        }
    }

    private void stamp(Graphics2D g, int px, int py, int size) {
        switch (selectedBrushType) {
            case option2 -> { // spray: random dot cluster
                for (int j = 0; j < 3; j++) {
                    int ox = (int) (Math.random() * size - size / 2.0);
                    int oy = (int) (Math.random() * size - size / 2.0);
                    int dot = (int) (size * 0.5 + Math.random() * 2);
                    g.fill(new Ellipse2D.Double(px + ox, py + oy, dot, dot));
                }
            }
            case option3 -> { // dotted: one small dot per step
                int dot = (int) (size * 0.3);
                g.fill(new Ellipse2D.Double(px - dot / 2.0, py - dot / 2.0, dot, dot));
            }
            case option4 -> { // oil: translucent bristle cluster
                Color base = ColorTool.getColor();
                for (int i = 0; i < 10; i++) {
                    int ox = (int) (Math.random() * size - size / 2.0);
                    int oy = (int) (Math.random() * size - size / 2.0);
                    int alpha = (int) (Math.random() * 150 + 100);
                    g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
                    g.fillOval(px + ox, py + oy, size, size);
                }
                g.setColor(base);
            }
            case option5 -> { // star
                AffineTransform original = g.getTransform();
                try {
                    double ox = (Math.random() - 0.5) * size * 0.3;
                    double oy = (Math.random() - 0.5) * size * 0.3;
                    AffineTransform at = new AffineTransform();
                    at.translate(px + ox, py + oy);
                    g.setTransform(at);
                    g.fill(createStar(5, size * 0.6, size * 0.24));
                } finally {
                    g.setTransform(original);
                }
            }
            default -> { }
        }
    }

    private Shape createStar(int points, double outer, double inner) {
        GeneralPath path = new GeneralPath();
        double angle = Math.PI / points;
        for (int i = 0; i < 2 * points; i++) {
            double r = (i % 2 == 0) ? outer : inner;
            double x = r * Math.cos(i * angle);
            double y = r * Math.sin(i * angle);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.closePath();
        return path;
    }
}
