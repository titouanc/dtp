/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.Dimension;
import java.sql.SQLException;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.FileChooserView;
import be.ac.ulb.infof307.g03.views.MainPaneView;

/**
 * @author fhennecker, pierre
 * @brief Controller of the MainPane containing the tree view on the left 
 * and the jMonkey Canvas on the right.
 */
public class MainPaneController {
	private MainPaneView view;
	private WorldController world;
	private Project project;
	
	/**
	 * Constructor of MainPaneController.
	 * @param project The project to be display on the MainPane
	 * @throws SQLException 
	 */
	public MainPaneController(Project project) throws SQLException{
		this.project = project;
		// Set up jme3 canvas' settings
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        settings.setAudioRenderer(null);
        settings.setUseInput(true);
        settings.setSamples(4); // enables antialiasing
        // Create jme3 canvas
        this.world = new WorldController(settings, project);
	}
	
	/**
	 * @author fhennecker
	 * Runs the MainPane GUI
	 */
	public void run(){
		
		this.world.run();
		
        // Set up event listener
        JmeCanvasContext context = (JmeCanvasContext) this.world.getViewContext();
        context.setSystemListener(this.world.getView());
        
        // Set up resize behavior
        Dimension jme3Dimension = new Dimension(640, 480);
        context.getCanvas().setMinimumSize(jme3Dimension);
        context.getCanvas().setPreferredSize(jme3Dimension);
        // Start jme3 canvas
        this.world.startViewCanvas();
        
		// Creating the MainPaneView, with the jMonkey Canvas we just created
		initView(this.project, context);
	}
	
	/**
	 * This method initiate the view
	 * @param project The project to be display on the MainPane
	 * @param context The jme world context
	 */
	public void initView(Project project, JmeCanvasContext context){
		this.view = new MainPaneView(this, project, context.getCanvas());
	}
	
	public WorldController getWc(){
		return this.world;
	}
	
	/**
	 * @return The controller's view
	 */
	public MainPaneView getView(){
		return this.view;
		
	}
}
