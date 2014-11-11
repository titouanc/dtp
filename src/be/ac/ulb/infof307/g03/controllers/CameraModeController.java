package be.ac.ulb.infof307.g03.controllers;


import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.WorldView;
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

	@Override
	public void update(Observable o, Object arg) {
		//if (arg.getClass()==_project.getClass()) {
			try {				
				changeMode(_project.config("world.mode"));
				_cam2D.setMouseMode(_project.config("mouse.mode"));
				_cam3D.setMouseMode(_project.config("mouse.mode"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		//}
	}
	
	public void setToRemove(WorldView wv) {
		_cam2D.setWv(wv);
	}
	
}
