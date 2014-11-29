package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.TextureView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * @author wmoulart
 * @brief Controller of the JPanel that will open when user wants to change the texture of a group.
 */
public class TextureController implements ActionListener,MouseListener, Observer {
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
/*
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (cmd.equals(_CHANGETEXTURE)) {
			System.out.println("fdp");
		}
	}
*/
	@Override
	public void update(Observable obs, Object arg) {
		System.out.println("UPDATE");
		if (obs instanceof Project){
			Config conf = (Config) arg ;
			if (conf.getName().equals("texture.mode") ){
				//updateTextureMode(conf.getValue());
			}
		}
		
	}

	/*
	@SuppressWarnings("deprecation")
	private void updateTextureMode(String value) {
		if (value.equals("shown")){
			_view.show();
			System.out.println("SHOW");
		}
		else{
			System.out.println("HIDE");

			_view.hide();
		}
		else if (value.equals("hidden")){
			_view.hide();
		}	
	}
*/
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {			
			if (_view.getCurrentMode().equals("Colors")){
				_project.config("texture.selected",_view.getSelectedColor());
			}
			else{
				_project.config("texture.selected",_view.getSelectedTexture());
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
