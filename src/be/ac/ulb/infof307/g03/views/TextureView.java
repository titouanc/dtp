package be.ac.ulb.infof307.g03.views;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;

import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.controllers.ToolsBarController;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Walter
 * @brief view of the panel that ill open when user wants to change texture
 */
public class TextureView extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private TextureController _textureController;
	private Project _project;
	
	private JPanel _typeSelection;
	private JScrollPane _materialList;
	private GridBagLayout _paneLayout;
	

	/**
	 * @param newControler
	 * @param project
	 */
	public TextureView(TextureController newControler, Project project) {
    	super();  	
    	_textureController = newControler;
    	_project = project;
        
    	_paneLayout = new GridBagLayout();
    	//this.setLayout(_paneLayout);
    	this.setLayout(_paneLayout);
		
		this.addTypeSelection();
		this.addMaterialChoice();
        
    }
	
	public void addTypeSelection(){
		_typeSelection = new JPanel();

        GridBagConstraints c = new GridBagConstraints();
		
		JLabel typeLabel = new JLabel("Type : ");
		JComboBox<String> typeChoice = new JComboBox<String>();
		typeChoice.addItem("Color");
		typeChoice.addItem("Texture");
		
		_typeSelection.add(typeLabel);
		_typeSelection.add(typeChoice);
		
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.anchor = GridBagConstraints.PAGE_START; //bottom of space		
		this.add(_typeSelection,c);
		
	}
	
	public void addMaterialChoice(){


		String[] data = {"one", "two", "three", "four"};
		JList<String> materialList = new JList<String>(data);
		_materialList = new JScrollPane(materialList);

        GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
	    c.anchor = GridBagConstraints.PAGE_START; //bottom of space;
	    c.weightx = 1;
	    c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 2;
		//c.ipady = 200;
		this.add(_materialList,c);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}
