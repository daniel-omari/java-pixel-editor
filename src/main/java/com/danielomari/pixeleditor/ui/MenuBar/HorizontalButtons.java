package com.danielomari.pixeleditor.ui.MenuBar;

import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import com.danielomari.pixeleditor.util.AutomaticSave;
import com.danielomari.pixeleditor.util.Save;
import com.danielomari.pixeleditor.util.tools.InsertTool;
import com.danielomari.pixeleditor.PixelGraphicEditor;

import com.danielomari.pixeleditor.util.Help;

import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;


public class HorizontalButtons {
    private AutoSaveType selectedAutoSaveType = AutoSaveType.OFF;
    private AutomaticSave autoSave = null;
    private final Save save = new Save();


    public HorizontalButtons() {
    }

    // Home Button Logic
    private void HomeButtonLogic(JButton button) {
        JPopupMenu popupMenu = new JPopupMenu();
        String[] options = {"Canvas Selection", "Auto Save: ", "Exit", "Help"};
        for (String option : options) {
            JMenuItem item = new JMenuItem(option);
            popupMenu.add(item);
            switch (option) {
                case "Canvas Selection":
                    item.addActionListener(e -> HomeButtonCanvasSelection());
                    break;
                case "Auto Save: ":
                    item.addActionListener(e -> HomeButtonAutoSave());
                    item.setText("Auto Save: " + selectedAutoSaveType);
                    break;
                case "Exit":
                    item.addActionListener(e -> System.exit(0));
                    break;
                case "Help":
                    item.addActionListener(e -> {
                        //parentMenu.setVisible(false);  // Close popup menu    
                        Help(popupMenu);
                        });
                    break;
            }
        }
        popupMenu.setVisible(true);
        popupMenu.show(button, 0, button.getHeight());
    }

    private void HomeButtonCanvasSelection() {
        System.out.println("HomeButtonCanvasSelection");
    }

    private void HomeButtonAutoSave() {
        // Auto save functionality
        if (selectedAutoSaveType == AutoSaveType.OFF) {
            selectedAutoSaveType = AutoSaveType.ON;
            System.out.println("Automatic Saving is turned On.\n");
            autoSave = new AutomaticSave();
        } else {
            selectedAutoSaveType = AutoSaveType.OFF;
            System.out.println("Automatic Saving is turned Off.\n");
            if (autoSave != null) {
                autoSave.stopAutoSave();
                return;
            }
        }
        System.out.println("Selected type: " + selectedAutoSaveType);
//        AutomaticSave autoSave = new AutomaticSave();


    }

    public void getHomeButton(JButton button) {
        HomeButtonLogic(button);
    }

    // File Button Logic
    private void FileButtonLogic(JButton button) {
        JPopupMenu popupMenu = new JPopupMenu();
        String[] options = {"New", "Open", "Save", "Save As"};
        for (String option : options) {
            JMenuItem item = new JMenuItem(option);
            popupMenu.add(item);

            switch (option) {
                case "New":
                    item.addActionListener(e -> FileButtonNew());
                    break;
                case "Open":
                    item.addActionListener(e -> FileButtonOpen());
                    break;
                case "Save":
                    item.addActionListener(e -> FileButtonSave());
                    break;
                case "Save As":
                    item.addActionListener(e -> FileButtonSaveAs());
                    break;
            }
        }
        popupMenu.setVisible(true);
        popupMenu.show(button, 0, button.getHeight());
    }

    private void FileButtonNew() {
        System.out.println("File Button New");
        int confirm = JOptionPane.showConfirmDialog(null, "<html><b>Are you sure you want to create a new file? This will clear the current canvas, any unsaved work will be lost.</b><html>", "Confirm New File", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            CommandManager.getInstance().clear();
            CanvasPanel.getInstance().clearCanvas();
            CanvasPanel.getInstance().repaint();

            save.getResetSave();
        } if(confirm == JOptionPane.NO_OPTION) {
            System.out.println("New File Cancelled");
        }
    }

    private void FileButtonOpen() {
        System.out.println("File Button Open");
        InsertTool insertTool = new InsertTool();
        insertTool.insert();
    }

    private void FileButtonSave() {
        System.out.println("File Button Save");
        Save.saveImage();
    }

    private void FileButtonSaveAs() {
        System.out.println("File Button Save As");
        Save.saveImageAs();
    }

    public void getFileButton(JButton button) {
        FileButtonLogic(button);
    }

    private void Help(JPopupMenu parentMenu) {
        System.out.println("File Button Help");
        parentMenu.setVisible(false); // Close the popup first
        
        // Get the parent frame correctly
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(PixelGraphicEditor.getCanvas());
        
        // Create and show help dialog
        Help helpTool = new Help(parentFrame);
        helpTool.showHelp();
    }


    private enum AutoSaveType {ON, OFF}
}
