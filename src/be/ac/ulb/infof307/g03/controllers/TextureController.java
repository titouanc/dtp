package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.TextureView;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author wmoulart
 * @brief Controller of the JPanel that will open when user wants to change the texture of a group.
 */
public class TextureController implements ActionListener {
	// Attributes
	private TextureView _view;
	private Project _project;
	
	static final public String _CHANGETEXTURE	= "Change Texture";
	
	
	
	/**
	 * @param aProject
	 */
	public TextureController(Project aProject){	
		_project = aProject;     
	}
	
	/**
	 * Run the View
	 */
	public void run(){
		initView(_project);
		_project.addObserver(_view);
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
	

}
