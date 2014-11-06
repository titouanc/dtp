/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.app.SimpleApplication;


/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape
 */
public class WorldView extends SimpleApplication {	
	
	WorldController controller; 
	
	private boolean _freeCam = false;
	Camera2D _cam2D;

	WorldView(WorldController c){
		super();
		controller = c;
	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		_cam2D = new Camera2D(cam, this.getInputManager());
		_cam2D.setEnabled(true);	
	}

}
