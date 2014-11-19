package be.ac.ulb.infof307.g03.controllers;

import java.util.Vector;

import be.ac.ulb.infof307.g03.views.WorldView;

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
import com.jme3.scene.Geometry;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian, brochape, wmoulart
 */
public class Camera2D implements AnalogListener, ActionListener {
	
	// Attributes
    private Camera _cam;
    private InputManager _inputManager;
    private String _mouseMode = _MODE_DRAGSELECT;
    private Vector3f _previousMousePosition;
    private WorldView _wv;
    private float frustumSize = 10;
    private final int minimumHeight = 1;
    
    // Speed parameters
	private float _rotationSpeed	= 1f;
    private float _moveSpeed		= 15f;
    private float _zoomSpeed		= 4f;
    
    // Flags
    private boolean _canMove	= false;
    private boolean _canRotate	= false;
    private boolean _enabled	= true;
    
    // Carera mode alias
	static private final String _MODE_DRAGROTATE = "dragRotate";
    static private final String _MODE_DRAGSELECT = "dragSelect";
    static private final String _MODE_DRAGMOVE = "dragMove";
    
    // Input alias
    static private final String _STRAFELEFT 	= "CAM2D_StrafeLeft";
	static private final String _STRAFERIGHT	= "CAM2D_StrafeRight"; 
	static private final String _FORWARD 		= "CAM2D_Forward";
	static private final String _BACKWARD		= "CAM2D_Backward"; 
	static private final String _MOVEDRAG		= "CAM2D_MoveDrag";
	
	static private final String _LEFT 			= "CAM2D_Left";
	static private final String _RIGHT			= "CAM2D_Right";
	static private final String _UP				= "CAM2D_Up";
	static private final String _DOWN			= "CAM2D_Down";

	static private final String _ZOOMIN			= "CAM2D_ZoomIn";
	static private final String _ZOOMOUT		= "CAM2D_ZoomOut";

	static private final String _ROTATELEFT 	= "CAM2D_RotateLeft";
	static private final String _ROTATERIGHT	= "CAM2D_RotateRight";
    
	/**
	 * Constructor of the 2D camera
	 */
    public Camera2D() {
	}
    
	/**
	 * @return true if the 2D camera controller is currently used
	 */
	public boolean isEnabled() {
		return _enabled;
	}
	
	/**
	 * Method used to declare that the 2D camera is being used (or not)
	 * @param enable New boolean saying if camera2D is enabled.
	 */
	public void setEnabled(boolean enable) {
		_enabled = enable;
	}
	
	/**
	 * @param cam The new Camera to be set.
	 */
	public void setCam(Camera cam) {
		_cam = cam;
	}
	
	/**
	 * This method set the input. Additionally it call the
	 * set up input method
	 * @param inputManager The new input manager to be set.
	 */
	public void setInputManager(InputManager inputManager) {
		_inputManager = inputManager;
		inputSetUp();
	}
	
	/**
	 * @param mouseMode The new mouse mode to be set.
	 */
	public void setMouseMode(String mouseMode) {
		_mouseMode = mouseMode;
	}
	
	/**
	 * @param wv The new world view to be set.
	 */
	public void setWv(WorldView wv) {
		_wv = wv;
	}
	
	/**
	 * Method to use to move the camera
	 * @param value value of movement
	 * @param sideways direction (up/down or left/right)
	 */
	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		if (sideways) {
			_cam.getUp(vel);
		} else { 
			_cam.getLeft(vel);
		}
		vel.multLocal(value*_moveSpeed);
		pos.addLocal(vel);

