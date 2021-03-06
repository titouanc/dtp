/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

/**
 * @author pierre,walter, Bruno
 * @brief GUI's toolbar View
 */

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JButton;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Project;

import java.util.Observable;
import java.util.Observer;

/**
 * This class implement a toolbar for the HomePlan GUI
 * It extend JToolBar
 */
public class ToolsBarView extends JToolBar implements Observer  {
		
	private class NavigationModule extends JToolBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public JToggleButton worldButton, objectButton, cursorButton, handButton, rotateButton;
		
		NavigationModule() {
			super();
			
			addButtonMouseMode();
	        addButtonsDimension();
			addButtonEditionMode();
		}
		
		private void addButtonEditionMode() {
	    	String classPath = getClass().getResource("ToolsBarView.class").toString();
	    	Icon worldIcon, objectIcon;
	    	String prefix = "";
	    	
	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		prefix = "/";
	    		worldIcon = new ImageIcon(getClass().getResource(prefix + "world.png"));
	    		objectIcon = new ImageIcon(getClass().getResource(prefix + "object.png"));
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
	    		worldIcon = new ImageIcon(prefix + "world.png");
	    		objectIcon   = new ImageIcon(prefix + "object.png");
	    	}
	    	
	    	this.worldButton = createJToggleButton(worldIcon, ToolsBarController.WORLD, "Switch to the world mode.");
	    	this.objectButton = createJToggleButton(objectIcon, ToolsBarController.OBJECT, "Switch to the object mode.");
	    	
	    	// Restore mode
	    	String editionMode = _project.config("edition.mode");
	    	if (editionMode.equals("object")) {
	    		this.objectButton.setSelected(true);
	    	} else {
	    		this.worldButton.setSelected(true);
	    	}
	    	
	    	ButtonGroup buttonGroup = new ButtonGroup();
	    	buttonGroup.add(this.worldButton);
	    	buttonGroup.add(this.objectButton);
	    	
