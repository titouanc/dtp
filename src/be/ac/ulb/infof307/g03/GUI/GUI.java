/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

/**
 * @author julianschembri
 *
 */
public class GUI {
	/**
	 * 
	 */
	public GUI() {
		// Create and set up the window
		JFrame frame = new JFrame("HomePlans");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        MenuBar menuBar = new MenuBar();
        frame.setJMenuBar(menuBar.createMenuBar());
        
        // Create the ToolsBar
        ToolsBar toolsBar = new ToolsBar();
        frame.add(toolsBar, BorderLayout.NORTH);
        
        // TODO Create blank content
        
        //JTextArea textArea = new JTextArea(5, 30);
        //textArea.setEditable(false);
        //JScrollPane scrollPane = new JScrollPane(textArea);
        
        frame.setPreferredSize(new Dimension(900, 600));
        //frame.add(scrollPane, BorderLayout.SOUTH);
        
        // Display the window.
        frame.pack();
        frame.setVisible(true);
	}
}
