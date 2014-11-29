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
import be.ac.ulb.infof307.g03.controllers.ObjectListController;
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
	private ObjectListController _objectList;
	private ObjectTreeController _objectTree;
	
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
        
        // Create the object list
        _objectList = new ObjectListController(project);
        _objectList.run();

		// Create an object tree
        _objectTree = new ObjectTreeController(project);
        _objectTree.run();

        
        // Create left menu
        _worldListScrollPane = new JScrollPane(_objectTree.getView()); 
        // Set up resize behavior
        Dimension listScrollPaneDimension = new Dimension(150,140);
        _worldListScrollPane.setMinimumSize(listScrollPaneDimension);
        _worldListScrollPane.setPreferredSize(listScrollPaneDimension);
        
        _objectListScrollPane = new JScrollPane(_objectList.getView());
        // Set up resize behavior
        _objectListScrollPane.setMinimumSize(listScrollPaneDimension);
        _objectListScrollPane.setPreferredSize(listScrollPaneDimension);
        
	     // Create split pane
	     _vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,_worldListScrollPane,_objectListScrollPane);
	     // Set up split pane
	     _vSplitPane.setDividerLocation(240);
	     _vSplitPane.setBorder(null);
        
        // Create split pane
		_hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_vSplitPane,canvas);
		// Set up split pane
		_hSplitPane.setDividerLocation(150);
		_hSplitPane.setBorder(null);
		
		
		// add the splitpane to the inherited Jpanel
		this.add(_hSplitPane);
	}

}
