/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

/**
 * @author pierre
 * @brief GUI's toolbar
 */


import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend JToolBar
 */
public class ToolsBarView extends JToolBar implements ActionListener  {
	
	ToolsBarController controller;
	
	private static final long serialVersionUID = 1L;
	
	// buttons actions
	static final private String _UNDO = "Undo";
	static final private String _REDO = "Redo";
	
	static final private String _LINE = "Line";
	static final private String _GROUP = "Group";
	
	static final private String _FLOOR_UP = "FloorUp";
	static final private String _FLOOR_DOWN = "FloorDown";
	
	static final private String _2D = "2D";
	static final private String _3D = "3D";

    /**
     * Constructor of the class ToolsBar.
     * It add property to the bar: Buttons, not flotable, lay out
     */
    public ToolsBarView(ToolsBarController c) {
    	super("HomePlan Toolbox");
    	
    	controller = c;
    	
    	// define if toolsbar can move
        this.setFloatable(false); 
        // add buttons
        _addButons();
        //Lay out the main panel.
    }
    
    /**
     * The private method used for creating all the buttons 
     */
    private void _addButons(){
        _addButtonsUndoRedo();
        _addForms();
        _addButtonsFloor();
        _addButtonsDimension();
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
    	JToggleButton secondDimension = new JToggleButton("2D");
    	secondDimension.setSelected(true);
    	JToggleButton thirdDimension = new JToggleButton("3D");
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
     * Inherited method from ActionListener abstract class
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (_UNDO.equals(cmd)) { 
			controller.onUndo();
        } 
		else if (_REDO.equals(cmd)) {
        	controller.onRedo();
        }
        else if (_LINE.equals(cmd)) {
        	controller.onLine() ;
        }
        else if (_GROUP.equals(cmd)) {
        	controller.onGroup();
        } 
		
        else if (_FLOOR_DOWN.equals(cmd)) {
        	controller.onFloorDown() ;
        }
        else if (_FLOOR_UP.equals(cmd)) {
        	controller.onFloorUp();
        }
        else if (_2D.equals(cmd)) {
        	controller.on2d() ;
        }
        else if (_3D.equals(cmd)) {
        	controller.on3d();
        }	
	}
}
