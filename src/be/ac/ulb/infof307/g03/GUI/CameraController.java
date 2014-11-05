package be.ac.ulb.infof307.g03.GUI;



public class CameraController {
	
	public static final boolean VIEW3D = false;
	public static final boolean VIEW2D = true;
	
	private boolean _currentMode = VIEW2D;
	private Camera2D _cam2D;
	
	CameraController(Camera2D camera){
		_cam2D = camera;
		_cam2D.setEnabled(true);
	}
	
	public void changeMode(boolean mode){
		if(mode != _currentMode){
			if(mode == VIEW3D){
				_cam2D.setEnabled(false);
			}
			else{
				_cam2D.setEnabled(true);
				
			}
			_currentMode = !_currentMode;
		}
	}

}
