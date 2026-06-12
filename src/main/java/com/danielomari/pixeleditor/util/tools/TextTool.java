package com.danielomari.pixeleditor.util.tools;

import com.danielomari.pixeleditor.commands.CommandManager;
import com.danielomari.pixeleditor.commands.Drawcommand;
import com.danielomari.pixeleditor.util.tools.Tool;
import com.danielomari.pixeleditor.ui.CanvasPanel;
import com.danielomari.pixeleditor.util.tools.ColorTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;    // For generic List
import java.util.Arrays;  // For Arrays.asList()

public class TextTool implements Tool {
    private static TextTool instance;
    private CanvasPanel canvas;
    private JTextField textField;
    private String textContent = "";
    private int fontSize = 16; // Default font size
    private Color currentColor;
    private Drawcommand currentCommand;
    private boolean isTyping = false;
    private Point startPoint;
    private String selectedFont = "Arial"; // Default font

    public TextTool() {
        this.currentColor = ColorTool.getColor(); // Default color from ColorTool
        this.selectedFont = "Arial";
    }

    public static TextTool getInstance() {
        if (instance == null) {
            instance = new TextTool();
        }
        return instance;
    }

    public void setSelectedFont(String font) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontArray = ge.getAvailableFontFamilyNames();
        List<String> availableFonts = Arrays.asList(fontArray);
        
        if (availableFonts.contains(font)) {
            this.selectedFont = font;
        } else {
            System.err.println("Font " + font + " not found. Falling back to Arial.");
            this.selectedFont = "Arial";
        }
    }

    public String getSelectedFont(){
        System.out.println("In text getSelectedFont, text is " + selectedFont);
        return selectedFont;
    }

    public void setCanvas(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    public void setTextContent(String text) {
        this.textContent = text;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
    }

    //develop a method to get color from colorTool
    private Color setColor() {
        Color color = ColorTool.getColor();
        return color;
    }

    @Override
    public void onPress(MouseEvent e) {
        if (!isTyping) { // Activate only when not typing
            if (e.getButton() == MouseEvent.BUTTON1) { // Left click
                handleTextInput(e.getPoint());
            } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                handleFontSizeChange();
            }
        }
    }

    private void handleTextInput(Point position) {
        currentCommand = new Drawcommand(CanvasPanel.getInstance());
        canvas = CanvasPanel.getInstance();
        startPoint = position;

        createTextField();
        setupTextFieldListeners();
    }

    private void createTextField() {
        isTyping = true;
        textField = new JTextField(20);
        textField.setBounds(startPoint.x, startPoint.y, 200, 30);
        textField.setForeground(currentColor);
        textField.setFont(new Font(selectedFont, Font.PLAIN, fontSize));
        canvas.add(textField);
        textField.requestFocus();
    }

    private void setupTextFieldListeners() {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                removeTextField();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    removeTextField();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    finalizeTextInput();
                }
            }
        });
    }

    private void handleFontSizeChange() {
        String input = JOptionPane.showInputDialog(
            canvas,
            "Enter font size (" + fontSize + " is current):",
            "Font Size Settings",
            JOptionPane.PLAIN_MESSAGE
        );
    
        if (input != null) {  // User didn't press Cancel
            try {
                int newSize = Integer.parseInt(input);
                if (newSize > 0) {
                    fontSize = newSize;
                } else {
                    JOptionPane.showMessageDialog(canvas, 
                        "Invalid size. Keeping current: " + fontSize,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(canvas, 
                    "Invalid number. Keeping current: " + fontSize,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void finalizeTextInput() {
        String textContent = textField.getText().trim();
        if (!textContent.isEmpty()) {
            drawText(startPoint.x, startPoint.y, textContent, fontSize);
        }
        removeTextField();
    }

    // Remove the text field properly
    private void removeTextField() {
        if (textField != null) {
            canvas.remove(textField);
            canvas.repaint();
            isTyping = false;
        }
    }

    // Prompt the user for font size using a dialog box
    public void promptForFontSize() {
        String input = JOptionPane.showInputDialog(canvas, "Enter font size:", "Font Size", JOptionPane.PLAIN_MESSAGE);

        // If user cancels the dialog, input will be null
        if (input != null) {
            try {
                int size = Integer.parseInt(input);
                if (size > 0) {
                    fontSize = size;
                    System.out.println("Font size set to: " + fontSize);

                    // Automatically create text box after font size is set
                    // Calculate center position of canvas for default placement
                    int centerX = canvas.getWidth() / 2;
                    int centerY = canvas.getHeight() / 2;
                    Point defaultPosition = new Point(centerX, centerY);

                    // Store command for undo/redo
                    currentCommand = new Drawcommand(canvas);
                    startPoint = defaultPosition;

                    // Create and setup text field
                    createTextField();
                    setupTextFieldListeners();

                } else {
                    JOptionPane.showMessageDialog(canvas, "Please enter a valid positive number for font size.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(canvas, "Invalid number. Font size is set to 16.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                fontSize = 16;

                // Still create text field with default size
                int centerX = canvas.getWidth() / 2;
                int centerY = canvas.getHeight() / 2;
                Point defaultPosition = new Point(centerX, centerY);

                currentCommand = new Drawcommand(canvas);
                startPoint = defaultPosition;

                createTextField();
                setupTextFieldListeners();
            }

        }
    }

    // Draw text on the canvas at the specified position
    private void drawText(int x, int y, String text, int fontSize) {
        // Store the state before action
        if (currentCommand == null) {
            currentCommand = new Drawcommand(canvas);
        }

        Graphics2D g = canvas.getCanvasImage().createGraphics();

        // Basic settings without fancy anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        // Create the font with exact pixel size
        Font textFont = new Font(selectedFont, Font.PLAIN, fontSize);
        g.setFont(textFont);

        // Get the color but make it slightly transparent to reduce "thickness"
        Color textColor = setColor();
        // Make color slightly transparent (adjust last parameter 0.9f if needed)
        Color adjustedColor = new Color(textColor.getRed()/255f,
                textColor.getGreen()/255f,
                textColor.getBlue()/255f, 0.9f);
        g.setColor(adjustedColor);

        // Draw the text
        g.drawString(text, x, y);
        g.dispose();

        // Store the state after the action
        if (currentCommand != null) {
            currentCommand.storeAfterState();
            CommandManager.getInstance().executeCommand(currentCommand);
            currentCommand = null;

            //System.out.println("Last action stored");
        }
    }


    @Override
    public void onRelease(MouseEvent e) {}

    @Override
    public void onDrag(MouseEvent e) {}
    
}


/* 
 * Changes:
 * Logic: 
 * 1. Left Clicking canvas will prompt user to enter text
 * If the user press esc, the dialog will disappear
 * 
 * 2. Right Clicking canvas will prompt user to enter font size. Default font size = 16
 * If the user press esc, the dialog will disappear.
 * If the user enter invalid font size, set the font size = current font size
 * 
*/