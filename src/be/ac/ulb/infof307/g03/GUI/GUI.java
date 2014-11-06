/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author julianschembri, pierre
 *
 */
public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor of GUI.
	 * It put every frame needed at the right place on the main frame
	 * Menu, toolsbar and the main workspace (splitpane)
	 */
	public GUI(Project project) {
		// Create and set up the window
		super("HomePlans");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        MenuBarController menuBar = new MenuBarController();
        this.setJMenuBar(menuBar.getView());
        
        // Create the main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        // Create the toolbar
        ToolsBarController toolsBar = new ToolsBarController();
        contentPane.add(toolsBar.view, BorderLayout.PAGE_START);
     
        // Create the workspace
        // this one contains Jmonkey canvas and the left menu
        MainPaneController workspace = new MainPaneController(project);
        contentPane.add(workspace.getView(), BorderLayout.CENTER);
        
        // Add the workspace to the frame
        this.setContentPane(contentPane);
        
        // Set up resize behavior
        Dimension windowDimension = new Dimension(640, 480);
        this.setMinimumSize(windowDimension);
        this.setPreferredSize(windowDimension);
        
        // Display the window
        this.pack();
        this.setVisible(true);
        
	}

}
