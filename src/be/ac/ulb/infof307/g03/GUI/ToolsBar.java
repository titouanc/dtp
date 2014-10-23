/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

/**
 * @author pierre
 *
 */


import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import java.awt.Dimension;

public class ToolsBar extends JPanel  {
	private JToolBar _toolBar = new JToolBar("HomePlan Toolbox");
    protected JTextArea textArea;

    public ToolsBar() {
    	super(new BorderLayout());

        //Create the toolBar.
        _toolBar.setFloatable(false); // define if toolsbar can move 
        _addButons();


        //Lay out the main panel.
        add(_toolBar, BorderLayout.PAGE_START);
        

    }
    
    private void _addButons(){
        _addButtonsUndoRedo();
        _addForms();
        _addButtonsFloor();
        _addButtonsDimension();
    }

    private void _addButtonsUndoRedo() {
        JButton buttonUndo = new JButton("Undo");
        _toolBar.add(buttonUndo);

        JButton buttonRedo = new JButton("Redo");
        _toolBar.add(buttonRedo);


        _toolBar.addSeparator();
    }

    private void _addForms() {
    	JButton rectangle = new JButton("Rectangle");
    	JButton circle = new JButton("Circle");
    	JButton line = new JButton("Line");

        _toolBar.add(rectangle);
        _toolBar.add(circle);
        _toolBar.add(line);
        

        _toolBar.addSeparator();
    }

    private void _addButtonsFloor() {
    	JButton floorMinus = new JButton("-");
    	JButton floorPlus = new JButton("+");

        _toolBar.add(floorMinus);
        _toolBar.add(floorPlus);

        _toolBar.addSeparator();
    }
    
    private void _addButtonsDimension() {
    	JToggleButton secondDimension = new JToggleButton("2D");
    	secondDimension.setSelected(true);
    	JToggleButton thirdDimension = new JToggleButton("3D");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(secondDimension);
        buttonGroup.add(thirdDimension);

        _toolBar.add(secondDimension);
        _toolBar.add(thirdDimension);

        _toolBar.addSeparator();
    }



}
