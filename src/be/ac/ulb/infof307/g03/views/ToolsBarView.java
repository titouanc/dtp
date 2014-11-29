/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

/**
 * @author pierre,walter, Bruno
 * @brief GUI's toolbar View
 */


import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;

import be.ac.ulb.infof307.g03.controllers.ToolsBarController;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;


/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend JToolBar
 */
public class ToolsBarView extends JToolBar implements Observer  {
	private static final long serialVersionUID = 1L;
	
	private ToolsBarController _controller;
	private Project _project;
	private JToggleButton _createButton, _cursorButton, _worldButton, _objectButton;

    /**
     * Constructor of the class ToolsBar.
     * It add property to the bar: Buttons, not floatable, lay out
     * @param newControler The view's controller
     */
    public ToolsBarView(ToolsBarController newControler, Project project) {
    	super("HomePlan Toolbox");
    	
    	_controller = newControler;
    	_project = project;
    	
    	// define if toolsbar can move
        this.setFloatable(false); 
        // add buttons
        _addButons();
        
    }
    
    public void setWorldModeSelected() {
    	Log.log(Level.FINEST, "[DEBUG] world button setSelected");
    	_worldButton.setSelected(true);
    }
    
    public void setObjectModeSelected() {
    	Log.log(Level.FINEST, "[DEBUG] object button setSelected");
    	_objectButton.setSelected(true);
    }

	/**
     * The private method used for creating all the buttons 
     */
    private void _addButons(){
        _addButtonsFloor();
        _addButtonsDimension();
        _addButtonMouseMode();
        _addButtonEditionMode();
        addButtonsObject();
    }

    private void _addButtonEditionMode() {
    	_worldButton = createJToggleButton("world", ToolsBarController.WORLD, "Switch to the world mode.");
    	_objectButton = createJToggleButton("object", ToolsBarController.OBJECT, "Switch to the object mode.");
    	
    	// Restore mode
    	String editionMode = _project.config("edition.mode");
    	if (editionMode.equals("object")) {
    		_objectButton.setSelected(true);
    	} else {
    		_worldButton.setSelected(true);
    	}
    	
    	ButtonGroup buttonGroup = new ButtonGroup();
    	buttonGroup.add(_worldButton);
    	buttonGroup.add(_objectButton);
    	
    	this.add(_worldButton);
    	this.add(_objectButton);
    	
    	this.addSeparator();
	}

	/**
     * The private method used for creating buttons to switch
     * from one floor to another
     */
    private void _addButtonsFloor() {
    	// button floor up
        this.add(createJButton("up", ToolsBarController.FLOOR_UP, "This will increase the floor seen"));
        // button floor down
        this.add(createJButton("down", ToolsBarController.FLOOR_DOWN, "This will decrease the floor seen"));
        // button new floor
        this.add(createJButton("new Floor", ToolsBarController.FLOOR_NEW, "Create a new floor."));
        // separator
        this.addSeparator();
    }
    
    /**
     * The private method used for creating buttons to switch
     * between 2D and 3D. Those buttons are mutually exclusive
     */  
    private void _addButtonsDimension() {
    	JToggleButton secondDimension = createJToggleButton("2D", ToolsBarController.VIEW2D, "Switch to 2D view");
    	JToggleButton thirdDimension = createJToggleButton("3D", ToolsBarController.VIEW3D, "Switch to 3D view");
    	
    	// restore mode
    	String worldMode = _project.config("world.mode");
    	if (worldMode.equals("2D")) {
    		secondDimension.setSelected(true);
    	} else if (worldMode.equals("3D")) {
    		thirdDimension.setSelected(true);
    	}
    	
    	// ButtonGroup make the mutually exclusive 
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(secondDimension);
        buttonGroup.add(thirdDimension);

        this.add(secondDimension);
        this.add(thirdDimension);

        this.addSeparator();
    }

