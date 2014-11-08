/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController implements ActionListener {
	
	private WorldView _view;
	private boolean _isViewCreated = false;
	CameraModeController _cameraModeController = new CameraModeController();
	
	static private final String _SELECTOBJECT 	= "SelectObject";
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
		this.setInput();
	}	
	
	/**
	 * Method used to select an object when the user right-clicked on the canvas
	 */
	private void selectObject(Vector2f cursorPosition){
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
	
	private void setInput(){
		_view.getInputManager().addMapping(_SELECTOBJECT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		_view.getInputManager().addListener(this, _SELECTOBJECT);
	}

	@Override
	public void onAction(String arg0, boolean arg1, float arg2) {
        if (arg0.equals(_SELECTOBJECT) && arg1){
            this.selectObject(_view.getInputManager().getCursorPosition());
        }	
	}


}
