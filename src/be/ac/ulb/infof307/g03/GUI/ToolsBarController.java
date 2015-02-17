package be.ac.ulb.infof307.g03.GUI;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JOptionPane;

/**
 * @author fhennecker, pierre, wmoulart
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController implements ActionListener, Observer {
	// Attributes
	private ToolsBarView view;
	private Project project;
	
	// Buttons actions alias
	/**
	 * Draw room button identifier
	 */
	static final public String NEWELEMENT = "TB_NewElement";

	/**
	 * Floor up button identifier
	 */
	static final public String FLOOR_UP   = "TB_FloorUp";
	
	/**
	 * Floor down button identifier
	 */
	static final public String FLOOR_DOWN = "TB_FloorDown";
	
	/**
	 * New Floor button identifier
	 */
	static final public String FLOOR_NEW  = "TB_FloorNew";

	/**
	 * 2D mode button identifier
	 */
	static final public String VIEW2D = "TB_2D";
	
	/**
	 * 3D mode button identifier
	 */
	static final public String VIEW3D = "TB_3D";

	/**
	 * Rotate mouse mode button identifier
	 */
	static final public String ROTATE = "TB_Rotate";
	
	/**
	 * Grab mouse mode button identifier
	 */
	static final public String HAND = "TB_Grab";
	
	/**
	 * Select mouse mode button identifier
	 */
	static final public String CURSOR = "TB_Cursor";
	
	/**
	 * World mode button identifier
	 */
	static final public String WORLD = "TB_World";
	
	/**
	 * Object mode button identifier
	 */
	static final public String OBJECT = "TB_Object";

	/**
	 * Cube creation button identifier
	 */
	static final public String CUBE = "TB_Cube";
	
	/**
	 * Sphere creation button identifier
	 */
	static final public String SPHERE = "TB_Sphere";
	
	/**
	 * Pyramid creation button identifier
	 */
	static final public String PYRAMID = "TB_Pyramid";
	
	/**
	 * Cylinder creation button identifier
	 */
	static final public String CYLINDER = "TB_Cylinder";
	
	// Edition mode alias
	static final private String WORLDMODE = "world";
	static final private String OBJECTMODE = "object";
	
	private String currentObjectMode = null;
	
	/**
	 * Constructor of ToolsBarController.
	 * It creates the ToolsBar view
	 * @param aProject The main project
	 */
	public ToolsBarController(Project aProject){	
		project = aProject;  
		aProject.addObserver(this);
		try {
			project.getGeometryDAO().addObserver(this);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	/**
	 * @author fhennecker
	 * Run the ToolsBar GUI
	 */
	public void run(){
		initView(project);
		project.addObserver(view);
		//Sets the default mode
        this.onDragSelectMode();
        
        this.currentObjectMode = project.config("edition.mode");
        if (this.currentObjectMode.equals(""))
        	this.currentObjectMode = WORLDMODE;
        else
        	updateEditionMode();
	}
	
	/**
	 * This method initiate the view
	 * @param aProject The main project
	 */
	public void initView(Project aProject){
		view = new ToolsBarView(this,aProject);
	}
	
	/**
	 * @return The controller view 
	 */
	public ToolsBarView getView(){
		return view;
	}
    
    /**
     * Callback for "new floor" button
     */
    public void onFloorNew(){
		try {
			MasterDAO daoFactory = project.getGeometryDAO();
			GeometricDAO<Floor> floorDao = daoFactory.getDao(Floor.class);
			List<Floor> allFloors = floorDao.queryForAll();
			// Still no floors in the database, create just one
			if (allFloors.isEmpty()){
				floorDao.insert(new Floor());
			} else {
				int minFloorIndex = 0;
				double height = 0;
				for (Floor floor : allFloors){
					if (floor.getIndex() > minFloorIndex)
						minFloorIndex = floor.getIndex();
					height += floor.getHeight();
				}
				Floor newFloor = new Floor(7);
				newFloor.setBaseHeight(height);
				newFloor.setIndex(minFloorIndex + 1);
				floorDao.insert(newFloor);
				this.project.config("floor.curren", newFloor.getUID());
			}
	    	daoFactory.notifyObservers();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will communicate with the controller
     */
    public void on2d(){
    	Log.info("Switch to 2D mode");
    	project.config("camera.mode", "2D");
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	Log.info("Switch to 3D mode");
    	project.config("camera.mode", "3D");
    }
    
    /**
     * The private method is called when the rotation button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragRotateMode(){
    	Log.info("Switch to rotate mode");
    	project.config("mouse.mode", "dragRotate");
    }

    /**
     * The private method is called when the cursor button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragSelectMode(){
    	Log.info("Switch to drag-select mode");
    	project.config("mouse.mode", "dragSelect");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragMoveMode(){
    	Log.info("Switch to drag-move mode");
    	project.config("mouse.mode", "dragMove");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */
    public void onConstruction(){
    	try {
    		GeometricDAO<Floor> floorDao = project.getGeometryDAO().getDao(Floor.class);
			if( floorDao.queryForAll().isEmpty()){
				Log.info("User try to switch to construction mode, but there is no floor");
				JOptionPane.showMessageDialog(view, "You have to create a floor first");
				view.setDragSelectSelected(true);
				onDragSelectMode();
			}
			else{
				Log.info("Switch to construction mode");
		    	project.config("mouse.mode", "construct");
			}
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
	private void updateEditionMode(String value) {
		if (this.currentObjectMode!=value)  {
			this.currentObjectMode = value;
			updateEditionMode();
		}	
	}
	
	private void updateEditionMode() {
		if (this.currentObjectMode.equals(WORLDMODE)) {
			view.setWorldModeSelected();
			view.setWorldEditionModuleVisible(true);
			view.setObjectEditionModuleVisible(false);
		} else if (this.currentObjectMode.equals(OBJECTMODE)) {
			view.setObjectModeSelected();
			view.setWorldEditionModuleVisible(false);
			view.setObjectEditionModuleVisible(true);
		}
		project.config("mouse.mode", "dragSelect");
		view.setDragSelectSelected(true);
	}
    
    /**
     * Inherited method from ActionListener abstract class
     * @param action A mouse click
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (cmd.equals(NEWELEMENT)) {
        	onConstruction();
        } else if (cmd.equals(FLOOR_NEW)){
        	onFloorNew();
        } else if (cmd.equals(VIEW2D)) {
        	on2d();
        } else if (cmd.equals(VIEW3D)) {
        	on3d();
        } else if (cmd.equals(ROTATE)){
        	onDragRotateMode();
        } else if (cmd.equals(HAND)){
        	onDragMoveMode();
        } else if (cmd.equals(CURSOR)){
        	onDragSelectMode();
        } else if (cmd.equals(WORLD)) {
        	onWorldMode();
        } else if (cmd.equals(OBJECT)) {
        	String aName = JOptionPane.showInputDialog("New object name ?");
        	onObjectMode(aName);
        } else if (cmd.equals(CUBE)) {
        	onCubeCreation();
        } else if (cmd.equals(SPHERE)) {
        	onSphereCreation();
        } else if (cmd.equals(PYRAMID)) {
        	onPyramidCreation();
        } else if (cmd.equals(CYLINDER)) {
        	onCylinderCreation();
        }
     }

     private void onObjectMode(String aName) {
    	 Log.log(Level.FINEST,"[DEBUG] User clicked on : object");
    	 if (aName != null) {
    		 Entity entity = new Entity(aName);
    		 try {
    			 MasterDAO daoFactory = project.getGeometryDAO();
    			 GeometricDAO<Entity> dao = daoFactory.getDao(Entity.class);
    			 dao.insert(entity);
    			 daoFactory.notifyObservers();
    		 } catch (SQLException e) {
    			 // TODO Auto-generated catch block
    			 e.printStackTrace();
    		 }
    		 project.config("entity.current", entity.getUID());
    		 project.config("edition.mode", "object");
    	 } else {
    		 view.setWorldModeSelected();
    	 }
     }

	private void onWorldMode() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : world");
		project.config("mouse.mode", "dragSelect");
		project.config("edition.mode", "world");	
	}
	
	private void onCubeCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : cube");
		project.config("mouse.mode", "cube");
	}
	
	private void onSphereCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : sphere");
		project.config("mouse.mode", "sphere");
	}
	
	private void onPyramidCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : pyramid");
		project.config("mouse.mode", "pyramid");
	}
	
	private void onCylinderCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : cylinder");
		project.config("mouse.mode", "cylinder");
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof Project) {
			Config config = (Config) obj;
			if (config.getName().equals("edition.mode")) {
				updateEditionMode(config.getValue());
			}
		}
		else if (obs instanceof MasterDAO){
			if (project.config("mouse.mode").equals("construct")){
				try {
					if( project.getGeometryDAO().getDao(Floor.class).queryForAll().isEmpty()){
						// if no more floor and user has selected the construct
						view.setDragSelectSelected(true);
						onDragSelectMode();
					}
				} catch (SQLException ex) {
					Log.exception(ex);
				}
			}
		}
	}
}
