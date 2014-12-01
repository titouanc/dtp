package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian, brochape
 */
public class Camera3D extends CameraController {
    private final int minimumHeight = 1;
	
	// Speed parameters
	private float _rotationSpeed 	= 1f;
	private float _moveSpeed 		= 10f;
	private float _zoomSpeed		= 4f;
	
	// Default camera parameter
	private final int _defaultCameraZ = 40;
	
	/**
	 * Camera 3D controller constructor
	 * @param cam
	 */
	public Camera3D(Camera cam) {
		super(cam);
	}
	
	/**
	 * Reset the direction toward which the camera looks
	 */
	public void resetCamera(WorldView wv) {
		Log.info("Reset 3D camera direction");
        _cam.setParallelProjection(false);
        
        _cam.setLocation(new Vector3f(_cam.getLocation().x,_cam.getLocation().y,_defaultCameraZ));
        _cam.setFrustumPerspective(45f, (float)_cam.getWidth() / _cam.getHeight(), 1f, 1000f);
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
			if (FastMath.abs(_cam.getDirection().getZ())!=1) {
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
	public void rotateCameraByGrab(float value, Vector3f axis) {
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
	public void rotateCamera(float value, boolean trigoRotate) {
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
	 * Move the camera where it looks
	 * @param value
	 */
	public void zoomCamera(float value) {
		Vector3f pos = _cam.getLocation().clone();
		Vector3f vel = _cam.getDirection().clone();
		vel.multLocal(-value*_zoomSpeed);
		pos.addLocal(vel);
		if (pos.z > minimumHeight){
			_cam.setLocation(pos);
		}
	}
	
	/**
	 * Move the camera using mouse moves
	 */
	public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos) {
		currPos.subtractLocal(oldPos);
		Vector3f pos = _cam.getLocation().clone();
		pos.subtractLocal(currPos);
		_cam.setLocation(pos);
	}

}

