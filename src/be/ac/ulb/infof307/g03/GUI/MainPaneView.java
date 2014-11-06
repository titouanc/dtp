/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 * This class implements the main view of the application, a splitpane
 * It contains a 3D view on the right. A tree on the left
 * @brief Main part of the Window
 */
public class MainPaneView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	private ObjectTree _objectTree;
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 * @param The projet to be display on the MainPane
	 * @param The canvas containing the 3D/2D view
	 */
	public MainPaneView(Project project, Canvas canvas){
		super(new BorderLayout());
		
        // Create an object tree
        _objectTree = new ObjectTree(project);
        
        // Create left menu
        _listScrollPane = new JScrollPane(_objectTree); 
        // Set up resize behavior
        Dimension listScrollPaneDimension = new Dimension(150,480);
        _listScrollPane.setMinimumSize(listScrollPaneDimension);
        _listScrollPane.setPreferredSize(listScrollPaneDimension);
        
        // Create split pane
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane,canvas);
		// Set up split pane
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(150);
		
		
		// add the splitpane to the inherited Jpanel
		this.add(_splitPane);
	}

}
