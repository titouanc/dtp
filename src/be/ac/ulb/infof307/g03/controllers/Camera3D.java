package be.ac.ulb.infof307.g03.controllers;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian, brochape
 */
public class Camera3D implements AnalogListener, ActionListener {

	// Attributes
	private Camera _cam;
	private InputManager _inputManager;
	private String _mouseMode = _MODE_DRAGSELECT;
	private Vector3f _previousMousePosition;
    private final int minimumHeight = 1;
	
	// Speed parameters
	private float _rotationSpeed 	= 1f;
	private float _moveSpeed 		= 10f;
	private float _zoomSpeed		= 4f;
	
	// Flags
	private boolean _canRotate 		= false;
	private boolean _canMove 		= false;
	private boolean _enabled 		= true;
	
	// Mouse Mode 
    static private final String _MODE_DRAGROTATE = "dragRotate";
    static private final String _MODE_DRAGSELECT = "dragSelect";
    static private final String _MODE_DRAGMOVE = "dragMove";
	
    // Input alias
	static private final String _STRAFELEFT 	= "CAM3D_StrafeLeft";
	static private final String _STRAFERIGHT	= "CAM3D_StrafeRight";
	static private final String _FORWARD		= "CAM3D_Forward";
	static private final String _BACKWARD		= "CAM3D_Backward";
	static private final String _MOVEDRAG		= "CAM3D_MOVEDRAG";
	static private final String _UP				= "CAM3D_Up";
	static private final String _DOWN			= "CAM3D_Down";
	static private final String _LEFT			= "CAM3D_Left";
	static private final String _RIGHT			= "CAM3D_Right";
	static private final String _ROTATELEFT		= "CAM3D_LoopLeft";
	static private final String _ROTATERIGHT	= "CAM3D_LoopRight";
	static private final String _ZOOMIN			= "CAM3D_ZoomIn";
	static private final String _ZOOMOUT		= "CAM3D_ZoomOut";
	
	// Default frustum parameters 
	static private float _defaultNear;
	static private float _defaultFar;
	static private float _defaultLeft;
	static private float _defaultRight;
	static private float _defaultTop;
	static private float _defaultBottom;
	
	// Default camera parameter
	private final int _defaultCameraZ = 40;
	
	/**
	 * Constructor of the 2D camera
	 */
	public Camera3D() {
	}

	/**
	 * @return True if the controller is enabled else false.
	 */
	public boolean isEnabled() {
		return _enabled;
	}

	/**
	 * Enable or disable the controller.
	 * @param enable True to enable the controller, false to disable the controller.
	 */
	public void setEnabled(boolean enable) {
		_enabled = enable;
	}

	/**
	 * Sets a camera
	 * @param cam The new camera to be set.
	 */
	public void setCam(Camera cam) {
		_cam = cam;
		
		// Reset frustum parameters
		_defaultNear = _cam.getFrustumNear();
		_defaultFar = _cam.getFrustumFar();
		_defaultLeft = _cam.getFrustumLeft();
		_defaultRight = _cam.getFrustumRight();
		_defaultTop = _cam.getFrustumTop();
		_defaultBottom = _cam.getFrustumBottom();
	}
	
	/**
	 * @param mouseMode The new mouse to be set.
	 */
	public void setMouseMode(String mouseMode) {
		_mouseMode = mouseMode;
	}
	
	/**
	 * Sets an input manager
	 * @param inputManager The new input manager to be set.
	 */
	public void setInputManager(InputManager inputManager) {
		System.out.println("ICI");

		_inputManager = inputManager;
		inputSetUp();
	}
	
	/**
	 * Reset the direction toward which the camera looks
	 */
	public void resetCamera() {
        _cam.setParallelProjection(false);
        
        _cam.setLocation(new Vector3f(_cam.getLocation().x,_cam.getLocation().y,_defaultCameraZ));
        //TODO : Unhardcode it ^ (get current zposition (correctly, it is currently fucked up) or set a default z-position)
        _cam.setFrustum(_defaultNear, _defaultFar, _defaultLeft, _defaultRight, _defaultTop,_defaultBottom);
	}
	
	

	/**
	 * Method to use to move the camera
	 * @param value Used to know if it's a forward or backward move
	 * @param sideways True if up or down, false if left or right
	 */
	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		
		// Choose the vector to follow
		if (sideways) { // forward or backward
			if (_cam.getDirection()!=Vector3f.UNIT_Z) {
				_cam.getDirection(vel);
			} else {
				_cam.getUp(vel);
			}
		} else { // strafe left or right
			_cam.getLeft(vel);
		}
		
