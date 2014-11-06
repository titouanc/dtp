/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
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
	private WorldController _world;
	private ObjectTree _objectTree;
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 */
	public MainPaneView(Project project){
		super(new BorderLayout());
		
        // Create an object tree
        _objectTree = new ObjectTree(project);
        
        // Create left menu
        _listScrollPane = new JScrollPane(_objectTree); 
        // Set up resize behavior
        Dimension listScrollPaneDimension = new Dimension(100,480);
        _listScrollPane.setMinimumSize(listScrollPaneDimension);
        _listScrollPane.setPreferredSize(listScrollPaneDimension);

        // Set up jme3 canvas' settings
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        // Create jme3 canvas
        _world = new WorldController();
        _world.getView().setSettings(settings);
        _world.getView().createCanvas();
        // Set up event listener
        JmeCanvasContext context = (JmeCanvasContext) _world.getView().getContext();
        context.setSystemListener(_world.getView());
        
        // Set up resize behavior
        Dimension jme3Dimension = new Dimension(640, 480);
        context.getCanvas().setMinimumSize(jme3Dimension);
        context.getCanvas().setPreferredSize(jme3Dimension);
        // Start jme3 canvas
        _world.getView().startCanvas();
        
        // Create split pane
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane,context.getCanvas());
		// Set up split pane
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(100);
		
		this.add(_splitPane);
	}

}
