package be.ac.ulb.infof307.g03.camera;

import be.ac.ulb.infof307.g03.world.WorldView;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * @author julianschembri
 *
 */
public abstract class CameraController {
	protected Camera cam;
	
	/**
	 * Constructor of the cameraController class
	 * @param newCam The cam that will be controlled
	 */
	public CameraController(Camera newCam) {
		this.cam = newCam;
	}
	
	/**
	 * Zoom in the project
	 * @param value The value of the zoom, negative or positive
	 */
	abstract public void zoomCamera(float value);
	
	/**
	 * Rotate the camera around the view axis
	 * @param value The value of the rotation
	 */
	abstract public void rotateCamera(float value);
	
	/**
	 *  Reset the direction toward which the camera looks
	 * @param wv The new camera to be set
	 */
	abstract public void resetCamera(WorldView wv);
	
	/**
	 * Move the camera with the grab tool
	 * @param oldPos The old position
	 * @param currPos The new position
	 */
	abstract public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos);
	
	/**
	 * Rotate the camera like a man can move his head using Quaternion
	 * @param value The value of the rotation
	 * @param axis The axis which the camera turn around
	 */
	abstract public void rotateCameraByGrab(float value, Vector3f axis);
	
	/**
	 * Move camera, right or left, or forward backward
	 * @param value The value of the move
	 * @param forward Is going forward
	 */
	abstract public void moveCamera(float value,boolean forward);
}
