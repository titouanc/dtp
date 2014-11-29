package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.TextureView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author wmoulart
 * @brief Controller of the JPanel that will open when user wants to change the texture of a group.
 */
public class TextureController implements ActionListener, Observer {
	// Attributes
	private TextureView _view;
	private Project _project;
	
	static final public String _CHANGETEXTURE	= "Change Texture";
	
	
	
	/**
	 * @param aProject
	 */
	public TextureController(Project aProject){	
		_project = aProject; 
		_project.addObserver(this);
	}
	
	/**
	 * Run the View
	 */
	public void run(){
		initView(_project);
	}
	
	/**
	 * @param aProject
	 */
	public void initView(Project aProject){
		_view = new TextureView(this,aProject);
	}
	
	/**
	 * @return The controller view 
	 */
	public TextureView getView(){
		return _view;
	}

	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (cmd.equals(_CHANGETEXTURE)) {
			System.out.println("fdp");
		}
	}

	@Override
	public void update(Observable obs, Object arg) {
		System.out.println("UPDATE");
		if (obs instanceof Project){
			Config conf = (Config) arg ;
			if (conf.getName().equals("texture.mode") ){
				updateTextureMode(conf.getValue());
			}
		}
		
	}

	@SuppressWarnings("deprecation")
	private void updateTextureMode(String value) {
		if (value.equals("shown")){
			_view.show();
			System.out.println("SHOW");
		}
		else{
			System.out.println("HIDE");

			_view.hide();
		}/*
		else if (value.equals("hidden")){
			_view.hide();
		}	*/
	}
	

}
