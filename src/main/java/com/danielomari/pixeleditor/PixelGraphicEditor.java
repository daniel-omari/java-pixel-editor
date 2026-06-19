package com.danielomari.pixeleditor;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.danielomari.pixeleditor.ui.MenuBar.MenuBars;
import com.danielomari.pixeleditor.util.Configuration;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import com.danielomari.pixeleditor.ui.LayersPanel;
import com.formdev.flatlaf.FlatLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;


public class PixelGraphicEditor {
    private final JFrame mainFrame;
    private static CanvasPanel canvas;
    private static JSplitPane leftSplit;   // tools | (canvas + layers)
    private static JSplitPane rightSplit;  // canvas | layers
    private static final String LEFT_DIVIDER = "layout.left.divider";
    private static final String RIGHT_DIVIDER = "layout.right.divider";
    private static final int DEFAULT_LAYERS_W = 220;

    public PixelGraphicEditor() {
        mainFrame = new JFrame("Pixel Graphic Editor");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Size the window to fit within the screen minus any bars/docks/etc
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

        int usableWidth = screenBounds.width - screenInsets.left - screenInsets.right;
        int usableHeight = screenBounds.height - screenInsets.top - screenInsets.bottom;

        mainFrame.setSize(usableWidth, usableHeight);
        mainFrame.setLocation(screenBounds.x + screenInsets.left, screenBounds.y + screenInsets.top);

        mainFrame.setMinimumSize(new Dimension(200, 200));
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(true);

        // Create and attach the UI components.
        MenuBars menuBars = MenuBars.getInstance();
        menuBars.createVerticalMenuBar();
        menuBars.createHorizontalMenuBar();
        createCanvas();

        // Resizable layout: tools | (canvas | layers), with draggable dividers.
        LayersPanel layersPanel = new LayersPanel(canvas);
        rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, layersPanel);
        rightSplit.setResizeWeight(1.0);   // canvas absorbs window resizing
        rightSplit.setContinuousLayout(true);
        rightSplit.setOneTouchExpandable(true);

        leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, MenuBars.verticalBar, rightSplit);
        leftSplit.setResizeWeight(0.0);    // the tool column keeps its width
        leftSplit.setContinuousLayout(true);
        leftSplit.setOneTouchExpandable(true);

        mainFrame.add(MenuBars.horizontalBar, BorderLayout.NORTH);
        mainFrame.add(leftSplit, BorderLayout.CENTER);

        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                canvas.repaint(); // Re-centre the fixed document when the window resizes.
            }
        });
        mainFrame.setResizable(true);
        mainFrame.setVisible(true);
        EventQueue.invokeLater(() -> {
            mainFrame.setAlwaysOnTop(false);

            // Show the help window only on the first ever launch, then remember it
            // (persisted in the config file) so it does not reopen every time.
            Configuration config = Configuration.getInstance();
            if (!config.is("help.shown", false)) {
                Timer timer = new Timer(500, e -> MenuBars.getInstance().showHelpDocumentation());
                timer.setRepeats(false);
                timer.start();
                config.properties.setProperty("help.shown", "true");
                try {
                    config.getUpdatedConfiguration();
                } catch (RuntimeException ex) {
                    // Non-fatal: if the flag can't be saved, help simply shows again next launch.
                }
            }

            // Restore saved panel sizes (or sensible defaults), then persist future changes.
            leftSplit.setDividerLocation(
                    config.getInt(LEFT_DIVIDER, MenuBars.verticalBar.getPreferredSize().width));
            rightSplit.setDividerLocation(
                    config.getInt(RIGHT_DIVIDER, Math.max(0, rightSplit.getWidth() - DEFAULT_LAYERS_W)));
            wireLayoutSaving(config);
        });
    }

    // Persist divider positions a moment after they change (debounced, so dragging
    // doesn't hammer the config file).
    private void wireLayoutSaving(Configuration config) {
        Timer saveTimer = new Timer(400, e -> {
            config.putInt(LEFT_DIVIDER, leftSplit.getDividerLocation());
            config.putInt(RIGHT_DIVIDER, rightSplit.getDividerLocation());
            config.save();
        });
        saveTimer.setRepeats(false);
        leftSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> saveTimer.restart());
        rightSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> saveTimer.restart());
    }

    // Reset the panels to their default sizes (Home -> Reset Layout).
    public static void resetLayout() {
        if (leftSplit == null || rightSplit == null) return;
        int defLeft = MenuBars.verticalBar.getPreferredSize().width;
        int defRight = Math.max(0, rightSplit.getWidth() - DEFAULT_LAYERS_W);
        leftSplit.setDividerLocation(defLeft);
        rightSplit.setDividerLocation(defRight);
        Configuration config = Configuration.getInstance();
        config.putInt(LEFT_DIVIDER, defLeft);
        config.putInt(RIGHT_DIVIDER, defRight);
        config.save();
    }

    private void createCanvas() {
        canvas = new CanvasPanel(); // the split-pane layout adds it to the frame
    }

    public static CanvasPanel getCanvas() {
        return canvas;
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 4);
            FlatLaf lookAndFeel;
            if (Configuration.getInstance().is("ui.dark.mode.disabled", true)) {
                lookAndFeel = new FlatMacDarkLaf();
            } else {
                lookAndFeel = new FlatMacLightLaf();
            }
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Only load when everything is initialised.
        SwingUtilities.invokeLater(PixelGraphicEditor::new);
    }
}