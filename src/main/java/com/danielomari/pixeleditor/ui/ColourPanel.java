package com.danielomari.pixeleditor.ui;

import com.danielomari.pixeleditor.util.tools.ColorTool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Photoshop-style Colour dock (top-right): an HSV picker (saturation/value
 * square + hue bar) as the main control, plus the current colour, recent
 * colours, and a custom picker. Choosing a colour sets the current drawing
 * colour for every tool, and the panel stays in sync with the eyedropper.
 */
public class ColourPanel extends JPanel {
    private final JPanel currentSwatch = new JPanel();
    private final JPanel recentRow = new JPanel(new GridLayout(1, 6, 2, 2));
    private final HsvPicker picker;

    public ColourPanel() {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel("Colour");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        add(title, BorderLayout.NORTH);

        // Live drag updates the colour; release commits it to the recent list.
        picker = new HsvPicker(ColorTool::setCurrentColorLive,
                () -> ColorTool.pickColor(ColorTool.getColor()));
        add(picker, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 6));

        JPanel row = new JPanel(new BorderLayout(6, 0));
        currentSwatch.setPreferredSize(new Dimension(48, 28));
        currentSwatch.setBorder(new LineBorder(Color.DARK_GRAY));
        currentSwatch.setToolTipText("Current colour - click for a custom picker");
        currentSwatch.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { chooseCustom(); }
        });
        row.add(currentSwatch, BorderLayout.WEST);
        JButton custom = new JButton("Custom...");
        custom.setFocusable(false);
        custom.addActionListener(e -> chooseCustom());
        row.add(custom, BorderLayout.CENTER);
        south.add(row, BorderLayout.NORTH);

        JPanel recentWrap = new JPanel(new BorderLayout(0, 2));
        JLabel rl = new JLabel("Recent");
        rl.setFont(rl.getFont().deriveFont(11f));
        recentWrap.add(rl, BorderLayout.NORTH);
        recentWrap.add(recentRow, BorderLayout.CENTER);
        south.add(recentWrap, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        ColorTool.addColorChangeListener(this::refresh);
        refresh();
    }

    private void chooseCustom() {
        Color c = JColorChooser.showDialog(this, "Pick a colour", ColorTool.getColor());
        if (c != null) ColorTool.pickColor(c);
    }

    private void refresh() {
        Color current = ColorTool.getColor();
        currentSwatch.setBackground(current);
        picker.setColor(current);
        recentRow.removeAll();
        List<Color> recents = ColorTool.getRecentColors();
        for (int i = 0; i < 6; i++) {
            if (i < recents.size()) {
                recentRow.add(recentSwatch(recents.get(i)));
            } else {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                recentRow.add(empty);
            }
        }
        recentRow.revalidate();
        recentRow.repaint();
    }

    private JComponent recentSwatch(Color colour) {
        JPanel sw = new JPanel();
        sw.setBackground(colour);
        sw.setBorder(new LineBorder(new Color(0, 0, 0, 60)));
        sw.setPreferredSize(new Dimension(16, 16));
        sw.setToolTipText(String.format("#%02X%02X%02X", colour.getRed(), colour.getGreen(), colour.getBlue()));
        sw.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { ColorTool.pickColor(colour); }
        });
        return sw;
    }

    /** Saturation/Value square + a hue bar, painted with gradients. */
    private static final class HsvPicker extends JPanel {
        private static final int HUE_BAR = 16;
        private static final int PAD = 4;
        private float hue = 0f, sat = 1f, val = 1f;
        private int activeRegion = 0; // 1 = SV square, 2 = hue bar
        private final Consumer<Color> onLive;
        private final Runnable onCommit;

        HsvPicker(Consumer<Color> onLive, Runnable onCommit) {
            this.onLive = onLive;
            this.onCommit = onCommit;
            setPreferredSize(new Dimension(190, 130));
            MouseAdapter ma = new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    activeRegion = hueRect().contains(e.getPoint()) ? 2 : 1;
                    handle(e);
                }
                @Override public void mouseDragged(MouseEvent e) { handle(e); }
                @Override public void mouseReleased(MouseEvent e) { if (onCommit != null) onCommit.run(); }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        private Rectangle squareRect() {
            return new Rectangle(PAD, PAD, getWidth() - HUE_BAR - 3 * PAD, getHeight() - 2 * PAD);
        }

        private Rectangle hueRect() {
            return new Rectangle(getWidth() - HUE_BAR - PAD, PAD, HUE_BAR, getHeight() - 2 * PAD);
        }

        private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

        private void handle(MouseEvent e) {
            if (activeRegion == 2) {
                Rectangle hb = hueRect();
                hue = clamp((e.getY() - hb.y) / (float) hb.height);
            } else {
                Rectangle sq = squareRect();
                sat = clamp((e.getX() - sq.x) / (float) sq.width);
                val = 1f - clamp((e.getY() - sq.y) / (float) sq.height);
            }
            if (onLive != null) onLive.accept(Color.getHSBColor(hue, sat, val));
            repaint();
        }

        // Sync from outside (e.g. eyedropper) without re-firing.
        void setColor(Color c) {
            if (c == null) return;
            if (Color.getHSBColor(hue, sat, val).getRGB() == c.getRGB()) return; // already showing it
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            hue = hsb[0];
            sat = hsb[1];
            val = hsb[2];
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle sq = squareRect();
            Rectangle hb = hueRect();

            // SV square = pure hue + white(left->right) + black(top->bottom) overlays.
            g.setColor(Color.getHSBColor(hue, 1f, 1f));
            g.fillRect(sq.x, sq.y, sq.width, sq.height);
            g.setPaint(new GradientPaint(sq.x, 0, Color.WHITE, sq.x + sq.width, 0, new Color(255, 255, 255, 0)));
            g.fillRect(sq.x, sq.y, sq.width, sq.height);
            g.setPaint(new GradientPaint(0, sq.y, new Color(0, 0, 0, 0), 0, sq.y + sq.height, Color.BLACK));
            g.fillRect(sq.x, sq.y, sq.width, sq.height);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(sq.x, sq.y, sq.width, sq.height);

            // SV marker.
            int mx = sq.x + Math.round(sat * sq.width);
            int my = sq.y + Math.round((1f - val) * sq.height);
            g.setColor(Color.BLACK);
            g.drawOval(mx - 4, my - 4, 8, 8);
            g.setColor(Color.WHITE);
            g.drawOval(mx - 3, my - 3, 6, 6);

            // Hue bar.
            for (int yy = 0; yy < hb.height; yy++) {
                g.setColor(Color.getHSBColor(yy / (float) hb.height, 1f, 1f));
                g.fillRect(hb.x, hb.y + yy, hb.width, 1);
            }
            g.setColor(Color.DARK_GRAY);
            g.drawRect(hb.x, hb.y, hb.width, hb.height);

            // Hue marker.
            int hy = hb.y + Math.round(hue * hb.height);
            g.setColor(Color.WHITE);
            g.drawRect(hb.x - 1, hy - 2, hb.width + 1, 4);
            g.setColor(Color.BLACK);
            g.drawRect(hb.x - 2, hy - 3, hb.width + 3, 6);

            g.dispose();
        }
    }
}
