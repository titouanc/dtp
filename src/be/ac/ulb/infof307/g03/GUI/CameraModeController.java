package be.ac.ulb.infof307.g03.GUI;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

public class CameraModeController {
	
	public static final boolean VIEW3D = false;
	public static final boolean VIEW2D = true;
	
	private boolean _currentMode = VIEW2D;
	private Camera2D _cam2D = new Camera2D();
	private Camera3D _cam3D = new Camera3D();
	
	CameraModeController(){
		_cam2D.setEnabled(true);
		_cam3D.setEnabled(false);
	}
	
	public void changeMode(boolean mode){
		if(mode != _currentMode){
			if(mode == VIEW3D){
				_cam2D.setEnabled(false);
				_cam3D.setEnabled(true);
				//_cam3D.resetDirection();
			} else{
				_cam2D.setEnabled(true);
				_cam3D.setEnabled(false);
				_cam2D.resetDirection();
			}
			_currentMode = !_currentMode;
		}
	}
	
	public void setCamera(Camera cam) {
		_cam2D.setCam(cam);
		_cam3D.setCam(cam);
	}
	
	public void setInputManager(InputManager inputManager) {
		_cam2D.setInputManager(inputManager);
		_cam3D.setInputManager(inputManager);
	}
	
}
