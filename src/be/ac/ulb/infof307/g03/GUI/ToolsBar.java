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
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend Jpanel
 */
public class ToolsBar extends JPanel implements ActionListener  {
	
	private static final long serialVersionUID = 1L;
	
	// the toolbar is named in case it's dragged out of the main frame
	private JToolBar _toolBar = new JToolBar("HomePlan Toolbox");
	
	// buttons actions
	static final private String _UNDO = "Undo";
	static final private String _REDO = "Redo";
	
	// TODO action shapes
	
	static final private String _FLOOR_UP = "FloorUp";
	static final private String _FLOOR_DOWN = "FloorDown";
	
	static final private String _2D = "2D";
	static final private String _3D = "3D";

    /**
     * Constructor of the class ToolsBar.
     * It add property to the bar: Buttons, not flotable, lay out
     */
    public ToolsBar() {
    	super(new BorderLayout());
    	// define if toolsbar can move
        _toolBar.setFloatable(false); 
        // add buttons
        _addButons();
        //Lay out the main panel.
        add(_toolBar, BorderLayout.PAGE_START);
    }
    
    /**
     * The private method is called when the button undo
     * is clicked. It will commmunicate with the controller
     */
    private void _clickedUndo(){
    	System.out.println("undo");
    	
    }
    
    /**
     * The private method is called when the button redo
     * is clicked. It will commmunicate with the controller
     */ 
    private void _clickedRedo(){
    	System.out.println("redo");
    	
    }
    /**
     * The private method is called when the button with a shape //TODO
     * is clicked. It will commmunicate with the controller
     */
    private void _clickedShape(){
    	// TODO define how shape will be implemented
    	System.out.println("shape");
    }
    
    /**
     * The private method is called when the button floor up
     * is clicked. It will commmunicate with the controller
     */
    private void _clickedFloorUp(){
    	System.out.println("floorUp");	
    }
    
    /**
     * The private method is called when the button floor down
     * is clicked. It will commmunicate with the controller
     */
    private void _clickedFloorDown(){
    	System.out.println("floor down");
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will commmunicate with the controller
     */
    private void _clicked2d(){
    	System.out.println("go2D");
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will commmunicate with the controller
     */
    private void _clicked3d(){
    	System.out.println("go3D");
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
            _toolBar.add(button);
    	}
        _toolBar.addSeparator();
    }
    
    /**
     * The private method used for creating buttons for all the form
     * possible to add on the project
     */
    private void _addForms() {
    	JButton rectangle = new JButton("Rectangle");
    	JButton circle = new JButton("Circle");
    	JButton line = new JButton("Line");

        _toolBar.add(rectangle);
        _toolBar.add(circle);
        _toolBar.add(line);
        

        _toolBar.addSeparator();
    }

    /**
     * The private method used for creating buttons to switch
     * from one floor to another
     */
    private void _addButtonsFloor() {
    	// button + floor
        JButton buttonUp = new JButton("+");
        buttonUp.setActionCommand(_FLOOR_UP);
        buttonUp.setToolTipText("This will increase the floor seen");
        buttonUp.addActionListener(this);
        _toolBar.add(buttonUp);
        
        // button - floor
        JButton buttonDown = new JButton("-");
        buttonDown.setActionCommand(_FLOOR_DOWN);
        buttonDown.setToolTipText("This will decrease the floor seen");
        buttonDown.addActionListener(this);
        _toolBar.add(buttonDown);

        _toolBar.addSeparator();
    }
    
    /**
     * The private method used for creating buttons to switch
     * between 2D and 3D. Those buttons are mutually exclusive
     */  
    private void _addButtonsDimension() {
    	JToggleButton secondDimension = new JToggleButton("2D");
    	secondDimension.setSelected(true);
    	JToggleButton thirdDimension = new JToggleButton("3D");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(secondDimension);
        buttonGroup.add(thirdDimension);
        
        secondDimension.setActionCommand(_2D);
        secondDimension.setToolTipText("Switch to 2D view");
        secondDimension.addActionListener(this);
        
        thirdDimension.setActionCommand(_3D);
        thirdDimension.setToolTipText("Switch to 3D view");
        thirdDimension.addActionListener(this);

        _toolBar.add(secondDimension);
        _toolBar.add(thirdDimension);

        _toolBar.addSeparator();
    }

    /**
     * Inherited method from ActionListener abstract class
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (_UNDO.equals(cmd)) { 
			_clickedUndo();
        } 
		else if (_REDO.equals(cmd)) {
        	_clickedRedo();
        }
		// TODO action listening on shapes 
		
        else if (_FLOOR_DOWN.equals(cmd)) {
        	_clickedFloorDown() ;
        }
        else if (_FLOOR_UP.equals(cmd)) {
        	_clickedFloorUp();
        }
        else if (_2D.equals(cmd)) {
        	_clicked2d() ;
        }
        else if (_3D.equals(cmd)) {
        	_clicked3d();
        	
        }
		
	}



}
