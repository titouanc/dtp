/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

/**
 * @author julianschembri, pierre
 *
 */
public class GUI extends JFrame implements ComponentListener {
	/**
	 * Constructor of GUI.
	 * It put every frame needed at the right place on the main frame
	 * Menu, toolsbar and the main workspace (splitpane)
	 */
	
	private static final long serialVersionUID = 1L;
	
	public GUI() {
		// Create and set up the window
		super("HomePlans");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        this.setJMenuBar(new MenuBar());
        
        // Create the main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        // creation of toolbar
        ToolsBar toolsBar = new ToolsBar();
        contentPane.add(toolsBar, BorderLayout.NORTH);
     
        // Creation of the splitPane
        // this pane contains Jmonkey
        MainPane workspace = new MainPane();
        contentPane.add(workspace, BorderLayout.SOUTH);
        
        // add main panel to the frame
        this.setContentPane(contentPane);
        
        // handle resize event
        this.addComponentListener(this);
        
        // Display the window
        this.pack();
        this.setVisible(true);
        
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		System.out.println("plop");
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