    /**
     * The private method used for creating rotation buttons.
     * Those buttons are mutually exclusive
     */  
    private void _addButtonMouseMode() {
    	String classPath = getClass().getResource("ToolsBarView.class").toString();
    	String prefix = "";

    	Icon cursorIcon, grabIcon, rotateIcon, pencilIcon;
    
    	if(classPath.subSequence(0, 3).equals("rsr")){
    		prefix = "/";
    		cursorIcon = new ImageIcon(getClass().getResource(prefix + "cursor.png"));
    		grabIcon = new ImageIcon(getClass().getResource(prefix + "grab.png"));
    		rotateIcon = new ImageIcon(getClass().getResource(prefix + "rotate.png"));
    		pencilIcon = new ImageIcon(getClass().getResource(prefix + "pencil.png"));
    	} else {
    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/asset/";
    		cursorIcon = new ImageIcon(prefix + "cursor.png");
    		grabIcon   = new ImageIcon(prefix + "grab.png");
    		rotateIcon = new ImageIcon(prefix + "rotate.png");
    		pencilIcon = new ImageIcon(prefix + "pencil.png");
    	}
    	
    	//Creates the buttons
    	_cursorButton = createJToggleButton(cursorIcon, ToolsBarController.CURSOR, "Move element") ;
    	JToggleButton hand = createJToggleButton(grabIcon, ToolsBarController.HAND, "Move screen"); 
    	JToggleButton rotate = createJToggleButton(rotateIcon, ToolsBarController.ROTATE, "Rotate the screen");
    	_createButton = createJToggleButton(pencilIcon, ToolsBarController.NEWELEMENT, "Create a new room");

    	//Creates the button group (so that there's only one button "selected" at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_cursorButton);
        buttonGroup.add(_createButton);
        buttonGroup.add(hand);
        buttonGroup.add(rotate);
        
        // restore mouse mode / restore button selection
        String mouseMode = _project.config("mouse.mode");
        if (mouseMode.equals("dragRotate")) {
        	rotate.setSelected(true);
        } else if (mouseMode.equals("dragMove")) {
        	hand.setSelected(true);
    	} else {
    		_cursorButton.setSelected(true);
    	}

        this.add(_cursorButton);
        this.add(_createButton);
        this.add(hand);
        this.add(rotate);
        this.addSeparator();
        
    }
    
    private void addButtonsObject(){

    	Icon cubeIcon,sphereIcon;
    	String prefix;
    	String classPath = getClass().getResource("ToolsBarView.class").toString();
    	
    	if(classPath.subSequence(0, 3).equals("rsr")){
    		prefix = "/";
    		cubeIcon = new ImageIcon(getClass().getResource(prefix + "cube.png"));
    		sphereIcon = new ImageIcon(getClass().getResource(prefix + "sphere.png"));
    	} else {
    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/asset/";
    		cubeIcon = new ImageIcon(prefix + "cube.png");
    		sphereIcon   = new ImageIcon(prefix + "sphere.png");
    	}

    	JToggleButton cubeButton = createJToggleButton(cubeIcon, ToolsBarController.CUBE, "Add a new cube");
    	JToggleButton sphereButton = createJToggleButton(sphereIcon, ToolsBarController.SPHERE, "Add a new sphere");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(cubeButton);
        buttonGroup.add(sphereButton);
    	
    	this.add(cubeButton);
    	this.add(sphereButton);
    	
    	this.addSeparator();
        
    	
    	
    }
    
    /**
     * Create a JButton 
     * @param label A string to display on the button.
     * @param action A string who's an action alias.
     * @param desc A string who describes what appens when the button is pressed.
     * @return The created button.
     */
    private JButton createJButton(String label, String action, String desc) {
    	JButton button = new JButton(label);
        button.setActionCommand(action);
        button.setToolTipText(desc);
        button.addActionListener(_controller);
    	return button;
    }
    
    /**
     * Create a JToggleButton with a label.
     * @param label A string who's the label to display on the button.
     * @param action A string who's the action alias.
     * @param desc A string who describes what happens when the button is pressed.
     * @return The created button.
     */
    private JToggleButton createJToggleButton(String label, String action, String desc) {
    	JToggleButton button = new JToggleButton(label);
    	button.setActionCommand(action);
    	button.setToolTipText(desc);
    	button.addActionListener(_controller);
    	return button;
    }
    
    /**
    * Create a JToggleButton with an icon.
    * @param icon An icon who will be displayed on the button.
    * @param action A string who's the action alias.
    * @param desc A string who describes what happens when the button is pressed.
    * @return The created button.
    */
    private JToggleButton createJToggleButton(Icon icon, String action, String desc) {
    	JToggleButton button = new JToggleButton(icon);
    	button.setActionCommand(action);
    	button.setToolTipText(desc);
    	button.addActionListener(_controller);
    	return button;
    }
     
    @Override
 	public void update(Observable obs, Object arg) {
    	if (obs instanceof Project) {
    		Config param = (Config) arg;
    		if (param.getName().equals("mouse.mode")){
    			_createButton.setEnabled (! param.getValue().equals("construct"));
    		}
    	}
 	}

}
