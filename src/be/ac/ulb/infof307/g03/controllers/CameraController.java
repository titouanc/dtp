package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * @author julianschembri
 *
 */
public abstract class CameraController {
	protected Camera cam;
	
	/**
	 * @param newCam New cam to be set.
	 */
	public CameraController(Camera newCam) {
		this.cam = newCam;
	}
	
	abstract public void zoomCamera(float value);
	abstract public void rotateCamera(float value, boolean trigoRotate);
	abstract public void resetCamera(WorldView wv);
	abstract public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos);
	abstract public void rotateCameraByGrab(float value, Vector3f axis);
	abstract public void moveCamera(float value,boolean forward);
}
