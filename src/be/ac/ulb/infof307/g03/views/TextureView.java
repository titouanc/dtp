package be.ac.ulb.infof307.g03.views;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;

import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.controllers.ToolsBarController;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Walter
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JToolBar implements Observer {

	private static final long serialVersionUID = 1L;
	private TextureController _textureController;
	private Project _project;

	/**
	 * @param newControler
	 * @param project
	 */
	public TextureView(TextureController newControler, Project project) {
    	super("Texture Edit");  	
    	_textureController = newControler;
    	_project = project;
        this.setFloatable(false); 
        JPanel contentPane = new JPanel(new BorderLayout());    
        // Create the toolbar
        _textureController = new TextureController(project);
        _textureController.run();
        
    }

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}
