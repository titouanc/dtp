/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
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
	
	/**
	 * Method used to select an object when the user right-clicked on the canvas
	 */
	public void selectObject(Vector2f cursorPosition){
		float mouseX = cursorPosition.getX();
		float mouseY = cursorPosition.getY();
		
		Vector3f camPos = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 0f).clone();
		Vector3f camDir = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 1f).subtractLocal(camPos);
		Ray ray = new Ray(camPos, camDir);
		
		CollisionResults results = new CollisionResults();
		_view.getRootNode().collideWith(ray, results);
		for (int i = 0; i < results.size(); i++){
			System.out.println("ACH COLLIZION");
		}
	}

}
