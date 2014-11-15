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
	
	public static final String VIEW3D = "3D";
	public static final String VIEW2D = "2D";
	
	private String _currentMode = VIEW2D;
	private Camera2D _cam2D = new Camera2D();
	private Camera3D _cam3D = new Camera3D();
	
	CameraModeController(Project proj){
		_cam2D.setEnabled(true);
		_cam3D.setEnabled(false);
		proj.addObserver(this);
	}
	
	public void changeMode(String mode){
		System.out.println("[CameraController] Change view mode to " + mode);
		if (mode != _currentMode){
			if (mode.equals(VIEW3D)){
				_cam2D.setEnabled(false);
				_cam3D.setEnabled(true);
				//_cam3D.resetDirection();
			} else if (mode.equals(VIEW2D)) {
				_cam2D.setEnabled(true);
				_cam3D.setEnabled(false);
				_cam2D.resetDirection();
			}
			_currentMode = mode;
		}
	}
	
	public void setCamera(Camera cam) {
		_cam2D.setCam(cam);
		_cam3D.setCam(cam);
	}
	
	public Camera2D get2DCam(){
		return _cam2D;
	}
	
	public void setInputManager(InputManager inputManager) {
		_cam2D.setInputManager(inputManager);
		_cam3D.setInputManager(inputManager);
	}

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
	
	public void setToRemove(WorldView wv) {
		_cam2D.setWv(wv);
	}
	
}
