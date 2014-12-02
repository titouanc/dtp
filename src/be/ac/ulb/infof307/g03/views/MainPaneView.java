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
import be.ac.ulb.infof307.g03.controllers.TextureController;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre,walter
 * This class implements the main view of the application, a splitpane
 * It contains a 3D view on the right. A tree on the left
 * @brief Main part of the Window
 */
public class MainPaneView extends JPanel {
	
	private MainPaneController controller;
	
	private static final long serialVersionUID = 1L;
	
	private JScrollPane textureScrollPane;

	private JSplitPane hSplitPane, vSplitPane;
	private JScrollPane worldListScrollPane;
	private JScrollPane objectListScrollPane;
	private ObjectListController objectList;

	private ObjectTreeController objectTree;
	private TextureController texture ;
	
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 * @param newController This view's controller
	 * @param project The project to be display on the MainPane
	 * @param canvas The canvas containing the 3D/2D view
	 */
	public MainPaneView(MainPaneController newController, Project project, Canvas canvas){
		super(new BorderLayout());
		
		this.controller = newController;
        
        // Create the object list
        this.objectList = new ObjectListController(project);
        this.objectList.run();

		// Create an object tree
        this.objectTree = new ObjectTreeController(project);
        this.objectTree.run();

        
        this.texture = new TextureController(project);
        this.texture.run();
        
        Dimension listScrollPaneDimension = new Dimension(150,480);
        Dimension textureScrollPaneDimension = new Dimension(150,480);

        this.worldListScrollPane = new JScrollPane(this.objectTree.getView()); 
        // Set up resize behavior
        this.worldListScrollPane.setMinimumSize(listScrollPaneDimension);
        this.worldListScrollPane.setPreferredSize(listScrollPaneDimension);
        
        this.objectListScrollPane = new JScrollPane(this.objectList.getView());
        // Set up resize behavior
        this.objectListScrollPane.setMinimumSize(listScrollPaneDimension);
        this.objectListScrollPane.setPreferredSize(listScrollPaneDimension);
        
	     // Create split pane
	     this.vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,this.worldListScrollPane,this.objectListScrollPane);
	     // Set up split pane
	     this.vSplitPane.setDividerLocation(240);
	     this.vSplitPane.setBorder(null);
        
        // Create split pane
		this.hSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.vSplitPane,canvas);
		// Set up split pane
		this.hSplitPane.setDividerLocation(150);
		this.hSplitPane.setBorder(null);
		
        // Create right menu
        this.textureScrollPane = new JScrollPane (this.texture.getView());
        this.textureScrollPane.setMinimumSize(textureScrollPaneDimension);
        this.textureScrollPane.setPreferredSize(textureScrollPaneDimension);
        
		// add the splitpane to the inherited Jpanel
		add(this.hSplitPane);
		add(this.textureScrollPane, BorderLayout.EAST);
	}

}
