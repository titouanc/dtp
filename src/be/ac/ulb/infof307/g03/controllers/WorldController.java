/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController {
	
	private WorldView _view;
	private boolean _isViewCreated = false;
	CameraModeController _cameraModeController;
	
	/**
	 * Constructor of WorldController.
	 * It creates the controller view.
	 * @throws SQLException 
	 */
	public WorldController(AppSettings settings, Project project) throws SQLException{
		_view = new WorldView(this, project.getGeometryDAO());
		_cameraModeController = new CameraModeController(project);
		_view.setSettings(settings);
		_view.createCanvas();
	}
	
	/**
	 * @return the world view.
	 */
	public WorldView getView(){
		return _view;
	}
	
	/**
	 * @return The view context.
	 */
	public JmeContext getViewContext(){
		return _view.getContext();
	}
	
	/** 
	 * @return The camera mode controller.
	 */
	public CameraModeController getCameraModeController() {
		return _cameraModeController;
	}
	
	/**
	 * Start the view canvas.
	 */
	public void startViewCanvas(){
		_view.startCanvas();
	}
	
	/**
	 * Gets called when the view is initialized.
	 */
	public void onViewCreated(){
		_isViewCreated = true;
	}	

}
