package be.ac.ulb.infof307.g03.camera;

import java.util.Vector;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.world.WorldView;

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
	private float rotationSpeed	= 1f;
    private float moveSpeed		= 15f;
    private float zoomSpeed		= 4f;
	
    
    /**
     * Camera 2D controller constructor
     * @param newCam
     */
    public Camera2D(Camera newCam) {
		super(newCam);
	}
    
    @Override
	public void moveCamera(float value, boolean sideways) {
		Vector3f pos = cam.getLocation().clone();
		Vector3f vel = new Vector3f();
		if (sideways) {
			cam.getUp(vel);
		} else { 
			cam.getLeft(vel);
		}
		vel.multLocal(value*this.moveSpeed);
		pos.addLocal(vel);

		cam.setLocation(pos); 
	}
	
	@Override
	public void moveCameraByGrab(Vector3f oldPos, Vector3f currPos) {
		currPos.subtractLocal(oldPos);
		Vector3f pos = cam.getLocation().clone();
		pos.subtractLocal(currPos);
		cam.setLocation(pos);
	}
	
	@Override
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
	      cam.setLocation(new Vector3f(X,Y,Z+offset));
		  cam.lookAt(new Vector3f(X,Y,0),Vector3f.UNIT_Z);
	      cam.setParallelProjection(true);
	     this.frustumSize = Z/offset+offset;
	      float aspect = (float) cam.getWidth() / cam.getHeight();
	      cam.setFrustum(-1000, 1000, -aspect * this.frustumSize, aspect * this.frustumSize, this.frustumSize, -this.frustumSize);
	  }
	
	@Override
	public void zoomCamera(float value){
		if(this.frustumSize + 0.3f * value >= this.minimumHeight){
			
	        this.frustumSize += 0.3f * value;
	
	        float aspect = (float) cam.getWidth() / cam.getHeight();
	        cam.setFrustum(-1000, 1000, -aspect * this.frustumSize, aspect * this.frustumSize, this.frustumSize, -this.frustumSize);
	
			Vector3f pos = cam.getLocation().clone();
			pos.setZ(pos.getZ() + (value*this.zoomSpeed));
			cam.setLocation(pos);
		}
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
	public void rotateCameraByGrab(float value, Vector3f axis) {
		// TODO Auto-generated method stub
	}

}

