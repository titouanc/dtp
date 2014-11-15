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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend JToolBar
 */
public class ToolsBarView extends JToolBar implements ActionListener  {
	private static final long serialVersionUID = 1L;
	
	private ToolsBarController _controller;
	
	// buttons actions
	static final private String _UNDO 		= "Undo";
	static final private String _REDO 		= "Redo";
	
	static final private String _LINE  		= "Line";
	static final private String _GROUP 		= "Group";
	
	static final private String _FLOOR_UP   = "FloorUp";
	static final private String _FLOOR_DOWN = "FloorDown";
	
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
    public ToolsBarView(ToolsBarController newControler) {
    	super("HomePlan Toolbox");
    	
    	_controller = newControler;
    	
    	// define if toolsbar can move
        this.setFloatable(false); 
        // add buttons
        _addButons();
      
    }
    
    /**
     * The private method used for creating all the buttons 
     */
    private void _addButons(){
        _addButtonsUndoRedo();
        _addForms();
        _addButtonsFloor();
        _addButtonsDimension();
        _addButtonRotation();
 
    }
    
    /**
     * The private method used for creating the button undo to 
     * go back in history and the button redo that does the opposite  
     */
    private void _addButtonsUndoRedo() {
    	// listButton, for adding/removing a button without pain
    	String[] listButton = new String[] {_UNDO, _REDO};
    	
    	for( String buttonName : listButton){
            JButton button = new JButton(buttonName);
            button.setActionCommand(buttonName);
            button.setToolTipText(buttonName+" last action");
            button.addActionListener(this);
            this.add(button);
    	}
       this.addSeparator();
    }
    
    /**
     * The private method used for creating buttons for all the form
     * possible to add on the project
     */
    private void _addForms() {
    	// button line
    	JButton lineButton = new JButton(_LINE);
    	lineButton.setActionCommand(_LINE);
    	lineButton.setToolTipText("Create a line");
    	lineButton.addActionListener(this);
        this.add(lineButton);
        
        //button group
    	JButton groupButton = new JButton(_GROUP);
    	groupButton.setActionCommand(_GROUP);
    	groupButton.setToolTipText("Create a group");
    	groupButton.addActionListener(this);
        this.add(groupButton);
        

        this.addSeparator();
    }

    /**
     * The private method used for creating buttons to switch
     * from one floor to another
     */
    private void _addButtonsFloor() {
    	// button floor up
        JButton buttonUp = new JButton("+");
        buttonUp.setActionCommand(_FLOOR_UP);
        buttonUp.setToolTipText("This will increase the floor seen");
        buttonUp.addActionListener(this);
        this.add(buttonUp);
        
        // button floor down
        JButton buttonDown = new JButton("-");
        buttonDown.setActionCommand(_FLOOR_DOWN);
        buttonDown.setToolTipText("This will decrease the floor seen");
        buttonDown.addActionListener(this);
        this.add(buttonDown);

        this.addSeparator();
    }
    
    /**
     * The private method used for creating buttons to switch
     * between 2D and 3D. Those buttons are mutually exclusive
     */  
    private void _addButtonsDimension() {
    	JToggleButton secondDimension = new JToggleButton(_2D);
    	secondDimension.setSelected(true);
    	JToggleButton thirdDimension = new JToggleButton(_3D);
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
    	Icon _cursorImage = new ImageIcon(dir + "cursor.png");
    	Icon _grabImage = new ImageIcon(dir + "grab.png");
    	Icon _rotateImage = new ImageIcon(dir + "rotate.png");
    	
    	//Creates the buttons
    	JToggleButton rotate = new JToggleButton(_rotateImage);
    	JToggleButton hand   = new JToggleButton(_grabImage);
    	JToggleButton cursor = new JToggleButton(_cursorImage);

    	//Creates the button group (so that there's only one button "selected" at a time
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rotate);
        buttonGroup.add(hand);
        buttonGroup.add(cursor);
        
        //Set the cursor button as currently being used
        cursor.setSelected(true);
        
        //Binds the buttons to an action
        rotate.setActionCommand(_ROTATE);
        rotate.setToolTipText("Rotate the screen");
        rotate.addActionListener(this);
        
        hand.setActionCommand(_HAND);
        hand.setToolTipText("Move screen");
        hand.addActionListener(this);
        
        cursor.setActionCommand(_CURSOR);
        cursor.setToolTipText("Move screen");
        cursor.addActionListener(this);
        

        this.add(rotate);
        this.add(hand);
        this.add(cursor);
        this.addSeparator();
        
    }
     
    /**
     * Inherited method from ActionListener abstract class
     * @param action A mouse click
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (_UNDO.equals(cmd)) { 
			_controller.onUndo();
        } 
		else if (_REDO.equals(cmd)) {
        	_controller.onRedo();
        }
        else if (_LINE.equals(cmd)) {
        	_controller.onLine() ;
        }
        else if (_GROUP.equals(cmd)) {
        	_controller.onGroup();
        } 		
        else if (_FLOOR_DOWN.equals(cmd)) {
        	_controller.onFloorDown() ;
        }
        else if (_FLOOR_UP.equals(cmd)) {
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

}
