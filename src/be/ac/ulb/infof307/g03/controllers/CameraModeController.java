package be.ac.ulb.infof307.g03.controllers;


import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

/**
 * @author julianschembri
 * 
 */
public class CameraModeController implements Observer {
	
	/**
	 * This constant is the 3D mode alias.
	 */
	public static final String _VIEW3D = "3D";
	/**
	 * This constant is the 2D mode alias.
	 */
	public static final String _VIEW2D = "2D";
	/**
	 * This variable contents the current mode.
	 */
	private String _currentMode;
	/**
	 * This variable contents the 2D camera controller.
	 */
	private Camera2D _cam2D = new Camera2D();
	/**
	 * This variable contents the 3D camera controller.
	 */
	private Camera3D _cam3D = new Camera3D();
	
	/**
	 * Constructor of the controller CameraModeController. It enable the 2D camera by default.
	 * @param proj
	 */
	CameraModeController(Project proj){
		_currentMode = proj.config("world.mode");
		if (proj.config("world.mode").equals("")) 
			_currentMode = _VIEW2D;
		
		proj.addObserver(this);
	}
	
	/**
	 * This method is used to change the actual mode.
	 */
	public void updateMode() {
		Log.info("Change view mode to %s", _currentMode);
		if (_currentMode.equals(_VIEW3D)){
			_cam2D.setEnabled(false);
			_cam3D.setEnabled(true);
			_cam3D.resetCamera();
		} else if (_currentMode.equals(_VIEW2D)) {
			_cam2D.setEnabled(true);
			_cam3D.setEnabled(false);
			_cam2D.resetCamera();
		}
	}
	
	/**
	 * This method is used to change the actual mode.
	 */
	private void updateMode(String value) {
		if (_currentMode!=value) {
			_currentMode = value;
			updateMode();
		}
	}

	
	/**
	 * Set the camera in the two camera controllers
	 * @param cam An instance of Camera class.
	 */
	public void setCamera(Camera cam) {
		_cam2D.setCam(cam);
		_cam3D.setCam(cam);
	}
	
	/**
	 * Getter
	 * @return The 2D camera controller.
	 */
	public Camera2D get2DCam(){
		return _cam2D;
	}
	
	/**
	 * Set the inputManager in the two camera controllers.
	 * @param inputManager
	 */
	public void setInputManager(InputManager inputManager) {
		_cam2D.setInputManager(inputManager);
		_cam3D.setInputManager(inputManager);
	}

	/**
	 * 
	 */
	public void update(Observable obs, Object arg) {
		if (obs instanceof Project){
			Config param = (Config) arg;
			if (param.getName().equals("world.mode")) {
				updateMode(param.getValue());
			} else if (param.getName().equals("mouse.mode")){
				Log.info("Change mouse mode to %s", param.getValue());
				_cam2D.setMouseMode(param.getValue());
				_cam3D.setMouseMode(param.getValue());
			}
		}
	}

	/**
	 * Set the world view 
	 * @param wv
	 */
	public void setWorldView(WorldView wv) {
		_cam2D.setWv(wv);
	}
	
}
