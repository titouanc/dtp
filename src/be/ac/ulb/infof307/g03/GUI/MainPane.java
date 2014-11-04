/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 * This class implements the main view of the application, namely a splitview.
 * It contains a 3D view on the right.
 */
public class MainPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JSplitPane _splitPane;
	private JScrollPane _listScrollPane;
	private Canvas3D _canvas;
	private ObjectTree _objectTree;
	
	public MainPane(Project project){
		super(new BorderLayout());
		
		// !!! temporary !!!
        String[] listShape = new String[] {"Rectangle", "Rectangle", "Rond", "Cercle"};
        JList list = new JList(listShape);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
        _canvas = new Canvas3D(project);
        _canvas.setSettings(settings);
        _canvas.createCanvas();
        // Set up event listener
        JmeCanvasContext context = (JmeCanvasContext) _canvas.getContext();
        context.setSystemListener(_canvas);
        
        Integer width=640, height=480;
        try {
			if (project.config("canvas.width") != "")
				width = new Integer(project.config("canvas.width"));
			if (project.config("canvas.height") != "")
				height = new Integer(project.config("canvas.height"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Set up resize behavior
        Dimension jme3Dimension = new Dimension(width, height);
        context.getCanvas().setMinimumSize(jme3Dimension);
        context.getCanvas().setPreferredSize(jme3Dimension);
        // Start jme3 canvas
        _canvas.startCanvas();
        
        System.out.println(String.format("Starting canvas %dx%d", width, height));
        
        // Create split pane
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,_listScrollPane,context.getCanvas());
		// Set up split pane
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setDividerLocation(100);
		
		this.add(_splitPane);
	}

}
