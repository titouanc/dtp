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
import java.awt.FlowLayout;
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
    	super("Texture Edit",JToolBar.VERTICAL);  	
    	_textureController = newControler;
    	_project = project;
        this.setFloatable(false); 
        
        String prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/asset/";
		ImageIcon redIcon = new ImageIcon(prefix + "cursor.png");
		
		//ButtonGroup buttonGroup = new ButtonGroup();
		
		
		this.setLayout(new FlowLayout(FlowLayout.TRAILING));
        
        JToggleButton redButton = new JToggleButton("Red",redIcon);
        redButton.setSize(this.getWidth(), 50);
        //redButton.setSize(50, 50);

        //buttonGroup.add(redButton);
        
        JToggleButton greenButton = new JToggleButton("Green",redIcon);
        greenButton.setSize(50, 50);
        //buttonGroup.add(greenButton);
        //buttonGroup.
        this.add(redButton);
        this.add(greenButton);
        
    }
	

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}
