/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre,walter
 * This class implements the main view of the application, a splitpane
 * It contains a 3D view on the right. A tree on the left
 * @brief Main part of the Window
 */

public class MainPaneView extends JPanel implements ComponentListener {
	
	
	private static final long serialVersionUID = 1L;

	private JSplitPane horizontalLeftSplitPane; 
	private JSplitPane horizontalRightSplitPane; 
	private JSplitPane verticalLeftSplitPane;
	private JSplitPane verticalRightSplitPane;
	private JScrollPane worldListScrollPane;
	private JScrollPane objectListScrollPane;
	private JScrollPane textureScrollPane;
	private JScrollPane statScrollPane;
	
	private ObjectListController objectList;
	private ObjectTreeController objectTree;
	private TextureController texture ;
	private StatisticsController stats;
	
	private Dimension listScrollPaneMinimumDimension;
	private Dimension listScrollPanePreferedDimension;
	
	
	/**
	 * Constructor of MainPane. It create a splitpane with a tree on
	 * the left and jMonkey 3D integration on the right
	 * @param project The project to be display on the MainPane
	 * @param canvas The canvas containing the 3D/2D view
	 */
	public MainPaneView(Project project, Canvas canvas){
		super(new BorderLayout());
		this.addComponentListener(this);
        
        // Create the object list
        this.objectList = new ObjectListController(project);
        this.objectList.run();

		// Create an object tree
        this.objectTree = new ObjectTreeController(project);
        this.objectTree.run();

        
        this.texture = new TextureController(project);
        this.texture.run();
        
        this.stats = new StatisticsController(project);
        this.stats.run();
        
        listScrollPanePreferedDimension = new Dimension(200,480);
        listScrollPaneMinimumDimension = new Dimension(150,75);

        this.worldListScrollPane = new JScrollPane(this.objectTree.getView()); 
        // Set up resize behavior
        this.worldListScrollPane.setMinimumSize(listScrollPaneMinimumDimension);
        this.worldListScrollPane.setPreferredSize(listScrollPanePreferedDimension);
        
        this.objectListScrollPane = new JScrollPane(this.objectList.getView());
        // Set up resize behavior
        this.objectListScrollPane.setMinimumSize(listScrollPaneMinimumDimension);
        this.objectListScrollPane.setPreferredSize(listScrollPanePreferedDimension);
	     
		
        // Create the texture
        
        
        this.textureScrollPane = new JScrollPane (this.texture.getView());
        this.textureScrollPane.setMinimumSize(listScrollPaneMinimumDimension);
        this.textureScrollPane.setPreferredSize(listScrollPanePreferedDimension);
        
        // Create the stat
        this.statScrollPane = new JScrollPane (this.stats.getView());
        this.statScrollPane.setMinimumSize(listScrollPaneMinimumDimension);
        this.statScrollPane.setPreferredSize(listScrollPanePreferedDimension);
        
        
	     // Create split pane
	     this.verticalLeftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,this.worldListScrollPane,this.objectListScrollPane);
	     // Set up split pane
	     this.verticalLeftSplitPane.setDividerLocation(240);
	     this.verticalLeftSplitPane.setBorder(null);
        
        // Create split pane
		this.horizontalLeftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.verticalLeftSplitPane,canvas);
		// Set up split pane
		this.horizontalLeftSplitPane.setDividerLocation(150);
		this.horizontalLeftSplitPane.setBorder(null);  
        
	     // Create split pane
	     this.verticalRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,this.textureScrollPane,this.statScrollPane);
	     // Set up split pane
	     this.verticalRightSplitPane.setDividerLocation(240);
	     this.verticalRightSplitPane.setBorder(null);
	     
	     // Create split pane
	     this.horizontalRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.horizontalLeftSplitPane,this.verticalRightSplitPane);
	     // Set up split pane
		this.horizontalRightSplitPane.setDividerLocation(getRightPanelDividerLocation());
		this.horizontalRightSplitPane.setBorder(null);  
	     
	     
		// add the splitpane to the inherited Jpanel
		add(this.horizontalRightSplitPane);
		//add(this.verticalRightSplitPane, BorderLayout.EAST);
	}


	@Override
	public void componentHidden(ComponentEvent arg0) {
		// No action wanted here
		
	}


	@Override
	public void componentMoved(ComponentEvent arg0) {
		// No action wanted here
		
	}


	@Override
	public void componentResized(ComponentEvent arg0) {
		this.horizontalRightSplitPane.setDividerLocation(getRightPanelDividerLocation());
		
	}


	@Override
	public void componentShown(ComponentEvent arg0) {
		// No action wanted here
		
	}
	
	private int getRightPanelDividerLocation(){
		return (int) (this.getSize().getWidth() - this.listScrollPanePreferedDimension.getWidth());
	}

}
