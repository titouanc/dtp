/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController {
	
	private WorldView _view;
	private boolean _isViewCreated = false;
	CameraModeController _cameraModeController = new CameraModeController();
	
	/**
	 * Constructor of WorldController.
	 * It creates the controller view.
	 */
	public WorldController(AppSettings settings){
		_view = new WorldView(this);
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
