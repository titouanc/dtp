/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

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
        
        // Display the window.
        frame.pack();
        frame.setVisible(true);
	}
}
