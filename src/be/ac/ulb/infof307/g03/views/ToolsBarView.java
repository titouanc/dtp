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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;


/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend JToolBar
 */
public class ToolsBarView extends JToolBar implements ActionListener, Observer  {
	private static final long serialVersionUID = 1L;
	
	private ToolsBarController _controller;
	private Project _project;
	private JToggleButton _createButton;
	private JToggleButton _cursorButton;
	
	// buttons actions
	static final private String _NEWELEMENT	= "NewElement";
	
	static final private String _FLOOR_UP   = "FloorUp";
	static final private String _FLOOR_DOWN = "FloorDown";
	static final private String _FLOOR_NEW  = "FloorNew";
	
	static final private String _2D 		= "2D";
	static final private String _3D 		= "3D";
	
	static final private String _ROTATE  	= "Rotate";	
	static final private String _HAND 		= "Grab";  
	static final private String _CURSOR		= "Cursor";

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

	/**
     * The private method used for creating all the buttons 
     */
    private void _addButons(){
        _addButtonsFloor();
        _addButtonsDimension();
        _addButtonRotation();
 
    }

    /**
     * The private method used for creating buttons to switch
     * from one floor to another
     */
    private void _addButtonsFloor() {
    	// button floor up
        JButton buttonUp = new JButton("up");
        buttonUp.setActionCommand(_FLOOR_UP);
        buttonUp.setToolTipText("This will increase the floor seen");
        buttonUp.addActionListener(this);
        this.add(buttonUp);
        
        // button floor down
        JButton buttonDown = new JButton("down");
        buttonDown.setActionCommand(_FLOOR_DOWN);
        buttonDown.setToolTipText("This will decrease the floor seen");
        buttonDown.addActionListener(this);
        this.add(buttonDown);
        
        JButton buttonNew = new JButton("new Floor");
        buttonNew.setActionCommand(_FLOOR_NEW);
        buttonNew.setToolTipText("Create a new floor...");
        buttonNew.addActionListener(this);
        this.add(buttonNew);

        this.addSeparator();
    }
    
    /**
     * The private method used for creating buttons to switch
     * between 2D and 3D. Those buttons are mutually exclusive
     */  
    private void _addButtonsDimension() {
    	JToggleButton secondDimension = new JToggleButton(_2D);
    	JToggleButton thirdDimension = new JToggleButton(_3D);
    	
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
        
        secondDimension.setActionCommand(_2D);
        secondDimension.setToolTipText("Switch to 2D view");
        secondDimension.addActionListener(this);
        
        thirdDimension.setActionCommand(_3D);
        thirdDimension.setToolTipText("Switch to 3D view");
        thirdDimension.addActionListener(this);

        this.add(secondDimension);
        this.add(thirdDimension);

        this.addSeparator();
    }

    /**
     * The private method used for creating rotation buttons.
     * Those buttons are mutually exclusive
     */  
    private void _addButtonRotation() {
    	
    	//Stores the path for the assets
    	String dir = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/asset/";
    	
    	//Gets the images
    	Icon cursorImage = new ImageIcon(dir + "cursor.png");
    	Icon grabImage = new ImageIcon(dir + "grab.png");
    	Icon rotateImage = new ImageIcon(dir + "rotate.png");
    	Icon constructImage = new ImageIcon(dir + "pencil.png");
    	
    	//Creates the buttons
    	JToggleButton rotate = new JToggleButton(rotateImage);
    	JToggleButton hand   = new JToggleButton(grabImage);
    	_cursorButton = new JToggleButton(cursorImage);
    	_createButton = new JToggleButton(constructImage);

    	//Creates the button group (so that there's only one button "selected" at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(_cursorButton);
        buttonGroup.add(_createButton);
        buttonGroup.add(hand);
        buttonGroup.add(rotate);
        
        
        //Binds the buttons to an action
        rotate.setActionCommand(_ROTATE);
        rotate.setToolTipText("Rotate the screen");
        rotate.addActionListener(this);
        
        hand.setActionCommand(_HAND);
        hand.setToolTipText("Move screen");
        hand.addActionListener(this);
        
        _cursorButton.setActionCommand(_CURSOR);
        _cursorButton.setToolTipText("Move screen");
        _cursorButton.addActionListener(this);
        
        _createButton.setActionCommand(_NEWELEMENT);
    	_createButton.setToolTipText("Create a new room");
    	_createButton.addActionListener(this);
        
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
    
    /**
     * @return a JToggleButton
     */
    public JToggleButton getCreateButton(){
    	return _createButton ;
    }
     
    /**
     * Inherited method from ActionListener abstract class
     * @param action A mouse click
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (_NEWELEMENT.equals(cmd)) {
        	_controller.onConstruction();
        } 		
        else if (_FLOOR_DOWN.equals(cmd)) {
        	_controller.onFloorDown() ;
        }
        else if (_FLOOR_UP.equals(cmd)) {
        	_controller.onFloorUp();
        }
        else if (_FLOOR_NEW.equals(cmd)){
        	_controller.onFloorNew();
        	_controller.onFloorUp();
        }
        else if (_2D.equals(cmd)) {
        	_controller.on2d() ;
        }
        else if (_3D.equals(cmd)) {
        	_controller.on3d();
        }
        else if (_ROTATE.equals(cmd)){
        	_controller.onDragRotateMode();
        }
        else if (_HAND.equals(cmd)){
        	_controller.onDragMoveMode();
        }
        else if (_CURSOR.equals(cmd)){
        	_controller.onDragSelectMode();
        }

	}
     
     @Override
 	public void update(Observable o, Object arg) {
 		Config param = (Config) arg;
 		if (param.getName().equals("mouse.mode")){
 			_createButton.setEnabled (! param.getValue().equals("construct"));
 		}
 	}

}