		_cam.setLocation(pos); 
	}
	
	/**
	 * Manage the camera control using a click and grab with the mouse
	 */
	private void clickAndGrab() {
		if (_canMove) {
			moveCameraByGrab();
		}
		/*
		if (_canRotate) { // TODO
			System.out.println("Use 'O' and 'P' button to rotate.");
		}
		*/
	}
	
	private void moveCameraByGrab() {
		Vector3f currentMousePosition = mouseOnGroundCoords();
		currentMousePosition.subtractLocal(_previousMousePosition);
		Vector3f pos = _cam.getLocation().clone();
		pos.subtractLocal(currentMousePosition);
		_cam.setLocation(pos);
		_previousMousePosition = mouseOnGroundCoords();
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
	 * Place the shapes at the center
	 * of the user's screen
	 */
	public void resetCamera(){
		System.out.println("[Camera2D] reset 2D");
		Vector<Geometry> shapes = _wv.getShapes();
		  float minX = 0,minY = 0,maxX = 0,maxY = 0,X = 0, Y= 0,Z = 0;
		  int offset=17;
		  Vector3f center;
		  for(int i =0; i< shapes.size();++i){
			  center=shapes.elementAt(i).getModelBound().getCenter();
			  if (center.x<minX){
				  minX=center.x;
			  }
			  if (center.y<minY){
				  minY=center.y;
			  }
			  if (center.x>maxX){
				  maxX=center.x;
			  }
			  if (center.y>maxY){
				  maxY=center.y;
			  }
			  Z+=center.z;
		  }
		  X=(minX+maxX)/2;
		  Y=(minY+maxY)/2;
	      _cam.setLocation(new Vector3f(X,Y,Z+offset));
		  _cam.lookAt(new Vector3f(X,Y,0),Vector3f.UNIT_Z);
	      _cam.setParallelProjection(true);
	      frustumSize = Z/offset+offset;
	      float aspect = (float) _cam.getWidth() / _cam.getHeight();
	      _cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
	  }
	
	/**
	 * Method used to zoom in or out in 2D mode
	 * float value : value of zoom
	 */
	private void zoomCamera(float value){
		if(frustumSize + 0.3f * value >= minimumHeight){
			
	        frustumSize += 0.3f * value;
	
	        float aspect = (float) _cam.getWidth() / _cam.getHeight();
	        _cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
	
			Vector3f pos = _cam.getLocation().clone();
			pos.setZ(pos.getZ() + (value*_moveSpeed));
			_cam.setLocation(pos);
		}
    }
	
	/**
	 * Methode used to rotate the camera
	 * float value : the value of the rotation
	 * boolean trigoRotate : direction of the rotation
	 */
	private void rotateCamera(float value, boolean trigoRotate) {
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
	 * Method that binds the keys to their actions
	 */
	public void inputSetUp() {

		// Key event mapping
		_inputManager.addMapping(_STRAFELEFT,	new KeyTrigger(KeyInput.KEY_LEFT));
		_inputManager.addMapping(_STRAFERIGHT,	new KeyTrigger(KeyInput.KEY_RIGHT));
		_inputManager.addMapping(_FORWARD,   	new KeyTrigger(KeyInput.KEY_UP));
		_inputManager.addMapping(_BACKWARD,		new KeyTrigger(KeyInput.KEY_DOWN));
		
		_inputManager.addMapping(_ROTATELEFT,	new KeyTrigger(KeyInput.KEY_O)); 
		_inputManager.addMapping(_ROTATERIGHT,	new KeyTrigger(KeyInput.KEY_P));
		
		_inputManager.addMapping(_ZOOMIN,		new KeyTrigger(KeyInput.KEY_ADD));
		_inputManager.addMapping(_ZOOMOUT,		new KeyTrigger(KeyInput.KEY_SUBTRACT));
		
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
		} else if(name.equals(_UP)){
			this.clickAndGrab();
		} else if(name.equals(_DOWN)){
			this.clickAndGrab();
		} else if(name.equals(_LEFT)){
			this.clickAndGrab();
		} else if(name.equals(_RIGHT)){
			this.clickAndGrab();
		} else if (name.equals(_ROTATELEFT)) {
			rotateCamera(value, false);
		} else if (name.equals(_ROTATERIGHT)) {
			rotateCamera(-value, true);
		} else if (name.equals(_ZOOMIN)) {
			zoomCamera(-value);
		} else if (name.equals(_ZOOMOUT)) {
			zoomCamera(value);
		} 
	}
}

