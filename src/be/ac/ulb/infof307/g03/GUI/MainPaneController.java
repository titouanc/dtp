/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.Dimension;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author fhennecker, pierre
 * @brief Controller of the MainPane.
 */
public class MainPaneController {
	private MainPaneView _view;
	private WorldController _world;
	
	/**
	 * Constructor of MainPaneController.
	 * @param The project to be display on the MainPane
	 */
	public MainPaneController(Project project){
		// Set up jme3 canvas' settings
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        // Create jme3 canvas
        _world = new WorldController(settings);
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
		_view = new MainPaneView(project, context.getCanvas());
		
	}
	
	/**
	 * @return The controller's view
	 */
	public MainPaneView getView(){
		return _view;
		
	}
	
	/**
	 * Create a demo on the MainPane
	 */
	public void createDemo(){ 
		_world.createDemoGeometry();
	}
}
