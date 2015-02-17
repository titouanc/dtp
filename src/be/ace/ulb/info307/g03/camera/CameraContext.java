package be.ace.ulb.info307.g03.camera;


import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.world.WorldView;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * @author julianschembri
 * 
 */
public class CameraContext implements AnalogListener, ActionListener, Observer {

	// Input alias
	static private final String STRAFELEFT 	= "StrafeLeft";
	static private final String STRAFERIGHT	= "StrafeRight"; 
	static private final String FORWARD 		= "Forward";
	static private final String BACKWARD		= "Backward"; 
	static private final String MOVEDRAG		= "MoveDrag";

	static private final String LEFT 			= "Left";
	static private final String RIGHT			= "Right";
	static private final String UP				= "Up";
	static private final String DOWN			= "Down";

	static private final String ZOOMIN			= "ZoomIn";
	static private final String ZOOMOUT		= "ZoomOut";

	static private final String ROTATELEFT 	= "RotateLeft";
	static private final String ROTATERIGHT	= "RotateRight";
	
    // Camera mode alias
	static private final String MODE_DRAGROTATE = "dragRotate";
    // static private final String MODE_DRAGSELECT = "dragSelect";
    static private final String MODE_DRAGMOVE = "dragMove";
	
    // Flags
    private boolean canMove	= false;
    private boolean canRotate	= false;
	
	private CameraController controller;
	private InputManager inputManager;
	private WorldView worldView;
	private Vector3f previousMousePosition;
	private Camera camera;
	private String mouseMode;
	
	/**
	 * @param projet
	 * @param camera
	 * @param interManager
	 * @param worldView
	 */
	public CameraContext(Project projet, Camera camera, InputManager interManager, WorldView worldView){
		projet.addObserver(this);
		this.camera = camera;
		this.inputManager = interManager;
		this.worldView = worldView;
		inputSetUp();
		String camMode = projet.config("camera.mode");
		if (camMode.isEmpty()){
			camMode = "2D";
			projet.config("camera.mode", camMode);
		}
		updateController(camMode);
		this.mouseMode = projet.config("mouse.mode");
	}
	
	CameraController getcontroller() {
		return this.controller;
	}
	
	void setcontroller(CameraController controller) {
		this.controller = controller;
	}

	/**
	 * Calculate the position on the ground where the mouse is projected
	 * @return The projected position of the mouse on the ground
	 */
	private Vector3f mouseOnGroundCoords() {
		Vector2f click2d = this.inputManager.getCursorPosition();
		Vector3f click3d = this.camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		Vector3f dir = this.camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
		float mul = click3d.z / dir.z;
		return new Vector3f(click3d.x - (mul*dir.x),click3d.y - (mul*dir.y),0);
	}
	
