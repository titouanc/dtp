/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.sql.SQLException;

import javax.swing.*;

import be.ac.ulb.infof307.g03.controllers.MainPaneController;
import be.ac.ulb.infof307.g03.controllers.MenuBarController;
import be.ac.ulb.infof307.g03.controllers.ToolsBarController;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author julianschembri, pierre
 * @brief The graphical application launched in the HomePlans Main.
 */
public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private MenuBarController menuBar;
	private ToolsBarController toolsBar;
	private MainPaneController workspace;
	private SplashScreen screen;
	private Project project;
	
	/**
	 * Constructor of GUI.
	 * It put every frame needed at the right place on the main frame
	 * Menu, toolsbar and the main workspace (splitpane)
	 * @param aProject The project to be display on the GUI.
	 * @throws SQLException 
	 */
	public GUI(Project aProject) throws SQLException {
		
		// Create and set up the window
		super();
		project = aProject;
		updateTitle();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Sets up the loading screen
        this.screen = SplashScreen.getSplashScreen();
   
        // Create the menuBar
        this.menuBar = new MenuBarController(aProject, this);
        this.menuBar.run();
        this.setJMenuBar(this.menuBar.getView());
        
        // Create the main panel
        // http://docs.oracle.com/javase/tutorial/uiswing/components/toplevel.html
        JPanel contentPane = new JPanel(new BorderLayout());
        
        // Create the toolbar
        this.toolsBar = new ToolsBarController(aProject);
        this.toolsBar.run();
        contentPane.add(this.toolsBar.getView(), BorderLayout.PAGE_START);
     
        // Create the workspace
        // this one contains Jmonkey canvas and the left menu
        this.workspace = new MainPaneController(aProject);
        this.workspace.run();
        contentPane.add(this.workspace.getView(), BorderLayout.CENTER);
        
        // Add the workspace to the frame
        this.setContentPane(contentPane);
        
        // Set up resize behavior
        Dimension windowDimension = new Dimension(860, 480);
        this.setMinimumSize(windowDimension);
        this.setPreferredSize(windowDimension);
        
        this.pack();
        if(this.screen != null){
	        while(!this.workspace.getWc().getView().isCreated()){
	        	try {
		            Thread.sleep(2000);
		        }
		        	catch(InterruptedException ex) {
		        		Log.exception(ex);
		        }
	        }
	        this.screen.close();
        }
        // Display the window
        this.setVisible(true);
	}
	
    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"foo", "bar", "baz"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120,140,200,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading "+comps[(frame/5)%3]+"...", 120, 150);
    }
    
    public void updateTitle(){
    	setTitle("HomePlans - " + (project.isOnDisk() ? project.getFilename() : "Unsaved"));
    }

}
