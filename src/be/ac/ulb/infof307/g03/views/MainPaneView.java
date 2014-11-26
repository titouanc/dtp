/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import be.ac.ulb.infof307.g03.controllers.MainPaneController;
import be.ac.ulb.infof307.g03.controllers.ObjectTreeController;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 * This class implements the main view of the application, a splitpane
 * It contains a 3D view on the right. A tree on the left
 * @brief Main part of the Window
 */
public class MainPaneView extends JPanel {
	
	private MainPaneController _controller;
	
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _hSplitPane, _vSplitPane;
	private JScrollPane _worldListScrollPane;
	private JScrollPane _objectListScrollPane;
	private ObjectTreeController _worldTree;
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 * @param newController This view's controller
	 * @param project The projet to be display on the MainPane
	 * @param canvas The canvas containing the 3D/2D view
	 */
	public MainPaneView(MainPaneController newController, Project project, Canvas canvas){
		super(new BorderLayout());
		
		_controller = newController;
		
        // Create an object tree
        _worldTree = new ObjectTreeController(project);
        
        // Create left menu
        _worldListScrollPane = new JScrollPane(_worldTree.getView()); 
        // Set up resize behavior
        Dimension listScrollPaneDimension = new Dimension(150,140);
        _worldListScrollPane.setMinimumSize(listScrollPaneDimension);
        _worldListScrollPane.setPreferredSize(listScrollPaneDimension);
        
        _objectListScrollPane = new JScrollPane(/* TODO */);
        // Set up resize behavior
        _objectListScrollPane.setMinimumSize(listScrollPaneDimension);
        _objectListScrollPane.setPreferredSize(listScrollPaneDimension);
        
	     // Create split pane
	     _vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,_worldListScrollPane,_objectListScrollPane);
	     // Set up split pane
	     _vSplitPane.setDividerLocation(240);
        
        // Create split pane
		_hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_vSplitPane,canvas);
		// Set up split pane
		_hSplitPane.setDividerLocation(150);
		
		
		// add the splitpane to the inherited Jpanel
		this.add(_hSplitPane);
	}

}
