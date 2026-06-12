package com.danielomari.pixeleditor.util;

import com.danielomari.pixeleditor.PixelGraphicEditor;

import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Help{
    private final JDialog helpDialog;
    private final JTextPane instructionPane;

    public Help(Frame parent) {
        helpDialog = new JDialog(parent, "Help Documentation", true);
        instructionPane = createInstructionPane(); 
        initializeDialog();
    }

    private JTextPane createInstructionPane() {
        JTextPane pane = new JTextPane();
        pane.setContentType("text/html");
        pane.setText("<html><div style='padding:20px;text-align:center;'>"
                + "<h1>Pixel Graphic Editor</h1>"
                + "<h2>Help Documentation</h2>"
                + "<p>Click on Home &rarr; Help to view this Menu</p>"
                   + "<h2>Select a tool to view instructions</h2></div></html>");
        pane.setEditable(false);
        return pane;
    }

    private void initializeDialog() {
        helpDialog.setLayout(new BorderLayout());
        helpDialog.setSize(800, 600);
        helpDialog.setLocationRelativeTo(helpDialog.getParent());

        // Add components
        helpDialog.add(createHelpHorizontalBar(), BorderLayout.NORTH);
        helpDialog.add(createHelpVerticalBar(), BorderLayout.WEST);
        helpDialog.add(new JScrollPane(instructionPane), BorderLayout.CENTER);
        helpDialog.add(createBottomPanel(), BorderLayout.SOUTH);

        // ESC key support
        helpDialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE");
        helpDialog.getRootPane().getActionMap().put("CLOSE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                helpDialog.dispose();
            }
        });
    }
    
    private JPanel createHelpHorizontalBar() {
        JPanel horizontalBar = new JPanel(new GridLayout(1, 8));
        String[] buttons = {"Home", "File", "Insert", "Save", "Undo", "Redo", "Exit"};
        
        for (String label : buttons) {
            JButton btn = new JButton(label);
            btn.setFont(new Font("Comic Sans", Font.BOLD, 14));
            btn.addActionListener(e -> updateInstructions(label));
            horizontalBar.add(btn);
        }
        return horizontalBar;
    }

    private JPanel createHelpVerticalBar() {
        JPanel verticalBar = new JPanel(new GridLayout(12, 1));
        String[] tools = {"Brush", "Pencil", "Eraser", "Colour", "Shape", 
                        "Select", "Zoom", "Rotate", "Text", "Icon Only Mode", "Dark Mode", "Keybinds"};
        
        for (String tool : tools) {
            JButton btn = new JButton(tool);
            btn.setFont(new Font("Comic Sans", Font.BOLD, 16));
            btn.addActionListener(e -> updateInstructions(tool));
            verticalBar.add(btn);
        }
        return verticalBar;
    }

    private JPanel createBottomPanel() {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> helpDialog.dispose());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(okButton);
        return bottomPanel;
    }

    private void updateInstructions(String componentName) {
        String instructions = getInstructionsForComponent(componentName);
        instructionPane.setText(instructions);
    }

    private String getInstructionsForComponent(String componentName) {
        // Customize these instructions according to your application's functionality
        switch (componentName) {
            case "Home":
            return "<html><div style='padding: 10px;'>"
                + "<h3>Home</h3>"
                + "<p><b>Contains sub-menus:</b></p>"
                + "<ul>"
                + "<li>Canvas Selection</li>"
                + "<li>Auto Save</li>"
                + "<li>Exit</li>"
                + "<li>Help</li>"
                + "</ul>"
                + "<p><b>Instructions</b></p>"
                + "<ul>"
                + "<li><b>Canvas Selection</b>: Adjusts canvas dimensions. (tbc)</li>"
                + "<li><b>Auto save</b>: Saves automatically every <b>5 seconds</b> (default). Toggle using <b>Auto Save</b> button.</li>"
                + "<li><b>Exit</b>: Exit the function without hassle. </li>"
                + "<li><b>Help</b>: View function details by clicking button in help window</li>"
                + "</ul></div></html>";
    
            case "Brush":
                return "<html><div style='padding: 10px;'>"
                        + "<h3>Brush</h3>"
                        + "<p><b>Instructions</b></p>"
                        + "<ul>"
                        + "<li>Click the <b>Brush</b> button</li>"
                        + "<li>Select a <b>brush type</b></li>"
                        + "<li>Choose a <b>size</b></li>"
                        + "<li>Use <b>left/right mouse buttons</b> to draw</li>"
                        + "</ul>"
                        + "<p><b>Brush Sizes</b></p>"
                        + "<ul>"
                        + "<li>Small</li>"
                        + "<li>Medium (default)</li>"
                        + "<li>Large</li>"
                        + "</ul>"
                        + "<p><b>Contains 5 brush types:</b></p>"
                        + "<ul>"
                        + "<li>Natural Pencil</li>"
                        + "<li>Spray</li>"
                        + "<li>Dotted Lines</li>"
                        + "<li>Oil Brush</li>"
                        + "<li>Star Pattern</li>"
                        + "</ul>"
                        + "</div></html>";
    
            case "Text":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Text</h3>"
                    + "<p>Supports all fonts available in GraphicsEnvironment that support regular characters.</p>"
                    + "<p><b>Instructions</b></p>"
                    + "<ol>"
                    + "<li>Click <b>Text</b> button</li>"
                    + "<li><b>Left-click</b> canvas to create text field</li>"
                    + "<li>Enter text and press <b>Enter</b></li>"
                    + "<li><b>Right-click</b> to adjust font size</li>"
                    + "<li>Press <b>ESC</b> to exit</li>"
                    + "</ol></div></html>";
    
            case "Undo":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Undo</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click to revert last action</li>"
                    + "</ul></div></html>";

            case "Redo":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Redo</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click to redo last undone action</li>"
                    + "</ul></div></html>";           

            case "Rotate":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Rotate</h3>"
                    + "<p><b>Rotation Options</b></p>"
                    + "<ul>"
                    + "<li>Rotate Right 90\u00B0 </li>"
                    + "<li>Rotate Left 90\u00B0 </li>"
                    + "<li>Rotate 180\u00B0 </li>"
                    + "<li>Flip Vertical</li>"
                    + "<li>Flip Horizontal</li>"
                    + "</ul>"
                    + "<p><b>Instructions</b></p>"
                    + "<p><b>Full Canvas Rotation:</b></p>"
                    + "<ul>"
                    + "<li>Click the <b>Rotate</b> button</li>"
                    + "</ul>"
                    + "<p><b>Specific Area Rotation:</b></p>"
                    + "<ul>"
                    + "<li>Use <b>Select tool</b> to choose area</li>"
                    + "<li>Click <b>Rotate</b> button</li>"
                    + "</ul>"
                    + "<p><b>Inserted Image Rotation:</b></p>"
                    + "<ul>"
                    + "<li>Insert image using <b>Insert</b> button</li>"
                    + "<li>Use <b>Select tool</b> to rotate image</li>"
                    + "<li>Click empty space to rotate canvas instead</li>"
                    + "</ul></div></html>";
            case "Zoom":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Zoom</h3>"
                    + "<p><b>Zoom Levels</b></p>"
                    + "<ul>"
                    + "<li>Maximum: 300%</li>"
                    + "<li>Minimum: 10%</li>"
                    + "</ul>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li><b>Left-click</b> to zoom in</li>"
                    + "<li><b>Right-click</b> to zoom out</li>"
                    + "<li>Press <b>M</b> on the keyboard to quickly switch to the tool </li>"
                    + "<li>Click the <b>Zoom</b> button again or press <b>M</b> again to reset magnification level</li>"
                    + "</ul></div></html>";
            case "Eraser":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Eraser</h3>"
                    + "<p><b>Eraser Sizes</b></p>"
                    + "<ul>"
                    + "<li>Small</li>"
                    + "<li>Medium</li>"
                    + "<li>Large</li>"
                    + "<li>Extreme</li>"
                    + "</ul>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click the <b>Eraser</b> button</li>"
                    + "<li>Use <b>left/right mouse buttons</b> to erase</li>"
                    + "</ul></div></html>";
            case "Colour":
                // Add similar formatted instructions
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Colour</h3>"
                    + "<p><b>Colour Modes Available</b></p>"
                    + "<ul>"
                    + "<li>Swatches</li>"
                    + "<li>HSV</li>"
                    + "<li>HSL</li>"
                    + "<li>RGB</li>"
                    + "<li>CMYK</li>"
                    + "</ul>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Choose a colour from the palette</li>"
                    + "<li>The selected colour will be applied</li>"
                    + "<li><b>Right click</b> to fill colour</li>"
                    + "</ul></div></html>";
            case "Pencil":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Pencil</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click the <b>Pencil</b> button</li>"
                    + "<li>Use <b>left/right mouse buttons</b> to draw</li>"
                    + "</ul></div></html>";
            case "Shape":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Shape</h3>"
                    + "<p><b>Provides 6 shape options:</b></p>"
                    + "<ul>"
                    + "<li>Rectangle</li>"
                    + "<li>Circle</li>"
                    + "<li>Line</li>"
                    + "<li>Triangle</li>"
                    + "<li>Pentagon</li>"
                    + "<li>Hexagon</li>"
                    + "</ul>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click the <b>Shape</b> button</li>"
                    + "<li>Use <b>left/right mouse buttons</b> to draw</li>"
                    + "</ul></div></html>";
            case "Icon Only Mode":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Icon Only Mode</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click to toggle interface mode</li>"
                    + "</ul></div></html>";

            case "Dark Mode":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Dark Mode</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click to toggle between themes</li>"
                    + "</ul></div></html>"; 
                    
            case "File":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>File</h3>"
                    + "<p><b>Options</b></p>"
                    + "<ul>"
                    + "<li><b>New:</b> Create new file</li>"
                    + "<li><b>Open:</b> Select existing file</li>"
                    + "<li><b>Save:</b> Save current file</li>"
                    + "<li><b>Save As:</b> Save in specific format</li>"
                    + "</ul>"
                    + "<p><b>Supported Formats (Save As):</b></p>"
                    + "<ul>"
                    + "<li>JPEG</li>"
                    + "<li>BMP</li>"
                    + "<li>PNG</li>"
                    + "</ul></div></html>"
                    + "<p><b>Instructions</b></p>"
                    + "<ul>"
                    + "<li>Click <b>File</b> to manage documents</li>"
                    + "</ul></div></html>";

            case "Exit":
                return "<html><div style='padding: 10px;'>"
                    + "<h3>Exit</h3>"
                    + "<p><b>Instructions</b></p>"
                    + "<ol>"
                    + "<li>Click <b>Exit</b> button</li>"
                    + "<li>Choose save option:"
                    + "<ul>"
                    + "<li>Yes: Save and exit</li>"
                    + "<li>No: Exit without saving</li>"
                    + "</ul></li>"
                    + "</ol></div></html>";
            case "Insert":
                return "<html><div style='padding: 10px;'>"
                     + "<h3>Insert</h3>"
                     + "<p><b>Instructions</b></p>"
                     + "<ol>"
                     + "<li>Click <b>Insert</b> button</li>"
                     + "<li>Choose file from dialog</li>"
                     + "<li>Click <b>OK</b> to confirm</li>"
                     + "</ol></div></html>";

            case "Save As":
                return "<html><div style='padding: 10px;'>"
                      + "<h3>Save As</h3>"
                      + "<p><b>Supported Formats</b></p>"
                      + "<ul>"
                      + "<li>JPEG</li>"
                      + "<li>BMP</li>"
                      + "<li>PNG</li>"
                      + "</ul>"
                      + "<p><b>Instructions</b></p>"
                      + "<ol>"
                      + "<li>Click <b>Save As</b></li>"
                      + "<li>Select destination folder</li>"
                      + "<li>Choose file format</li>"
                      + "<li>Click <b>Save</b></li>"
                      + "</ol></div></html>";

            case "Save":
                return "<html><div style='padding: 10px;'>"
                     + "<h3>Save</h3>"
                     + "<p><b>Instructions</b></p>"
                     + "<ol>"
                     + "<li>Click the <b>Save</b> button</li>"
                     + "<li>Choose a file location</li>"
                     + "<li>Click <b>Save</b> to confirm</li>"
                     + "</ol></div></html>";

            case "Select":
                return "<html><div style='padding: 10px;'>"
                      + "<h3>Select</h3>"
                      + "<p><b>Instructions</b></p>"
                      + "<ol>"
                      + "<li>Drag to create selection area:"
                      + "<ul>"
                      + "<li>Use <b>left mouse</b> or <b>right mouse</b> for selection</li>"
                      + "</ul></li>"
                      + "<li>Manipulate selection:"
                      + "<ul>"
                      + "<li><b>Left-click</b> + drag inside area to move</li>"
                      + "<li><b>Right-click</b> for sub-functions</li>"
                      + "</ul></li>"
                        + "<li>Select All:"
                        + "<ul>"
                        + "<li>Press <b>Ctrl A</b> to select the entire canvas</li>"
                        + "</ul></li>"
                      + "<li>Modify selection colour:"
                      + "<ul>"
                      + "<li>Click <b>colour option</b> and choose colour</li>"
                      + "<li>Pixels within selected boundaries will update automatically</li>"
                      + "</ul></li>"
                      + "</ol>"
                      + "<p><b>Sub-functions</b></p>"                          + "<ul>"
                      + "<li><b>Copy</b> - Duplicates selected content</li>"
                      + "<li><b>Cut</b> - Removes and stores selected content</li>"
                      + "<li><b>Paste</b> - Places stored content on canvas</li>"
                      + "<li><b>Delete</b> - Permanently removes selection</li>"
                      + "</ul></div></html>";
            case "Keybinds":
                return "<html><div style='padding: 10px;'>"
                     + "<h3>Keybinds</h3>"
                     + "<p><b>General</b></p>"
                     + "<ul>"
                     + "<li><b>Ctrl + Z</b> - Undo</li>"
                     + "<li><b>Ctrl + Y</b> - Redo</li>"
                     + "<li><b>Ctrl + S</b> - Save</li>"
                     + "<li><b>Ctrl + Shift + S</b> - Save As</li>"
                     + "<li><b>Ctrl + N</b> - New File</li>"
                     + "<li><b>Ctrl + O</b> - Open File</li>"
                     + "<li><b>Ctrl + Q</b> - Exit</li>"
                     + "<li><b>Ctrl + H</b> - Help</li>"
                     + "</ul>"
                     + "<p><b>Tool Shortcuts</b></p>"
                     + "<ul>"
                     + "<li><b>B</b> - Brush</li>"
                     + "<li><b>P</b> - Pencil</li>"
                     + "<li><b>E</b> - Eraser</li>"
                     + "<li><b>C</b> - Colour</li>"
                     + "<li><b>S</b> - Shape</li>"
//                     + "<li><b>L</b> - Select</li>"
                     + "<li><b>Z</b> - Zoom</li>"
//                     + "<li><b>R</b> - Rotate</li>"
                     + "<li><b>T</b> - Text</li>"
//                     + "<li><b>I</b> - Icon Only Mode</li>"
//                     + "<li><b>D</b> - Dark Mode</li>"
                     + "</ul></div></html>";

            default:
                return "<html><div style='padding: 10px;'>"
                     + "<h3>" + componentName + "</h3>"
                     + "<p>No specific instructions available for this component.</p>"
                     + "</div></html>";
        }
    }

    public void showHelp() {
        helpDialog.setVisible(true);
    }

}