	    	this.add(this.worldButton);
	    	this.add(this.objectButton);

		}
		
		/**
	     * The private method used for creating buttons to switch
	     * between 2D and 3D. Those buttons are mutually exclusive
	     */  
	    private void addButtonsDimension() {
	    	JToggleButton secondDimension = createJToggleButton("2D", ToolsBarController.VIEW2D, "Switch to 2D view");
	    	JToggleButton thirdDimension = createJToggleButton("3D", ToolsBarController.VIEW3D, "Switch to 3D view");
	    	
	    	// restore mode
	    	String cameraMode = _project.config("camera.mode");
	    	if (cameraMode.equals("2D")) {
	    		secondDimension.setSelected(true);
	    	} else if (cameraMode.equals("3D")) {
	    		thirdDimension.setSelected(true);
	    	}
	    	
	    	// ButtonGroup make the mutually exclusive 
	        ButtonGroup buttonGroup = new ButtonGroup();
	        buttonGroup.add(secondDimension);
	        buttonGroup.add(thirdDimension);

	        this.add(secondDimension);
	        this.add(thirdDimension);

	        this.addSeparator();
	    }
		
		 /**
	     * The private method used for creating rotation buttons.
	     * Those buttons are mutually exclusive
	     */  
	    private void addButtonMouseMode() {
	    	String classPath = getClass().getResource("ToolsBarView.class").toString();
	    	String prefix = "";

	    	Icon cursorIcon, grabIcon, rotateIcon;
	    
	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		prefix = "/";
	    		cursorIcon = new ImageIcon(getClass().getResource(prefix + "cursor.png"));
	    		grabIcon = new ImageIcon(getClass().getResource(prefix + "grab.png"));
	    		rotateIcon = new ImageIcon(getClass().getResource(prefix + "rotate.png"));
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
	    		cursorIcon = new ImageIcon(prefix + "cursor.png");
	    		grabIcon   = new ImageIcon(prefix + "grab.png");
	    		rotateIcon = new ImageIcon(prefix + "rotate.png");
	    	}
	    	
	    	//Creates the buttons
	    	this.cursorButton = createJToggleButton(cursorIcon, ToolsBarController.CURSOR, "Move element") ;
	    	this.handButton = createJToggleButton(grabIcon, ToolsBarController.HAND, "Move screen"); 
	    	this.rotateButton = createJToggleButton(rotateIcon, ToolsBarController.ROTATE, "Rotate the screen");

	    	//Creates the button group (so that there's only one button "selected" at a time
	        ButtonGroup buttonGroup = new ButtonGroup();
	        buttonGroup.add(this.cursorButton);
	        buttonGroup.add(this.handButton);
	        buttonGroup.add(this.rotateButton);
	        
	        // restore mouse mode / restore button selection
	        String mouseMode = _project.config("mouse.mode");
	        if (mouseMode.equals("dragRotate")) {
	        	this.rotateButton.setSelected(true);
	        } else if (mouseMode.equals("dragMove")) {
	        	this.handButton.setSelected(true);
	    	} else {
	    		this.cursorButton.setSelected(true);
	    	}

	        add(this.cursorButton);
	        add(this.handButton);
	        add(this.rotateButton);
	        addSeparator();
	    }
		
	}
	
	private class WorldEditionModule extends JToolBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JToggleButton _createButton;
		
		public WorldEditionModule() {
			super();
			
			addButtonsFloor();
			addButtonWallEdition();
		}
		
		/**
	     * The private method used for creating buttons to switch
	     * from one floor to another
	     */
	    private void addButtonsFloor() {
	    	Icon plusFloorIcon;
	    	String classPath = getClass().getResource("ToolsBarView.class").toString();
	    	String prefix = "";


	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		prefix = "/";
	    		plusFloorIcon = new ImageIcon(getClass().getResource(prefix + "plusFloor.png"));
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
	    		plusFloorIcon = new ImageIcon(prefix + "plusFloor.png");
	    	}
	        // button new floor
	        add(createJButton(plusFloorIcon, ToolsBarController.FLOOR_NEW, "Create a new floor."));
	        // separator
	        addSeparator();
	    }
	    
	    private void addButtonWallEdition() {
	    	String classPath = getClass().getResource("ToolsBarView.class").toString();
	    	String prefix = "";

	    	Icon pencilIcon;

	    	if(classPath.subSequence(0, 3).equals("rsr")){
	    		prefix = "/";
	    		pencilIcon = new ImageIcon(getClass().getResource(prefix + "pencil.png"));
	    	} else {
	    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
	    		pencilIcon = new ImageIcon(prefix + "pencil.png");
	    	}

	    	//Creates the buttons
	    	_createButton = createJToggleButton(pencilIcon, ToolsBarController.NEWELEMENT, "Create a new room");

	    	add(_createButton);
	    }
	}
	
	private class ObjectEditionModule extends JToolBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		JToggleButton cubeButton, sphereButton, pyramidButton, cylinderButton;
		
		public ObjectEditionModule() {
			super();
			
	        addButtonsObject();
		}
		
		 private void addButtonsObject(){
		    	Icon cubeIcon,sphereIcon, pyramidIcon, cylinderIcon;
		    	String prefix;
		    	String classPath = getClass().getResource("ToolsBarView.class").toString();
		    	
		    	if(classPath.subSequence(0, 3).equals("rsr")){
		    		prefix = "/";
		    		cubeIcon = new ImageIcon(getClass().getResource(prefix + "cube.png"));
		    		sphereIcon = new ImageIcon(getClass().getResource(prefix + "sphere.png"));
		    		pyramidIcon = new ImageIcon(getClass().getResource(prefix + "pyramid.png"));
		    		cylinderIcon = new ImageIcon(getClass().getResource(prefix + "cylinder.png"));
		    	} else {
		    		prefix = System.getProperty("user.dir") + "/src/be/ac/ulb/infof307/g03/assets/Tools/";
		    		cubeIcon = new ImageIcon(prefix + "cube.png");
		    		sphereIcon   = new ImageIcon(prefix + "sphere.png");
		    		pyramidIcon = new ImageIcon(prefix + "pyramid.png");
		    		cylinderIcon   = new ImageIcon(prefix + "cylinder.png");
		    	}

		    	cubeButton = createJToggleButton(cubeIcon, ToolsBarController.CUBE, "Add a new cube");
		    	sphereButton = createJToggleButton(sphereIcon, ToolsBarController.SPHERE, "Add a new sphere");
		    	pyramidButton = createJToggleButton(pyramidIcon, ToolsBarController.PYRAMID, "Add a new cube");
		    	cylinderButton = createJToggleButton(cylinderIcon, ToolsBarController.CYLINDER, "Add a new sphere");
	
		    	add(cubeButton);
		    	add(sphereButton);
		    	add(pyramidButton);
		    	add(cylinderButton);
		 }
		
	}
	
	private static final long serialVersionUID = 1L;
	
	private ToolsBarController _controller;
	private Project _project;
	NavigationModule _navigationModule = null;
	WorldEditionModule _worldEditionModule = null;
	ObjectEditionModule _objectEditionModule = null;

    /**
     * Constructor of the class ToolsBar.
     * It add property to the bar: Buttons, not floatable, lay out
     * @param newControler The view's controller
     * @param project The main project
     */
    public ToolsBarView(ToolsBarController newControler, Project project) {
    	super("HomePlan Toolbox");
    	
    	_controller = newControler;
    	_project = project;
    	
    	// define if toolsbar can move
        this.setFloatable(false); 
        // add buttons
        addButons();
        
        groupMouseAction();
    }
    
    private void groupMouseAction() {
    	ButtonGroup bg = new ButtonGroup();
    	bg.add(_objectEditionModule.cubeButton);
    	bg.add(_objectEditionModule.sphereButton);
    	bg.add(_objectEditionModule.pyramidButton);
    	bg.add(_objectEditionModule.cylinderButton);
    	bg.add(_worldEditionModule._createButton);
    	bg.add(_navigationModule.cursorButton);
    	bg.add(_navigationModule.handButton);
    	bg.add(_navigationModule.rotateButton);
    }
    
    /**
     * Set world mode selected
     */
    public void setWorldModeSelected() {
    	_navigationModule.worldButton.setSelected(true);
    }
    
    /**
     * Set object mode selected
     */
    public void setObjectModeSelected() {
    	_navigationModule.objectButton.setSelected(true);
    }

	/**
     * The private method used for creating all the buttons 
     */

    private void addButons(){
    	_navigationModule = new NavigationModule();
    	add(_navigationModule);
    	_worldEditionModule = new WorldEditionModule();
    	add(_worldEditionModule);
    	_objectEditionModule = new ObjectEditionModule();
    	add(_objectEditionModule);
    }
    
    
    /**
     * Create a JButton 
     * @param icon An icon to be displayed on the button.
     * @param action A string who's an action alias.
     * @param desc A string who describes what happens when the button is pressed.
     * @return The created button.
     */
    private JButton createJButton(Icon icon, String action, String desc) {
    	JButton button = new JButton(icon);
        button.setActionCommand(action);
        button.setToolTipText(desc);
        button.addActionListener(_controller);
    	return button;
    }
    
    /**
     * Create a JToggleButton with a label.
     * @param label A string who's the label to display on the button.
     * @param action A string who's the action alias.
     * @param desc A string who describes what happens when the button is pressed.
     * @return The created button.
     */
    private JToggleButton createJToggleButton(String label, String action, String desc) {
    	JToggleButton button = new JToggleButton(label);
    	button.setActionCommand(action);
    	button.setToolTipText(desc);
    	button.addActionListener(_controller);
    	return button;
    }
    
    /**
    * Create a JToggleButton with an icon.
    * @param icon An icon who will be displayed on the button.
    * @param action A string who's the action alias.
    * @param desc A string who describes what happens when the button is pressed.
    * @return The created button.
    */
    private JToggleButton createJToggleButton(Icon icon, String action, String desc) {
    	JToggleButton button = new JToggleButton(icon);
    	button.setActionCommand(action);
    	button.setToolTipText(desc);
    	button.addActionListener(_controller);
    	return button;
    }
    
    /**
     * Set the world edition module visible
     * @param visible If visible
     */
    public void setWorldEditionModuleVisible(boolean visible) {
    	_worldEditionModule.setVisible(visible);
    }
    
    /**
     * Set object edition module visible
     * @param visible If visible
     */
    public void setObjectEditionModuleVisible(boolean visible) {
    	_objectEditionModule.setVisible(visible);
    }
    
    @Override
 	public void update(Observable obs, Object arg) {
    	if (obs instanceof Project) {
    		Config param = (Config) arg;
    		if (param.getName().equals("mouse.mode")){
    			_worldEditionModule._createButton.setEnabled (! param.getValue().equals("construct"));
    		}
    	}
 	}

	/**
	 * Set the tool drag select selected
	 * @param b Boolean
	 */
	public void setDragSelectSelected(boolean b) {
		_navigationModule.cursorButton.setSelected(b);
	}

}