		// Make this vector parallel to the Oxy plan
		vel.setZ(0);
		vel.normalizeLocal();
		
		// Find the new position
		vel.multLocal(value*_moveSpeed);
		pos.addLocal(vel);

		// Move the camera
		_cam.setLocation(pos);
	}
	
	/**
	 * Rotate the camera like a man can move his head using Quaternion
	 * @param value
	 * @param axis
	 */
	private void rotateCameraByGrab(float value, Vector3f axis) {
		if (!_canRotate) 
			return;
		
		Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(_rotationSpeed * value, axis);

        Vector3f up = _cam.getUp();
        Vector3f left = _cam.getLeft();
        Vector3f dir = _cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        _cam.setAxes(q);
	}
	
	/**
	 * Rotate the camera on itself 
	 * @param value 
	 */
	private void selfRotate(float value) {
		Matrix3f mat = new Matrix3f();
		mat.fromAngleNormalAxis(_rotationSpeed * value, _cam.getDirection());

		Vector3f up = _cam.getUp();
		Vector3f left = _cam.getLeft();
		Vector3f dir = _cam.getDirection();

		mat.mult(up, up);
		mat.mult(left, left);
		mat.mult(dir, dir);

		Quaternion q = new Quaternion();
		q.fromAxes(left, up, dir);
		q.normalizeLocal();

		_cam.setAxes(q);
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
	 * Move the camera where it looks
	 * @param value
	 */
	private void zoomCamera(float value) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = _cam.getDirection().clone();
		vel.multLocal(value*_zoomSpeed);
		pos.addLocal(vel);
		if (pos.z > minimumHeight){
			_cam.setLocation(pos);
		}
	}
	
	/**
	 * Move the camera using mouse moves
	 */
	private void moveCameraByGrab() {
		Vector3f currentMousePosition = mouseOnGroundCoords();
		currentMousePosition.subtractLocal(_previousMousePosition);
		Vector3f pos = _cam.getLocation().clone();
		pos.subtractLocal(currentMousePosition);
		_cam.setLocation(pos);
		_previousMousePosition = mouseOnGroundCoords();
	}
	
	/**
	 * This function is used to control the camera using the mouse with a click and grab 
	 * @param value Is positive if it's a forward move
	 * @param axis Is the direction where the mouse moves
	 */
	private void clickAndGrab(float value,Vector3f axis) {
		if (_canMove) {
			moveCameraByGrab();
		} else if (_canRotate) { 
			rotateCameraByGrab(value,axis);
		}
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
		
		_inputManager.addMapping(_ZOOMIN, 		new MouseAxisTrigger(2, false));
        _inputManager.addMapping(_ZOOMOUT, 		new MouseAxisTrigger(2, true));

		// Mouse event mapping
		_inputManager.addMapping(_MOVEDRAG, 	new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		_inputManager.addMapping(_UP, 			new MouseAxisTrigger(1, false));
		_inputManager.addMapping(_DOWN, 		new MouseAxisTrigger(1, true));
		_inputManager.addMapping(_LEFT,			new MouseAxisTrigger(0, true));
		_inputManager.addMapping(_RIGHT,		new MouseAxisTrigger(0, false));

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
	 * (non-Javadoc)
	 * @see com.jme3.input.controls.ActionListener#onAction(java.lang.String, boolean, float)
	 */
	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (!_enabled)
			return;
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
		if (!_enabled)
			return;

		if (name.equals(_STRAFERIGHT)) {
			this.moveCamera(-value,false);
		} else if (name.equals(_STRAFELEFT)) {
			this.moveCamera(value,false);
		} else if (name.equals(_FORWARD)) {
			this.moveCamera(value,true);
		} else if (name.equals(_BACKWARD)) {
			this.moveCamera(-value,true);
		} else if (name.equals(_LEFT)) {
			clickAndGrab(-value, _cam.getUp());
		} else if (name.equals(_RIGHT)) {
			clickAndGrab(value, _cam.getUp());
		} else if (name.equals(_UP)) {
			clickAndGrab(value, _cam.getLeft());
		} else if (name.equals(_DOWN)) {
			clickAndGrab(-value, _cam.getLeft());
		} else if (name.equals(_ROTATELEFT)) {
			selfRotate(value);
		} else if (name.equals(_ROTATERIGHT)) {
			selfRotate(-value);
		} else if (name.equals(_ZOOMIN)) {
			zoomCamera(value);
		} else if (name.equals(_ZOOMOUT)) {
			zoomCamera(-value);
		}

	}

}

