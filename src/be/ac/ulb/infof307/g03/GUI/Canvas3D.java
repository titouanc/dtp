package be.ac.ulb.infof307.g03.GUI;
 
import java.util.Vector;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.math.ColorRGBA;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
 
public class Canvas3D extends SimpleApplication {
 
  protected Geometry meshes;
  protected Geometry ground;
  protected Vector<Geometry> shapes = new Vector<Geometry>();

  Boolean isRunning=true;
  Boolean is3D=true;
  
 
  @Override
  public void simpleInitApp() {
	  
  	flyCam.setDragToRotate(true);
  	Vector3f [] vertices = new Vector3f[8];
  	vertices[0] = new Vector3f(0,0,0);
  	vertices[1] = new Vector3f(-1,1,0);
  	vertices[2] = new Vector3f(0,6,0);
  	vertices[3] = new Vector3f(3,5,0);
  	vertices[4] = new Vector3f(0,0,2);
  	vertices[5] = new Vector3f(-1,1,2);
  	vertices[6] = new Vector3f(0,6,2);
  	vertices[7] = new Vector3f(3,5,2);
  	

  	int n = 4;
  	int[] order = new int[(n-1)*2*3];//4 points -> 3 walls; each wall -> 2 triangles; each triangle ->3 points
  	int i;
  	for(i=0;i<(n-1);++i){
  		order[6*i] = i;
  		order[6*i+1] = i+1;
  		order[6*i+2] = i+n;
  		order[6*i+3] = i+1;
  		order[6*i+4] = i+n+1;
  		order[6*i+5] = i+n;
  	}
  	
  	Mesh mesh = new Mesh();
  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
  	mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(order));
  	mesh.updateBound();
  	
  	int[] groundOrder = new int[(n-2)*3];
  	
  	for(int j = 0; j< n-2;++j){
  		groundOrder[3*j] = 0;
  		groundOrder[3*j+1] = j+2;
  		groundOrder[3*j+2] = j+1;	
  	}
  	
  	Mesh groundMesh = new Mesh();
  	groundMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
  	groundMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(groundOrder));
  	
    meshes = new Geometry("Mesh",mesh);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
    mat.setColor("Color", ColorRGBA.Blue);
    meshes.setMaterial(mat);
    shapes.add(meshes);
    rootNode.attachChild(meshes);
    
    ground = new Geometry("Groundmesh",groundMesh);
    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
    mat2.setColor("Color", ColorRGBA.Red);
    ground.setMaterial(mat2);
    shapes.add(ground);
    rootNode.attachChild(ground);
    
    
    
    
    initKeys(); // load my custom keybinding
    camHeight(shapes);
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
	  cam.setLocation(new Vector3f(X,Y,Z+offset));	  
  }
 
  /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    // You can map one or several inputs to one named action
    inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("New",    new KeyTrigger(KeyInput.KEY_F));
    inputManager.addMapping("2D",     new KeyTrigger(KeyInput.KEY_M));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
    inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_N));
    inputManager.addMapping("3D",	  new KeyTrigger(KeyInput.KEY_B));
                                      //new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    // Add the names to the action listener.
    inputManager.addListener(actionListener,"Pause","2D","3D");
    inputManager.addListener(analogListener,"Left", "Right","Rotate", "New");
 
  }
 
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Pause") && keyPressed) {
        isRunning = !isRunning;
      }
      if(name.equals("2D") && keyPressed){
    	  is3D = false;
      	  flyCam.setEnabled(true);
    	  Quaternion pitch90 = new Quaternion();
    	  pitch90.fromAngleAxis(FastMath.PI/2, new Vector3f(0,0,1));
    	  for(int i =0; i< shapes.size();++i){
    		  shapes.elementAt(i).setLocalRotation(pitch90);
    	  }

      }
      if (name.equals("3D") && keyPressed){
    	  is3D=true;
      }
    }
  };
 
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
      if (isRunning) {
        if (name.equals("Rotate")) {
        	if(is3D){
          	  for(int i =0; i< shapes.size();++i){
          		shapes.elementAt(i).rotate(0, value*speed, 0);
        	  }
        	}
        }
        if (name.equals("Right")) {
          Vector3f v = meshes.getLocalTranslation();
      	  for(int i =0; i< shapes.size();++i){
          	shapes.elementAt(i).setLocalTranslation(v.x + value*speed, v.y, v.z);
    	  }
        }
        if (name.equals("Left")) {
          Vector3f v = meshes.getLocalTranslation();
      	  for(int i =0; i< shapes.size();++i){
    		  shapes.elementAt(i).setLocalTranslation(v.x - value*speed, v.y, v.z);
    	  }
        }
        if(name.equals("New")){
        	rootNode.detachChild(meshes);
        }
      } else {
        System.out.println("Press P to unpause.");
      }
    }
  };
}

