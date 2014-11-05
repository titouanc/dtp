package be.ac.ulb.infof307.g03.GUI;

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

/*
 * Camera2D is the controller of the camera when the view is switched on 2D
 */
public class Camera2D implements AnalogListener, ActionListener {
	
	private Camera _cam;
	private float _rotationSpeed = 1f;
    private float _moveSpeed = 3f;
    private boolean _canRotate = false;
    private boolean _enabled = true;
    private InputManager _inputManager;

    public Camera2D(Camera cam, InputManager inputManager) {
    	_inputManager = inputManager;
		_cam = cam;
		inputSetUp();
	}
	
	public boolean isEnabled() {
		return _enabled;
	}
	
	public void setEnabled(boolean enable) {
		_enabled = enable;
	}
	
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
	
	private void zoomCamera(float value){
		Vector3f pos = _cam.getLocation().clone();
		pos.setZ(pos.getZ() + (value*_moveSpeed));
		_cam.setLocation(pos);
    }

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
	
	public void rotateDrag(float value, Vector3f axis) {
        if (!_canRotate){
            return;
        }

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

	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (!_enabled)
            return;
        if (name.equals("RotateDrag")){
            _canRotate = value;
        }	
	}

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
}
