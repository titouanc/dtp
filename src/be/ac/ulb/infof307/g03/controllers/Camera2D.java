package be.ac.ulb.infof307.g03.controllers;

import java.util.Vector;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;

/**
 * Camera2D is the controller of the camera when the view is switched on 2D
 * @author schembrijulian, brochape, wmoulart
 */
public class Camera2D extends CameraController {
	
	private float frustumSize = 10;
    private final int minimumHeight = 1;
    
    // Speed parameters
	private float _rotationSpeed	= 1f;
    private float _moveSpeed		= 15f;
    private float _zoomSpeed		= 4f;
	
    
    /**
     * Camera 2D controller constructor
     * @param cam
     */
    public Camera2D(Camera cam) {
		super(cam);
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
	
	public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos) {
		currPos.subtractLocal(oldPos);
		Vector3f pos = _cam.getLocation().clone();
		pos.subtractLocal(currPos);
		_cam.setLocation(pos);
	}
	
	/**
	 * Place the shapes at the center
	 * of the user's screen
	 */
	public void resetCamera(WorldView wv){
		Log.debug("Camera2D reset to 2D");
		Vector<Geometry> shapes = wv.getShapes();
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
	public void zoomCamera(float value){
		if(frustumSize + 0.3f * value >= minimumHeight){
			
	        frustumSize += 0.3f * value;
	
	        float aspect = (float) _cam.getWidth() / _cam.getHeight();
	        _cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
	
			Vector3f pos = _cam.getLocation().clone();
			pos.setZ(pos.getZ() + (value*_zoomSpeed));
			_cam.setLocation(pos);
		}
    }
	
	/**
	 * Methode used to rotate the camera
	 * float value : the value of the rotation
	 * boolean trigoRotate : direction of the rotation
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

	@Override
	public void rotateCameraByGrab(float value, Vector3f axis) {
		// TODO Auto-generated method stub
		
	}

}

