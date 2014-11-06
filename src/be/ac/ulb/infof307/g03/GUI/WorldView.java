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

	/**
	 * Constructor of WorldView
	 * @param newController The view's controller
	 */
	WorldView(WorldController newController){
		super();
		_controller = newController;
		this.setDisplayStatView(false);
	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		_controller.getCameraModeController().setCamera(cam);
		_controller.getCameraModeController().setInputManager(inputManager);
	}

}
