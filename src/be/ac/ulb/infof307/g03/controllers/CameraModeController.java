package be.ac.ulb.infof307.g03.controllers;


import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

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
	private Project _project;
	
	/**
	 * 
	 * @param aProject
	 */
	CameraModeController(Project aProject) {
		_project = aProject;
		_cam2D.setEnabled(true);
		_cam3D.setEnabled(false);
		aProject.addObserver(this);
	}
	
	/**
	 * 
	 * @param mode
	 */
	public void changeMode(String mode){
		if(!mode.equals(_currentMode)){
			if(mode.equals(VIEW3D)){
				_cam2D.setEnabled(false);
				_cam3D.setEnabled(true);
				//_cam3D.resetDirection();
				_currentMode = VIEW3D;
			} else{
				_cam2D.setEnabled(true);
				_cam3D.setEnabled(false);
				_cam2D.resetDirection();
				_currentMode = VIEW2D;
			}		
		}
	}
	
	/**
	 * 
	 * @param cam
	 */
	public void setCamera(Camera cam) {
		_cam2D.setCam(cam);
		_cam3D.setCam(cam);
	}
	
	/**
	 * 
	 * @param inputManager
	 */
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
	
}
