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

	public Camera3D() {
	}

	public boolean isEnabled() {
		return _enabled;
	}

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
	
	public void resetDirection() {
		Quaternion q = new Quaternion();
        q.fromAxes(_cam.getLeft(),new Vector3f(0f,0f,1f), _cam.getUp());
        q.normalizeLocal();
        _cam.setAxes(q);
	}

	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		if (sideways) {
			_cam.getDirection(vel);
		} else { 
			_cam.getLeft(vel);
		}
		//vel.setZ(0);
		//vel.normalizeLocal();
		vel.multLocal(value*_moveSpeed);
		pos.addLocal(vel);

		_cam.setLocation(pos);
	}

	public void camHeight(Vector <Geometry> shape){
		 
	}

	private void zoomCamera(float value){
		Vector3f pos = _cam.getLocation().clone();
		Vector3f dir = _cam.getDirection().clone(); 
		dir.multLocal(value*_moveSpeed);
		pos.addLocal(dir);
		_cam.setLocation(pos);
	}
	
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
	
	public void inputSetUp() {

		// Key event mapping
		_inputManager.addMapping("CAM3D_StrafeLeft",	new KeyTrigger(KeyInput.KEY_LEFT));
		_inputManager.addMapping("CAM3D_StrafeRight",	new KeyTrigger(KeyInput.KEY_RIGHT));
		_inputManager.addMapping("CAM3D_Forward",   	new KeyTrigger(KeyInput.KEY_UP));
		_inputManager.addMapping("CAM3D_Backward",		new KeyTrigger(KeyInput.KEY_DOWN));
		_inputManager.addMapping("CAM3D_Loop", 			new KeyTrigger(KeyInput.KEY_L));

		// Mouse event mapping
		_inputManager.addMapping("CAM3D_RotateDrag", 	new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		_inputManager.addMapping("CAM3D_Up", 			new MouseAxisTrigger(1, false));
		_inputManager.addMapping("CAM3D_Down", 			new MouseAxisTrigger(1, true));
		_inputManager.addMapping("CAM3D_Left",			new MouseAxisTrigger(0, true));
		_inputManager.addMapping("CAM3D_Right",			new MouseAxisTrigger(0, false));
		_inputManager.addMapping("CAM3D_ZoomIn", 		new MouseAxisTrigger(2, false));
		_inputManager.addMapping("CAM3D_ZoomOut", 		new MouseAxisTrigger(2, true));


		// Add the names to the action listener
		_inputManager.addListener(	this, 
				"CAM3D_StrafeLeft", 
				"CAM3D_StrafeRight", 
				"CAM3D_Forward", 
				"CAM3D_Backward", 
				"CAM3D_RotateDrag", 
				"CAM3D_Up", 
				"CAM3D_Down",
				"CAM3D_Left", 
				"CAM3D_Right", 
				"CAM3D_ZoomIn", 
				"CAM3D_ZoomOut",
				"CAM3D_Loop"
				);
	}
	
	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (!_enabled)
			return;
		if (name.equals("CAM3D_RotateDrag")){
			_canRotate = value;
		} else if (name.equals("CAM3D_Loop")) {
			_loop = value;
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (!_enabled)
			return;

		if (name.equals("CAM3D_StrafeRight")) {
			this.moveCamera(-value,false);
		} else if (name.equals("CAM3D_StrafeLeft")) {
			this.moveCamera(value,false);
		} else if (name.equals("CAM3D_Forward")) {
			this.moveCamera(value,true);
		} else if (name.equals("CAM3D_Backward")) {
			this.moveCamera(-value,true);
		} else if (name.equals("CAM3D_Left")) {
			rotateCamera(-value, _cam.getUp());
		} else if (name.equals("CAM3D_Right")) {
			rotateCamera(value, _cam.getUp());
		} else if (name.equals("CAM3D_Up")) {
			rotateCamera(value, _cam.getLeft());
		} else if (name.equals("CAM3D_Down")) {
			rotateCamera(-value, _cam.getLeft());
		} else if (name.equals("CAM3D_ZoomIn")) {
			zoomCamera(value);
		} else if (name.equals("CAM3D_ZoomOut")) {
			zoomCamera(-value);
		}

	}

}

