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
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.BorderLayout;

import java.awt.Dimension;

public class ToolsBar extends JPanel  {
	private JToolBar _toolBar = new JToolBar("HomePlan Toolbox");
    protected JTextArea textArea;

    public ToolsBar() {
    	super(new BorderLayout());

        //Create the toolBar.
        _toolBar.setFloatable(false); // defini si la toolBar peut bouger 
        _addButons();

        //Create the text area used for output.  Request
        //enough space for 5 rows and 30 columns.
        textArea = new JTextArea(5, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Lay out the main panel.
        setPreferredSize(new Dimension(450, 130));
        add(_toolBar, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
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




    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("toolBarDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new ToolsBar());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
	        createAndShowGUI();
            }
        });
    }
}
