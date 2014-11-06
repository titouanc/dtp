/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 * This class implements the main view of the application, a splitpane
 * It contains a 3D view on the right. A tree on the left
 */
public class MainPaneView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	
	private ObjectTree _objectTree;
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
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
		
		this.add(_splitPane);
	}

}
