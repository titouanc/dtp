package be.ac.ulb.infof307.g03.GUI;

public class CameraController {
	
	public static final boolean VIEW3D = false;
	public static final boolean VIEW2D = true;
	
	private boolean _currentMode = VIEW2D;
	private Camera2D _cam2D;
	private Camera3D _cam3D;
	
	CameraController(Camera2D cam2D, Camera3D cam3D){
		_cam2D = cam2D;
		_cam2D.setEnabled(true);
		_cam3D = cam3D; 
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
	
}
