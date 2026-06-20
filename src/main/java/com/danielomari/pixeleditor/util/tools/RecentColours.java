package com.danielomari.pixeleditor.util.tools;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Fixed-size most-recently-used list of colours for the Colour panel.
public class RecentColours extends AbstractColorChooserPanel {
    private JPanel recentColours;

    @Override
    public void updateChooser() {
        updateRecentColors();
        repaint();
    }

    @Override
    protected void buildChooser() {
        setLayout(new BorderLayout());
        recentColours = new JPanel();
        recentColours.setLayout(new GridLayout(1, 5, 5, 5));
        add(recentColours, BorderLayout.CENTER);
        updateRecentColors();
    }

    @Override
    public String getDisplayName() {
        return "Recent Colours";
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    private void updateRecentColors() {
        recentColours.removeAll();
        List<Color> recentColors = ColorTool.getRecentColors();
        for (Color color : recentColors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.addActionListener(e -> getColorSelectionModel().setSelectedColor(color));
            recentColours.add(colorButton);
        }
        recentColours.revalidate();
        recentColours.repaint();
    }
}