	/**
	 * Method that binds the keys to their actions
	 */
	public void inputSetUp() {
		// Key event mapping
		this.inputManager.addMapping(STRAFELEFT,	new KeyTrigger(KeyInput.KEY_LEFT));
		this.inputManager.addMapping(STRAFERIGHT,	new KeyTrigger(KeyInput.KEY_RIGHT));
		this.inputManager.addMapping(FORWARD,   	new KeyTrigger(KeyInput.KEY_UP));
		this.inputManager.addMapping(BACKWARD,		new KeyTrigger(KeyInput.KEY_DOWN));
		
		this.inputManager.addMapping(ROTATELEFT, 	new KeyTrigger(KeyInput.KEY_O));
		this.inputManager.addMapping(ROTATERIGHT, 	new KeyTrigger(KeyInput.KEY_P));
		
		this.inputManager.addMapping(ZOOMIN,	 	new KeyTrigger(KeyInput.KEY_ADD));
		this.inputManager.addMapping(ZOOMOUT, 		new KeyTrigger(KeyInput.KEY_SUBTRACT));

		// Mouse event mapping
		this.inputManager.addMapping(MOVEDRAG, 	new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		this.inputManager.addMapping(UP, 			new MouseAxisTrigger(1, false));
		this.inputManager.addMapping(DOWN, 		new MouseAxisTrigger(1, true));
		this.inputManager.addMapping(LEFT,			new MouseAxisTrigger(0, true));
		this.inputManager.addMapping(RIGHT,		new MouseAxisTrigger(0, false));
		
		this.inputManager.addMapping(ZOOMIN, 		new MouseAxisTrigger(2, false));
        this.inputManager.addMapping(ZOOMOUT, 		new MouseAxisTrigger(2, true));

		// Add the names to the action listener
		this.inputManager.addListener(	this, 
				STRAFELEFT, 
				STRAFERIGHT, 
				FORWARD, 
				BACKWARD, 

				MOVEDRAG, 

				UP, 
				DOWN,
				LEFT, 
				RIGHT, 

				ROTATELEFT,
				ROTATERIGHT,

				ZOOMIN,
				ZOOMOUT
				);
	}
	
	/**
	 * This function is used to control the camera using the mouse with a click and grab 
	 * @param value Is positive if it's a forward move
	 * @param axis Is the direction where the mouse moves
	 */
	private void clickAndGrab(float value,Vector3f axis) {
		if (this.canMove) {
			this.controller.moveCameraByGrab(this.previousMousePosition, mouseOnGroundCoords());
			this.previousMousePosition = mouseOnGroundCoords();
		} else if (this.canRotate) { 
			this.controller.rotateCameraByGrab(value,axis);
		}
	}
	
	private void updateController(String value) {
		Log.debug("[DEBUG] CameraContext controller updated : " + value);
		if (value.equals("2D")) {
			setcontroller(new Camera2D(this.camera));
		} else if (value.equals("3D")) {
			setcontroller(new Camera3D(this.camera));
		}
		this.controller.resetCamera(this.worldView);
	}
	
	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (name.equals(MOVEDRAG)) { 
			this.previousMousePosition = mouseOnGroundCoords();
			if (this.mouseMode.equals(MODE_DRAGMOVE)) {
				this.canMove = value;
			} else if (this.mouseMode.equals(MODE_DRAGROTATE)){
				this.canRotate = value;
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.jme3.input.controls.AnalogListener#onAnalog(java.lang.String, float, float)
	 */
	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(STRAFERIGHT)) {
			this.controller.moveCamera(-value,false);
		} else if (name.equals(STRAFELEFT)) {
			this.controller.moveCamera(value,false);
		} else if (name.equals(FORWARD)) {
			this.controller.moveCamera(value,true);
		} else if (name.equals(BACKWARD)) {
			this.controller.moveCamera(-value,true);
		} else if (name.equals(LEFT)) {
			clickAndGrab(-value, this.camera.getUp());
		} else if (name.equals(RIGHT)) {
			clickAndGrab(value, this.camera.getUp());
		} else if (name.equals(UP)) {
			clickAndGrab(value, this.camera.getLeft());
		} else if (name.equals(DOWN)) {
			clickAndGrab(-value, this.camera.getLeft());
		} else if (name.equals(ROTATELEFT)) {
			this.controller.rotateCamera(value,true);
		} else if (name.equals(ROTATERIGHT)) {
			this.controller.rotateCamera(-value,false);
		} else if (name.equals(ZOOMIN)) {
			this.controller.zoomCamera(-value);
		} else if (name.equals(ZOOMOUT)) {
			this.controller.zoomCamera(value);
		}
	}
	
	/**
	 * 
	 */
	public void update(Observable obs, Object arg) {
		if (obs instanceof Project){
			Config param = (Config) arg;
			if (param.getName().equals("camera.mode")) {
				updateController(param.getValue());
			} else if (param.getName().equals("mouse.mode")){
				Log.info("Change mouse mode to %s", param.getValue());
				this.mouseMode = param.getValue();
			}
		}
	}
}
