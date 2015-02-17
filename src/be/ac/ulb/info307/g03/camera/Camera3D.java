package be.ac.ulb.info307.g03.camera;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.world.WorldView;

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
	private float rotationSpeed 	= 1f;
	private float moveSpeed 		= 10f;
	private float zoomSpeed		= 4f;
	
	// Default camera parameter
	private final int defaultCameraZ = 40;
	
	/**
	 * Camera 3D controller constructor
	 * @param newCam
	 */
	public Camera3D(Camera newCam) {
		super(newCam);
	}
	
	@Override
	public void resetCamera(WorldView wv) {
		Log.info("Reset 3D camera direction");
        cam.setParallelProjection(false);
        
        cam.setLocation(new Vector3f(cam.getLocation().x,cam.getLocation().y, this.defaultCameraZ));
        cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 1f, 1000f);
	}

	@Override
	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		
		// Choose the vector to follow
		if (sideways) { // forward or backward
			if (FastMath.abs(cam.getDirection().getZ())!=1) {
				cam.getDirection(vel);
			} else {
				cam.getUp(vel);
			}
		} else { // strafe left or right
			cam.getLeft(vel);
		}
		
		// Make this vector parallel to the Oxy plan
		vel.setZ(0);
		vel.normalizeLocal();
		
		// Find the new position
		vel.multLocal(value*this.moveSpeed);
		pos.addLocal(vel);

		// Move the camera
		cam.setLocation(pos);
	}
	
	@Override
	public void rotateCameraByGrab(float value, Vector3f axis) {
		Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(this.rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        cam.setAxes(q);
	}
	
	@Override
	public void rotateCamera(float value) {
		Matrix3f mat = new Matrix3f();
		mat.fromAngleNormalAxis(this.rotationSpeed * value, cam.getDirection());

		Vector3f up = cam.getUp();
		Vector3f left = cam.getLeft();
		Vector3f dir = cam.getDirection();

		mat.mult(up, up);
		mat.mult(left, left);
		mat.mult(dir, dir);

		Quaternion q = new Quaternion();
		q.fromAxes(left, up, dir);
		q.normalizeLocal();

		cam.setAxes(q);
	}
	
	@Override
	public void zoomCamera(float value) {
		Vector3f pos = cam.getLocation().clone();
		Vector3f vel = cam.getDirection().clone();
		vel.multLocal(-value*this.zoomSpeed);
		pos.addLocal(vel);
		if (pos.z > this.minimumHeight){
			cam.setLocation(pos);
		}
	}
	
	@Override
	public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos) {
		currPos.subtractLocal(oldPos);
		Vector3f pos = cam.getLocation().clone();
		pos.subtractLocal(currPos);
		cam.setLocation(pos);
	}

}

