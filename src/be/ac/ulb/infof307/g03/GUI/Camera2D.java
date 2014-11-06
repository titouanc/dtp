package be.ac.ulb.infof307.g03.GUI;

import java.util.Vector;

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
import com.jme3.scene.Geometry;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian, brochape, wmoulart
 */
public class Camera2D implements AnalogListener, ActionListener {
	
	private Camera _cam;
	private float _rotationSpeed = 3f;
    private float _moveSpeed = 3f;
    private boolean _canRotate = false;
    private boolean _enabled = true;
    private InputManager _inputManager;

	/**
	 * Constructor of the 2D camera
	 * Needs the main camera
	 * Needs an inputManager so keys can be bound 
	 */
    public Camera2D() {
	}
    
	/**
	 * @return true if the 2D is currently used
	 */
	public boolean isEnabled() {
		return _enabled;
	}
	
	/**
	 * Method used to declare that the 2D camera is being used (or not)
	 */
	public void setEnabled(boolean enable) {
		_enabled = enable;
	}
	
	public void setCam(Camera cam) {
		_cam = cam;
	}
	
	public void setInputManager(InputManager inputManager) {
		_inputManager = inputManager;
		inputSetUp();
	}

	/**
	 * Reset the direction toward witch the camera looks
	 */
	public void resetDirection() {
		Quaternion q = new Quaternion();
        q.fromAxes(_cam.getLeft(), _cam.getUp(), new Vector3f(0,0,-1));
        q.normalizeLocal();
        _cam.setAxes(q);
	}
	
	/**
	 * Method to use to move the camera
	 * float value : value of movement
	 * boolean sideways : direction (up/down or left/right)
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
	
	public void camHeight(Vector <Geometry> shape){
		  float minX = 0,minY = 0,maxX = 0,maxY = 0,X = 0, Y= 0,Z = 0;
		  int offset=17;
		  Vector3f center;
		  for(int i =0; i< shape.size();++i){
			  center=shape.elementAt(i).getModelBound().getCenter();
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
	  }
	
	/**
	 * Method used to zoom in or out in 2D mode
	 * float value : value of zoom
	 */
	private void zoomCamera(float value){
		Vector3f pos = _cam.getLocation().clone();
		pos.setZ(pos.getZ() + (value*_moveSpeed));
		_cam.setLocation(pos);
    }
	
	/**
	 * Methode used to rotate the camera
	 * float value : the value of the rotation
	 * boolean trigoRotate : direction of the rotation
	 */
	private void rotateCamera(float value, boolean trigoRotate) {
        if (!_canRotate){
            return;
        }
        float cos1deg = 0.99939f;
        float sin1deg = 0.03489f;
        if (trigoRotate) {
        	sin1deg *= -1;
        }
        
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
		
	}

	/**
	 * Method that binds the keys to their actions
	 */
	public void inputSetUp() {

		// Key event mapping
		_inputManager.addMapping("StrafeLeft",		new KeyTrigger(KeyInput.KEY_LEFT));
		_inputManager.addMapping("StrafeRight",		new KeyTrigger(KeyInput.KEY_RIGHT));
		_inputManager.addMapping("Forward",   		new KeyTrigger(KeyInput.KEY_UP));
		_inputManager.addMapping("Backward",		new KeyTrigger(KeyInput.KEY_DOWN));
		
		// Mouse event mapping
		_inputManager.addMapping("RotateDrag", 		new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		//_inputManager.addMapping("Up", 			new MouseAxisTrigger(1, false));
		//_inputManager.addMapping("Down", 			new MouseAxisTrigger(1, true));
		_inputManager.addMapping("Left",			new MouseAxisTrigger(0, true));
		_inputManager.addMapping("Right",			new MouseAxisTrigger(0, false));
		_inputManager.addMapping("ZoomIn", new MouseAxisTrigger(2, false));
        _inputManager.addMapping("ZoomOut", new MouseAxisTrigger(2, true));


		// Add the names to the action listener
		_inputManager.addListener(	this, 
									"StrafeLeft", 
									"StrafeRight", 
									"Forward", 
									"Backward", 
									"RotateDrag", 
								  /*"Up", 
									"Down",*/ 
									"Left", 
									"Right", 
									"ZoomIn", 
									"ZoomOut"
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
        if (name.equals("RotateDrag")){
            _canRotate = value;
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

		if (name.equals("StrafeRight")) {
			this.moveCamera(-value,false);
		} else if (name.equals("StrafeLeft")) {
			this.moveCamera(value,false);
		} else if (name.equals("Forward")) {
			this.moveCamera(value,true);
		} else if (name.equals("Backward")) {
			this.moveCamera(-value,true);
		} else if (name.equals("Left")) {
			rotateCamera(value, false);
		} else if (name.equals("Right")) {
			rotateCamera(-value, true);
		} /*else if (name.equals("Up")) {
            rotateCamera(-value, true);
		} else if (name.equals("Down")) {
            rotateCamera(value, false);
		}*/
		else if (name.equals("ZoomIn")) {
			zoomCamera(value);
		} else if (name.equals("ZoomOut")) {
			zoomCamera(-value);
		}	
	}
}
