/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

/**
 * @author julianschembri, pierre
 *
 */
public class GUI {
	/**
	 * Constructor of GUI.
	 * It put every frame needed at the right place on the main frame
	 * Menu, toolsbar and the main workspace (splitpane)
	 */
	public GUI() {
		// Create and set up the window
		JFrame frame = new JFrame("HomePlans");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        frame.setJMenuBar(new MenuBar());
        
        // Creation du main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        // creation of toolbar
        ToolsBar toolsBar = new ToolsBar();
        contentPane.add(toolsBar, BorderLayout.NORTH);
     
        // Creation of the splitPane
        // this pane contains Jmonkey
        MainPane workspace = new MainPane();
        contentPane.add(workspace, BorderLayout.SOUTH);
        
        // ajoute le panel principal au frame
        frame.setContentPane(contentPane);
        
        // Display the window.
        frame.pack();
        frame.setVisible(true);
	}
}
