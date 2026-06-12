package com.danielomari.pixeleditor.util;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

public class SafeExit implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        int confirmQuit = JOptionPane.showConfirmDialog(null, "<html><b>Are you sure you want to exit the application?</b><html>", "Confirm exit", JOptionPane.YES_NO_OPTION);
        if (confirmQuit == JOptionPane.YES_OPTION) {
            if(Save.getHasSavedRecently())
                System.exit(0);
            else {
            int confirmSave = JOptionPane.showConfirmDialog(null, "<html><b>Do you want to save your work before exiting?</b><html>", "Confirm save", JOptionPane.YES_NO_OPTION);
                if (confirmSave == JOptionPane.YES_OPTION) {
                    Save saveOption = new Save();
                    saveOption.saveImage();
                    System.exit(0);
                }
                else {
                    System.exit(0);
                }
            }
        }
    }
}
