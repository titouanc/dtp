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
	
	private WorldController _controller; 
	private boolean _freeCam = false;
	private Camera2D _cam2D;

	/*
	 * Constructor of WorldView
	 * @param The view's controller
	 */
	WorldView(WorldController newController){
		super();
		_controller = newController;
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
