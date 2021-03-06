/**
 * 
 */
package be.ac.ulb.infof307.g03.world;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;

/**
 * @author fhennecker,pierre,wmoulart
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController extends CanvasController implements Observer {
	private Spatial endWall = null;
	private List<Spatial> liveWalls = new ArrayList<Spatial>() ;
	private Vector3f lastMousePos = null;
    
	// Attributes
    private List<Point> inConstruction = new LinkedList <Point>();

    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param view The controller's view
     * @param settings The jMonkey application settings
     */
    public WorldController(WorldView view, AppSettings settings){
    	super(view, settings);
        
        view.getProject().addObserver(this);
        view.makeScene();
    }
    
    /**
     * Getter
     * @return The current floor.
     */
    public Floor getCurrentFloor(){
    	return this.project.getSelectionManager().currentFloor();
    }
    
    /**
     * Drop the currently moving point:
     * - Compute final position
     * - Update in database and notify
     * - Set current moving point to null
     * @param finalMove Use to know if it's the last move of the point.
     */
    public void dropMovingPoint(boolean finalMove){
    	Point movingPoint = (Point) movingGeometric;
    	if (movingPoint == null)
    		return;
    	
    	Vector3f newPos = getXYForMouse((float) getCurrentFloor().getBaseHeight());
    	movingPoint.setX(newPos.x);
    	movingPoint.setY(newPos.y);
        try {
        	MasterDAO dao = this.project.getMasterDAO();
        	dao.getDao(Point.class).modify(movingPoint);
        	if (finalMove) 
        		movingGeometric = null;
        	dao.notifyObservers();
        } catch (SQLException err){
        	Log.exception(err);
        }
    }
    
    private void dropMovingItem(boolean finalMove) {
		Item moving = (Item) movingGeometric;
		if (moving == null)
			return;

		Vector3f delta = getXYForMouse(0).subtract(this.lastMousePos);
		moving.setAbsolutePosition(moving.getAbsolutePositionVector().add(delta));
		this.lastMousePos = getXYForMouse(0);
		if (finalMove) 
			try {
	    		MasterDAO dao = this.project.getMasterDAO();
	    		dao.getDao(Item.class).modify(moving);
	    		movingGeometric = null;
	    		dao.notifyObservers();
	    	} catch (SQLException err){
	    		Log.exception(err);
	    	}
		else {
			Change change = new Change(Change.UPDATE, moving);
			this.view.updateItem(change);
		}
	}
    
    /**
     * Toggle selection for a Area item, save to database and notify observers
     * @param area The Area item to select
     */
	public void selectArea(Area area) {
		this.project.getSelectionManager().toggleSelect(area.getRoom());
	}
	
	/**
     * Toggle selection for an Item, save to database and notify observers
     * @param item The Item to select
     */
	public void selectItem(Item item) {
		this.project.getSelectionManager().toggleSelect(item);
	}
	
    
    /**
     * Add the points in the Point List when user click to create his wall
     */
    public void construct(){
    	Vector3f newPos = getXYForMouse((float) getCurrentFloor().getBaseHeight());
    	Point lastPoint=new Point(newPos.x, newPos.y, 0);

		try {
        	MasterDAO daoFactory = this.project.getMasterDAO();
        	GeometricDAO<Point> pointDao = daoFactory.getDao(Point.class);
        	Point near = pointDao.queryForFirst(lastPoint.getQueryForNear(pointDao, 1));
        	if (near != null){
        		lastPoint = near;
        		//lastPoint.select();
        		pointDao.modify(lastPoint);
        	} else {
        		pointDao.insert(lastPoint);
        	}
        	daoFactory.notifyObservers();
        } catch (SQLException err){
        	Log.exception(err);
        }
		this.inConstruction.add(lastPoint);
		buildLive();
    }
    
    /**
     * Build the room in live
     */
    public void buildLive(){
    	if (this.inConstruction.size()>1){ // If more than 2 points, we can mesh them together
    		int lastPoint=this.inConstruction.size()-1;
    		double height = this.getCurrentFloor().getBaseHeight();
    		
    		Vector3f currentPoint= this.inConstruction.get(lastPoint).toVector3f();
    		Vector3f previousPoint = this.inConstruction.get(lastPoint-1).toVector3f();
    		
    		currentPoint.setZ((float) height);
    		previousPoint.setZ((float) height);
    		
    		Line line = new Line(currentPoint,previousPoint);
    		line.setLineWidth(3);
    		Spatial wall = new Geometry("line", line );
            Material mat = view.makeBasicMaterial(new ColorRGBA(1f, 1f, 0.2f, 0.8f));  
            mat.getAdditionalRenderState().setDepthTest(false);
            if (inConstruction.size()>2){
            	if (endWall!= null){
            		this.view.getRootNode().detachChild(endWall); // Detach old red line 
            	}
	    		Line endLine = new Line(currentPoint,inConstruction.get(0).toVector3f().setZ((float) height));
	    		endLine.setLineWidth(3);
	    		Spatial finishedWall = new Geometry("line", endLine );
	            Material endMat = view.makeBasicMaterial(new ColorRGBA(0.8f, 0f, 0f, 0.7f));
	            endMat.getAdditionalRenderState().setDepthTest(false);
	            finishedWall.setMaterial(endMat);
	    		this.view.getRootNode().attachChild(finishedWall);
	    		endWall=finishedWall ;	    		
            }

            wall.setMaterial(mat);
            liveWalls.add(wall);
    		this.view.getRootNode().attachChild(wall);
    	}
    }
    
	 /**
     * Mesh the points together for the walls creation
     */
    public void finalizeConstruct(){
    	try {
			MasterDAO daoFactory = this.project.getMasterDAO();
	    	GeometricDAO<Point> pointDao = daoFactory.getDao(Point.class);
			// Minimum 3 points to build a room
	    	if (this.inConstruction.size() >= 3){
	    		Room room = new Room();
	    		room.setFloor(getCurrentFloor());
	    		daoFactory.getDao(Room.class).insert(room);
	    		daoFactory.getDao(Room.class).refresh(room);
	    		room.setName(room.getUID());
	    		
	    		/* Create all areas of this room (Ground, Roof, Walls)  */
	    		for (Class<? extends Area> className : daoFactory.areaClasses){
					try {
						Constructor<? extends Area> constr = className.getConstructor(Room.class);
						Area newArea = constr.newInstance(room);
		    			daoFactory.getDao(className).insert(newArea);
					} catch (Exception e) {
						Log.error("Unable to use Area subclass constructor at runtime ! (Missing method ?)");
						Log.exception(e);
					}
	    		}
	    		
	    		/* Create Room shape */
	    		for (Point p : this.inConstruction)
	    			room.addPoints(p);
	    		room.addPoints(this.inConstruction.get(0)); // close polygon
	    		daoFactory.getDao(Room.class).modify(room);
	    		daoFactory.getDao(Floor.class).refresh(getCurrentFloor());
	    	}
	    	
	    	for (Point p : this.inConstruction){
	    		if (p.getBindings().size() == 0){
	    			pointDao.remove(p);
	    		}
	    	}
	    	if (this.endWall != null)
	    		this.view.getRootNode().detachChild(this.endWall);
	    	
	    daoFactory.notifyObservers();
	    this.inConstruction.clear();
	    for (Spatial s : this.liveWalls){
    		this.view.getRootNode().detachChild(s);
    	}


		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
    /**
     * Convert a click position to clicked item
     * @return The clicked Geometric item, or null if not found
     */
    @Override
    public Geometric getClickedObject(){
    	Geometric clicked = null;
        CollisionResults results = new CollisionResults();
        this.view.getRootNode().collideWith(getRayForMousePosition(), results);
        if (results.size() > 0){
        	// Get 3D object from scene
            Geometry selected = results.getClosestCollision().getGeometry();
            
            try {
            	MasterDAO dao = this.project.getMasterDAO();
                // Get associated Geometric from database
                clicked = dao.getByUID(selected.getName());
                
        		if (clicked instanceof Primitive){
        			/* In world mode, select the whole Item (not only one of its Primitive) */
        			Node parentNode = selected.getParent();
        			clicked = dao.getByUID(parentNode.getName());
        		}
            } catch (SQLException e1) {
            	Log.exception(e1);
            }
        }
        return clicked;
    }
    
    @Override
	public void update(Observable obs, Object msg) {
    	if (obs instanceof Project) {
    		Config config = (Config) msg;
    		if (config.getName().equals("mouse.mode")) {
    			this.mouseMode = config.getValue();
    		}
    	}
	}
    
    private void dragSelectHandler() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	this.project.getSelectionManager().unselect();
        
        /* If it is an Area (Wall, Ground, Roof): select it */
        if (clicked instanceof Area)
        	selectArea((Area) clicked);
        
        else if (clicked instanceof Item){
        	selectItem((Item) clicked);
        	this.movingGeometric = clicked;
        	this.lastMousePos = getXYForMouse(0);
        }
        
        /* If it is a Point: initiate drag'n drop */
        else if (clicked instanceof Point)
    		this.movingGeometric = clicked;
    }
    
    @Override
	public void mouseMoved(float value) {
    	if (movingGeometric != null) {
    		if (movingGeometric instanceof Point)
    			dropMovingPoint(false);
    		else if (movingGeometric instanceof Item)
    			dropMovingItem(false);
    	}
    }
    

	@Override
	public void onLeftClick() {
		if (this.mouseMode.equals("construct")) { // We're in construct mode and left-click: add a point 
			construct();
		} else if (this.mouseMode.equals("dragSelect")) {
			dragSelectHandler();
		} 
	}

	@Override
	public void onLeftRelease() {
		if (movingGeometric != null) { // We're moving a point, and mouse button up: stop the point here
			if (movingGeometric instanceof Point)
				dropMovingPoint(true);
			else if (movingGeometric instanceof Item)
				dropMovingItem(true);
		}	
	}

	@Override
	public void onRightClick() {
		if (this.inConstruction.size() > 0) { // We're building a shape, and right-click: finish shape
			finalizeConstruct();
		} else if (this.mouseMode.equals("dragSelect")){
			Geometric clicked = getClickedObject();
			if (clicked instanceof Meshable){
				try {
					setTexture((Meshable)clicked,this.project.config("texture.selected"));
				} catch (SQLException ex) {
					Log.exception(ex);
				}
			}
		}
	}
	
}
