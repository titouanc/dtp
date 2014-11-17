package be.ac.ulb.infof307.g03.controllers;


import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;
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
	private String _currentMode = _VIEW2D;
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
		_cam2D.setEnabled(true);
		_cam3D.setEnabled(false);
		proj.addObserver(this);
	}
	
	/**
	 * This method is used to change the actual mode.
	 * @param mode A valid mode as _VIEW3D of _VIEW2D.
	 */
	public void changeMode(String mode){
		System.out.println("[CameraController] Change view mode to " + mode);
		if (mode != _currentMode){
			if (mode.equals(_VIEW3D)){
				_cam2D.setEnabled(false);
				_cam3D.setEnabled(true);
				_cam3D.resetCamera();
			} else if (mode.equals(_VIEW2D)) {
				_cam2D.setEnabled(true);
				_cam3D.setEnabled(false);
				_cam2D.resetCamera();
			}
			_currentMode = mode;
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
	public void update(Observable o, Object arg) {
		if (arg instanceof Config){
			Config param = (Config) arg;
			if (param.getName().equals("world.mode"))
				changeMode(param.getValue());
			else if (param.getName().equals("mouse.mode")){
				System.out.println("[CameraController] Change mouse mode to " + param.getValue());
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
