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
	private MainPaneView _view;
	private WorldController _world;
	private Project _project;
	
	/**
	 * Constructor of MainPaneController.
	 * @param project The project to be display on the MainPane
	 * @throws SQLException 
	 */
	public MainPaneController(Project project) throws SQLException{
		_project = project;
		// Set up jme3 canvas' settings
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        settings.setAudioRenderer(null);
        settings.setUseInput(true);
        settings.setSamples(4); // enables antialiasing
        // Create jme3 canvas
        _world = new WorldController(settings, project);
	}
	
	/**
	 * @author fhennecker
	 * Runs the MainPane GUI
	 */
	public void run(){
		
		_world.run();
		
        // Set up event listener
        JmeCanvasContext context = (JmeCanvasContext) _world.getViewContext();
        context.setSystemListener(_world.getView());
        
        // Set up resize behavior
        Dimension jme3Dimension = new Dimension(640, 480);
        context.getCanvas().setMinimumSize(jme3Dimension);
        context.getCanvas().setPreferredSize(jme3Dimension);
        // Start jme3 canvas
        _world.startViewCanvas();
        
		// Creating the MainPaneView, with the jMonkey Canvas we just created
		initView(_project, context);
	}
	
	/**
	 * This method initiate the view
	 * @param project The project to be display on the MainPane
	 * @param context The jme world context
	 */
	public void initView(Project project, JmeCanvasContext context){
		_view = new MainPaneView(this, project, context.getCanvas());
	}
	
	/**
	 * @return The controller's view
	 */
	public MainPaneView getView(){
		return _view;
		
	}
}
