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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian
 */
public class Camera3D implements AnalogListener, ActionListener {

	private Camera _cam;
	private float _rotationSpeed = 1f;
	private float _moveSpeed = 3f;
	private boolean _canRotate = false;
	private boolean _loop = false;
	private boolean _enabled = true;
	private InputManager _inputManager;
	private String _mouseMode;
	
    static private final String _MODE_DRAGROTATE = "dragRotate";
    static private final String _MODE_DRAGSELECT = "dragSelect";
    static private final String _MODE_DRAGMOVE = "dragMove";
	
	static private final String _STRAFELEFT 	= "CAM3D_StrafeLeft";
	static private final String _STRAFERIGHT	= "CAM3D_StrafeRight";
	static private final String _FORWARD		= "CAM3D_Forward";
	static private final String _BACKWARD		= "CAM3D_Backward";
	static private final String _ROTATEDRAG		= "CAM3D_RotateDrag";
	static private final String _UP				= "CAM3D_Up";
	static private final String _DOWN			= "CAM3D_Down";
	static private final String _LEFT			= "CAM3D_Left";
	static private final String _RIGHT			= "CAM3D_Right";
	
	// !!! <temporary> !!!
	static private final String _LOOP			= "CAM3D_Loop";
	// !!! </temporary> !!!

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
		_inputManager = inputManager;
		inputSetUp();
	}
	
	/**
	 * Reset the direction toward which the camera looks
	 */
	public void resetDirection() {
		Quaternion q = new Quaternion();
        q.fromAxes(_cam.getLeft(),new Vector3f(0f,0f,1f), _cam.getUp());
        q.normalizeLocal();
        _cam.setAxes(q);
	}

	/**
	 * Method to use to move the camera
	 * @param value Used to know if it's a forward or backward move
	 * @param sideways True if up or down, false if left or right
	 */
	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		if (sideways) {
			_cam.getDirection(vel);
		} else { 
			_cam.getLeft(vel);
		}
		vel.multLocal(value*_moveSpeed);
		pos.addLocal(vel);

		_cam.setLocation(pos);
	}
	
	/**
	 * Rotate the camera like a man can move his head
	 * @param value
	 * @param axis
	 */
	private void rotateCamera(float value, Vector3f axis) {
		if (!_canRotate) 
			return;
		
		if (_loop) {
			float cos1deg = 0.99939f;
	        float sin1deg = 0.03489f;
	        //if (trigoRotate) {
	        //	sin1deg *= -1;
	        //}
	        
	        Matrix3f mat = new Matrix3f();
	        mat.fromAngleNormalAxis(_rotationSpeed * value, _cam.getUp());

	        Vector3f up = _cam.getUp();
	        Vector3f left = _cam.getLeft();
	        Vector3f dir = _cam.getDirection();

	        Vector3f nup = new Vector3f( (cos1deg*up.getX())+(sin1deg*up.getY()), (-sin1deg*up.getX())+(cos1deg*up.getY()), up.getZ() );
	        Vector3f nleft = new Vector3f( (cos1deg*left.getX())+(sin1deg*left.getY()), (-sin1deg*left.getX())+(cos1deg*left.getY()), left.getZ());

	        Quaternion q = new Quaternion();
	        q.fromAxes(nleft, nup, dir);
	        q.normalizeLocal();

	        _cam.setAxes(q);
		} else {
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
		_inputManager.addMapping(_LOOP, 		new KeyTrigger(KeyInput.KEY_L));

		// Mouse event mapping
		_inputManager.addMapping(_ROTATEDRAG, 	new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
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
				_ROTATEDRAG, 
				_UP, 
				_DOWN,
				_LEFT, 
				_RIGHT, 
				_LOOP
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
		if (name.equals(_ROTATEDRAG)){
			_canRotate = value;
		} else if (name.equals(_LOOP)) {
			_loop = value;
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
			rotateCamera(-value, _cam.getUp());
		} else if (name.equals(_RIGHT)) {
			rotateCamera(value, _cam.getUp());
		} else if (name.equals(_UP)) {
			rotateCamera(value, _cam.getLeft());
		} else if (name.equals(_DOWN)) {
			rotateCamera(-value, _cam.getLeft());
		}

	}

}

