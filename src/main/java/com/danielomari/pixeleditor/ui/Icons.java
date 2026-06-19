package com.danielomari.pixeleditor.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Tool icons. Each icon first tries to load a PNG from the classpath at
 * {@code /icons/<name>.png} (drop a downloaded icon pack into
 * src/main/resources/icons/); if that file isn't present it falls back to a
 * simple icon drawn in code, so the toolbar always has something to show.
 */
public final class Icons {
    private static final int S = 22;        // drawn-fallback canvas size
    private static final int ICON_PX = 26;  // displayed icon size (PNGs scaled to this)
    private static final Color FG = new Color(150, 150, 150);

    private Icons() {}

    // Prefer a packaged PNG; otherwise use the supplied code-drawn fallback.
    private static Icon load(String name, Icon fallback) {
        URL url = Icons.class.getResource("/icons/" + name + ".png");
        if (url == null) return fallback;
        Image img = new ImageIcon(url).getImage().getScaledInstance(ICON_PX, ICON_PX, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private interface Painter { void paint(Graphics2D g); }

    private static ImageIcon draw(Painter painter) {
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(FG);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        painter.paint(g);
        g.dispose();
        return new ImageIcon(img);
    }

    public static Icon brush() {
        return load("brush", draw(g -> { g.drawLine(5, 17, 13, 9); g.fillOval(11, 4, 8, 8); }));
    }

    public static Icon pencil() {
        return load("pencil", draw(g -> {
            g.drawLine(5, 17, 14, 8);
            g.fillPolygon(new int[]{14, 19, 16}, new int[]{8, 5, 11}, 3);
            g.drawLine(4, 18, 6, 16);
        }));
    }

    public static Icon eraser() {
        return load("eraser", draw(g -> {
            g.drawPolygon(new int[]{4, 13, 18, 9}, new int[]{14, 6, 10, 18}, 4);
            g.drawLine(9, 18, 18, 10);
        }));
    }

    public static Icon eyedropper() {
        return load("eyedropper", draw(g -> {
            g.drawLine(5, 17, 13, 9);
            g.fillOval(12, 3, 7, 7);
            g.fillOval(4, 16, 4, 4);
        }));
    }

    public static Icon shape() {
        return load("shape", draw(g -> { g.drawRect(4, 9, 9, 9); g.drawOval(10, 4, 8, 8); }));
    }

    public static Icon select() {
        return load("select", draw(g -> {
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{3f, 2f}, 0f));
            g.drawRect(4, 4, 14, 14);
        }));
    }

    public static Icon zoom() {
        return load("zoom", draw(g -> { g.drawOval(4, 4, 11, 11); g.drawLine(13, 13, 18, 18); }));
    }

    public static Icon rotate() {
        return load("rotate", draw(g -> {
            g.drawArc(4, 4, 14, 14, 70, 250);
            g.fillPolygon(new int[]{16, 20, 14}, new int[]{4, 8, 8}, 3);
        }));
    }

    public static Icon text() {
        return load("text", draw(g -> {
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("T", (S - fm.stringWidth("T")) / 2, S - 6);
        }));
    }

    public static Icon colour() {
        return load("colour", drawColour());
    }

    public static Icon fill() {
        return load("fill", draw(g -> {
            g.drawPolygon(new int[]{6, 16, 14, 7}, new int[]{8, 10, 19, 17}, 4); // bucket
            g.drawArc(6, 3, 8, 8, 0, 180);                                       // handle
            g.fillOval(15, 14, 3, 4);                                            // drip
        }));
    }

    public static Icon darkMode() {
        return load("darkmode", drawDarkMode());
    }

    private static ImageIcon drawColour() {
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.RED);    g.fillArc(4, 4, 14, 14, 0, 90);
        g.setColor(Color.GREEN);  g.fillArc(4, 4, 14, 14, 90, 90);
        g.setColor(Color.BLUE);   g.fillArc(4, 4, 14, 14, 180, 90);
        g.setColor(Color.ORANGE); g.fillArc(4, 4, 14, 14, 270, 90);
        g.setColor(FG);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(4, 4, 14, 14);
        g.dispose();
        return new ImageIcon(img);
    }

    private static ImageIcon drawDarkMode() {
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(FG);
        g.fillOval(4, 4, 14, 14);
        g.setComposite(AlphaComposite.Clear);
        g.fillOval(8, 2, 13, 13);
        g.dispose();
        return new ImageIcon(img);
    }
}
