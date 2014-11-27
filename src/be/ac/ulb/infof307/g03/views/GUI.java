/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.*;

import be.ac.ulb.infof307.g03.controllers.MainPaneController;
import be.ac.ulb.infof307.g03.controllers.MenuBarController;
import be.ac.ulb.infof307.g03.controllers.ToolsBarController;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author julianschembri, pierre
 * @brief The graphical application launched in the HomePlans Main.
 */
public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private MenuBarController _menuBar;
	private ToolsBarController _toolsBar;
	private MainPaneController _workspace;
	
	/**
	 * Constructor of GUI.
	 * It put every frame needed at the right place on the main frame
	 * Menu, toolsbar and the main workspace (splitpane)
	 * @param project The project to be display on the GUI.
	 * @throws SQLException 
	 */
	public GUI(Project project) throws SQLException {
		// Create and set up the window
		super("HomePlans" + (project.isOnDisk() ? " - " + project.getFilename() : "Unsaved"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create the menuBar
        _menuBar = new MenuBarController(project, this);
        _menuBar.run();
        this.setJMenuBar(_menuBar.getView());
        
        // Create the main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        // Create the toolbar
        _toolsBar = new ToolsBarController(project);
        contentPane.add(_toolsBar.getView(), BorderLayout.PAGE_START);
     
        // Create the workspace
        // this one contains Jmonkey canvas and the left menu
        _workspace = new MainPaneController(project);
        _workspace.run();
        contentPane.add(_workspace.getView(), BorderLayout.CENTER);
        
        // Add the workspace to the frame
        this.setContentPane(contentPane);
        
        // Set up resize behavior
        Dimension windowDimension = new Dimension(860, 480);
        this.setMinimumSize(windowDimension);
        this.setPreferredSize(windowDimension);
        
        // Display the window
        this.pack();
        this.setVisible(true);
	}

}
