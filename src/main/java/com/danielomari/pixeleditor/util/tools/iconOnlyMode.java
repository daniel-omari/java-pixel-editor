package com.danielomari.pixeleditor.util.tools;
import com.danielomari.pixeleditor.util.tools.Tool;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

public class iconOnlyMode implements ActionListener {
    private List<JButton> buttonList;
    private boolean iconMode = false;
	private Map<JButton, ImageIcon> buttonIcons = new HashMap<>();
    private Map<JButton, String> buttonText = new HashMap<>();
    
    public iconOnlyMode(List<JButton> buttonList) {
	
		this.buttonList = buttonList;
	
    
    for (JButton button : buttonList) {
    	 if (button.getText().equals("Brush")) {
                buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/brush.png")));
                buttonText.put(button, "Brush");
                }
	else if (button.getText().equals("Pencil")) {
	 	buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/pencil.png")));
        	buttonText.put(button, "Pencil");
        	}
	else if (button.getText().equals("Eraser")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/eraser.png")));
		buttonText.put(button, "Eraser");
		}
	else if (button.getText().equals("Colour")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/colour.png")));
		buttonText.put(button, "Colour");
		}
	else if (button.getText().equals("Shape")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/shape.png")));
		buttonText.put(button, "Shape");
		}
	else if (button.getText().equals("Select")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/select.png")));
		buttonText.put(button, "Select");
		}
	else if (button.getText().equals("Zoom")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/zoom.png")));
		buttonText.put(button, "Zoom");
		}
	else if (button.getText().equals("Rotate")) {
		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/rotate.png")));
		buttonText.put(button, "Rotate");
		}
    	else     {
    		buttonIcons.put(button, new ImageIcon(getClass().getResource("/icons/text.png")));
		buttonText.put(button, "Text");
		}		
	}
	}
    
    public void actionPerformed(ActionEvent e) {
		iconMode = !iconMode;
		for (JButton button : buttonList) {
			if (iconMode) {
				button.setText("");
				button.setIcon(buttonIcons.get(button));
			}
			else {
				button.setText(buttonText.get(button));
				button.setIcon(null);
			}
		}
	}
    

}
