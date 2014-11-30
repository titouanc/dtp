package be.ac.ulb.infof307.g03.controllers;


import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

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
	static private final String _STRAFELEFT 	= "StrafeLeft";
	static private final String _STRAFERIGHT	= "StrafeRight"; 
	static private final String _FORWARD 		= "Forward";
	static private final String _BACKWARD		= "Backward"; 
	static private final String _MOVEDRAG		= "MoveDrag";

	static private final String _LEFT 			= "Left";
	static private final String _RIGHT			= "Right";
	static private final String _UP				= "Up";
	static private final String _DOWN			= "Down";

	static private final String _ZOOMIN			= "ZoomIn";
	static private final String _ZOOMOUT		= "ZoomOut";

	static private final String _ROTATELEFT 	= "RotateLeft";
	static private final String _ROTATERIGHT	= "RotateRight";
	
    // Carera mode alias
	static private final String _MODE_DRAGROTATE = "dragRotate";
    static private final String _MODE_DRAGSELECT = "dragSelect";
    static private final String _MODE_DRAGMOVE = "dragMove";
	
    // Flags
    private boolean _canMove	= false;
    private boolean _canRotate	= false;
	
	private CameraController _state;
	private InputManager _inputManager;
	private WorldView _wv;
	private Vector3f _previousMousePosition;
	private Camera _cam;
	private String _mouseMode;
	
	/**
	 * @param proj
	 * @param cam
	 * @param interManager
	 * @param wv
	 */
	public CameraContext(Project proj, Camera cam, InputManager interManager, WorldView wv){
		proj.addObserver(this);
		_cam = cam;
		_inputManager = interManager;
		_wv = wv;
		inputSetUp();
		String camMode = proj.config("camera.mode");
		if (camMode.isEmpty()){
			camMode = "2D";
			proj.config("camera.mode", camMode);
		}
		updateState(camMode);
		_mouseMode = proj.config("mouse.mode");
	}
	
	CameraController getState() {
		return _state;
	}
	
	void setState(CameraController state) {
		_state = state;
	}

	/**
	 * Calculate the position on the ground where the mouse is projected
	 * @return The projected position of the mouse on the ground
	 */
	private Vector3f mouseOnGroundCoords() {
		Vector2f click2d = _inputManager.getCursorPosition();
		Vector3f click3d = _cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		Vector3f dir = _cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
		float mul = click3d.z / dir.z;
		return new Vector3f(click3d.x - (mul*dir.x),click3d.y - (mul*dir.y),0);
	}
	
	/**
	 * Method that binds the keys to their actions
	 */
	public void inputSetUp() {
		// Key event mapping
		_inputManager.addMapping(_STRAFELEFT,	new KeyTrigger(KeyInput.KEY_LEFT));
		_inputManager.addMapping(_STRAFERIGHT,	new KeyTrigger(KeyInput.KEY_RIGHT));
		_inputManager.addMapping(_FORWARD,   	new KeyTrigger(KeyInput.KEY_UP));
		_inputManager.addMapping(_BACKWARD,		new KeyTrigger(KeyInput.KEY_DOWN));
		
		_inputManager.addMapping(_ROTATELEFT, 	new KeyTrigger(KeyInput.KEY_O));
		_inputManager.addMapping(_ROTATERIGHT, 	new KeyTrigger(KeyInput.KEY_P));
		
		_inputManager.addMapping(_ZOOMIN,	 	new KeyTrigger(KeyInput.KEY_ADD));
		_inputManager.addMapping(_ZOOMOUT, 		new KeyTrigger(KeyInput.KEY_SUBTRACT));

		// Mouse event mapping
		_inputManager.addMapping(_MOVEDRAG, 	new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		_inputManager.addMapping(_UP, 			new MouseAxisTrigger(1, false));
		_inputManager.addMapping(_DOWN, 		new MouseAxisTrigger(1, true));
		_inputManager.addMapping(_LEFT,			new MouseAxisTrigger(0, true));
		_inputManager.addMapping(_RIGHT,		new MouseAxisTrigger(0, false));
		
		_inputManager.addMapping(_ZOOMIN, 		new MouseAxisTrigger(2, false));
        _inputManager.addMapping(_ZOOMOUT, 		new MouseAxisTrigger(2, true));

		// Add the names to the action listener
		_inputManager.addListener(	this, 
				_STRAFELEFT, 
				_STRAFERIGHT, 
				_FORWARD, 
				_BACKWARD, 

				_MOVEDRAG, 

				_UP, 
				_DOWN,
				_LEFT, 
				_RIGHT, 

				_ROTATELEFT,
				_ROTATERIGHT,

				_ZOOMIN,
				_ZOOMOUT
				);
	}
	
	/**
	 * This function is used to control the camera using the mouse with a click and grab 
	 * @param value Is positive if it's a forward move
	 * @param axis Is the direction where the mouse moves
	 */
	private void clickAndGrab(float value,Vector3f axis) {
		if (_canMove) {
			_state.moveCameraByGrab(_previousMousePosition, mouseOnGroundCoords());
			_previousMousePosition = mouseOnGroundCoords();
		} else if (_canRotate) { 
			_state.rotateCameraByGrab(value,axis);
		}
	}
	
	private void updateState(String value) {
		Log.debug("[DEBUG] CameraContext state updated : " + value);
		if (value.equals("2D")) {
			setState(new Camera2D(_cam));
		} else if (value.equals("3D")) {
			setState(new Camera3D(_cam));
		}
		_state.resetCamera(_wv);
	}
	
	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (name.equals(_MOVEDRAG)) { 
			_previousMousePosition = mouseOnGroundCoords();
			if (_mouseMode.equals(_MODE_DRAGMOVE)) {
				_canMove = value;
			} else if (_mouseMode.equals(_MODE_DRAGROTATE)){
				_canRotate = value;
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.jme3.input.controls.AnalogListener#onAnalog(java.lang.String, float, float)
	 */
	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(_STRAFERIGHT)) {
			_state.moveCamera(-value,false);
		} else if (name.equals(_STRAFELEFT)) {
			_state.moveCamera(value,false);
		} else if (name.equals(_FORWARD)) {
			_state.moveCamera(value,true);
		} else if (name.equals(_BACKWARD)) {
			_state.moveCamera(-value,true);
		} else if (name.equals(_LEFT)) {
			clickAndGrab(-value, _cam.getUp());
		} else if (name.equals(_RIGHT)) {
			clickAndGrab(value, _cam.getUp());
		} else if (name.equals(_UP)) {
			clickAndGrab(value, _cam.getLeft());
		} else if (name.equals(_DOWN)) {
			clickAndGrab(-value, _cam.getLeft());
		} else if (name.equals(_ROTATELEFT)) {
			_state.rotateCamera(value,true);
		} else if (name.equals(_ROTATERIGHT)) {
			_state.rotateCamera(-value,false);
		} else if (name.equals(_ZOOMIN)) {
			_state.zoomCamera(-value);
		} else if (name.equals(_ZOOMOUT)) {
			_state.zoomCamera(value);
		}
	}
	
	/**
	 * 
	 */
	public void update(Observable obs, Object arg) {
		if (obs instanceof Project){
			Config param = (Config) arg;
			if (param.getName().equals("camera.mode")) {
				updateState(param.getValue());
			} else if (param.getName().equals("mouse.mode")){
				Log.info("Change mouse mode to %s", param.getValue());
				_mouseMode = param.getValue();
			}
		}
	}
}
