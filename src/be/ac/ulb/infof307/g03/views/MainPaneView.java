/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import be.ac.ulb.infof307.g03.controllers.MainPaneController;
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
	
	private MainPaneController _controller;
	
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _leftPane;
	private JSplitPane _rightPane;
	private JScrollPane _listScrollPane;
	private JScrollPane _textureScrollPane;
	private ObjectTreeController _objectTree;
	private TextureController _texture ;
	
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 * @param newController This view's controller
	 * @param project The project to be display on the MainPane
	 * @param canvas The canvas containing the 3D/2D view
	 */
	public MainPaneView(MainPaneController newController, Project project, Canvas canvas){
		super(new BorderLayout());
		
		_controller = newController;
		
        // Create an object tree
        _objectTree = new ObjectTreeController(project);
        _objectTree.run();
        
        _texture = new TextureController(project);
        _texture.run();
        
        Dimension listScrollPaneDimension = new Dimension(150,480);
        Dimension textureScrollPaneDimension = new Dimension(200,480);

       
        // Create left menu
        _listScrollPane = new JScrollPane(_objectTree.getView()); 
        _listScrollPane.setMinimumSize(listScrollPaneDimension);
        _listScrollPane.setPreferredSize(listScrollPaneDimension);
        
        // Create split pane
        _leftPane  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane,canvas);

		// Set up split pane
        _leftPane.setOneTouchExpandable(true);
        _leftPane.setDividerLocation(150);
		
        // Create right menu
        
        _textureScrollPane = new JScrollPane (_texture.getView());
        _textureScrollPane.setMinimumSize(textureScrollPaneDimension);
        _textureScrollPane.setPreferredSize(textureScrollPaneDimension); 
        //_rightPane
        _rightPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_leftPane,_textureScrollPane);
        _rightPane.setOneTouchExpandable(true);
        _rightPane.setDividerLocation(350);
        
        
		// add the splitpane to the inherited Jpanel
		this.add(_leftPane);
		this.add(_rightPane,BorderLayout.EAST);
		
		//Remove texture pane
		//this.remove(_rightPane);
		
	}

}
