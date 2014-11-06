/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.Dimension;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author fhennecker
 *
 */
public class MainPaneController {
	public MainPaneView view;
	
	private WorldController _world;
	
	MainPaneController(Project project){
		// Set up jme3 canvas' settings
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        // Create jme3 canvas
        _world = new WorldController();
        _world.view.setSettings(settings);
        _world.view.createCanvas();
        // Set up event listener
        JmeCanvasContext context = (JmeCanvasContext) _world.view.getContext();
        context.setSystemListener(_world.view);
        
        // Set up resize behavior
        Dimension jme3Dimension = new Dimension(640, 480);
        context.getCanvas().setMinimumSize(jme3Dimension);
        context.getCanvas().setPreferredSize(jme3Dimension);
        // Start jme3 canvas
        _world.view.startCanvas();
		view = new MainPaneView(project, context.getCanvas());
		
	}
	
	public void onApplicationStarted(){
		_world.createDemoGeometry();
	}
}
