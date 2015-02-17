/**
 * 
 */
package be.ac.ulb.infof307.g03.world;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 * @author fhennecker,pierre,wmoulart
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController extends CanvasController implements Observer {
    
	// Attributes
    private List<Point> inConstruction = new LinkedList <Point>();;	
    private Floor currentFloor = null;

    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param view The controller's view
     * @param settings The jMonkey application settings
     */
    public WorldController(WorldView view, AppSettings settings){
    	super(view, settings);
    	
        try {
			this.currentFloor = (Floor) this.project.getGeometryDAO().getByUID(this.project.config("floor.current"));
        } catch (SQLException e) {
			e.printStackTrace();
		}
        
        view.getProject().addObserver(this);
        view.makeScene();
    }
    
    /**
     * Getter
     * @return The current floor.
     */
    public Floor getCurrentFloor(){
    	return this.currentFloor;
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
    	
    	Vector3f newPos = getXYForMouse((float) this.currentFloor.getBaseHeight());
    	movingPoint.setX(newPos.x);
    	movingPoint.setY(newPos.y);
    	
        try {
        	MasterDAO dao = this.project.getGeometryDAO();
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
		
		moving.setAbsolutePosition(getXYForMouse(moving.getAbsolutePositionVector().z));
		
		try {
    		MasterDAO dao = this.project.getGeometryDAO();
    		dao.getDao(Item.class).modify(moving);
    		if (finalMove) 
    			movingGeometric = null;
    		dao.notifyObservers();
    	} catch (SQLException err){
    		Log.exception(err);
    	}
	}
    
    /**
     * Toggle selection for a Area item, save to database and notify observers
     * @param area The Area item to select
     */
	public void selectArea(Area area) {
		try {
			area.toggleSelect();
			MasterDAO dao = this.project.getGeometryDAO();
			
			for (Point p : area.getPoints()){
				if (area.isSelected())
					p.select();
				else
					p.deselect();
				dao.getDao(Point.class).modify(p);
			}
			dao.getDao(area.getClass()).modify(area);
			dao.notifyObservers(area);
			
			String floorUID = area.getRoom().getFloor().getUID();
			if (! this.project.config("floor.current").equals(floorUID))
				this.project.config("floor.current", floorUID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Toggle selection for an Item, save to database and notify observers
     * @param item The Item to select
     */
	public void selectItem(Item item) {
		try {
			item.toggleSelect();
			MasterDAO dao = this.project.getGeometryDAO();
			dao.getDao(Item.class).modify(item);
			dao.notifyObservers();
			
			String floorUID = item.getFloor().getUID();
			if (! currentFloor.getUID().equals(floorUID)){
				this.project.config("floor.current", floorUID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    /**
     * Add the points in the Point List when user click to create his wall
     */
    public void construct(){
    	Vector3f newPos = getXYForMouse((float) this.currentFloor.getBaseHeight());
    	Point lastPoint=new Point(newPos.x, newPos.y, 0);
		lastPoint.select();
		
		try {
        	MasterDAO daoFactory = this.project.getGeometryDAO();
        	GeometricDAO<Point> pointDao = daoFactory.getDao(Point.class);
        	Point near = pointDao.queryForFirst(lastPoint.getQueryForNear(pointDao, 1));
        	if (near != null){
        		lastPoint = near;
        		lastPoint.select();
        		pointDao.modify(lastPoint);
        	} else {
        		pointDao.insert(lastPoint);
        	}
        	daoFactory.notifyObservers();
        } catch (SQLException err){
        	Log.exception(err);
        }
		this.inConstruction.add(lastPoint);
    }
    
	 /**
     * Mesh the points together for the walls creation
     */
    public void finalizeConstruct(){
    	try {
			MasterDAO daoFactory = this.project.getGeometryDAO();
			// Minimum 3 points to build a room
	    	if (this.inConstruction.size() >= 3){
	    		Room room = new Room();
	    		room.setFloor(this.currentFloor);
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
						e.printStackTrace();
					}
	    		}
	    		
	    		/* Create Room shape */
	    		for (Point p : this.inConstruction)
	    			room.addPoints(p);
	    		room.addPoints(this.inConstruction.get(0)); // close polygon
	    		daoFactory.getDao(Room.class).modify(room);
	    		daoFactory.getDao(Floor.class).refresh(this.currentFloor);
	    	}
	    	
	    	GeometricDAO<Point> pointDao = daoFactory.getDao(Point.class);
	    	for (Point p : this.inConstruction){
	    		if (p.getBindings().size() == 0){
	    			pointDao.remove(p);
	    		} else {
	    			p.deselect();
	    			pointDao.modify(p);
	    		}
	    	}
	    	daoFactory.notifyObservers();
	    	
	    	this.inConstruction.clear();
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
            	MasterDAO dao = this.project.getGeometryDAO();
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
    		if (config.getName().equals("floor.current")){
    			String newUID = config.getValue();
    			if (this.currentFloor != null && newUID.equals(this.currentFloor.getUID()))
    				return;
    			try {
    				Geometric found = this.project.getGeometryDAO().getByUID(config.getValue());
    				this.currentFloor = (found != null) ? (Floor) found : null;
    			} catch (SQLException ex) {
    				Log.exception(ex);
    			}
    		} else if (config.getName().equals("mouse.mode")) {
    			this.mouseMode = config.getValue();
    		}
    	}
	}
    
    private void dragSelectHandler() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	this.deselectAll();
        
        /* If it is an Area (Wall, Ground, Roof): select it */
        if (clicked instanceof Area)
        	selectArea((Area) clicked);
        
        else if (clicked instanceof Item){
        	selectItem((Item) clicked);
        	this.movingGeometric = clicked;
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
		Log.debug("Left Click");
		if (this.mouseMode.equals("construct")) { // We're in construct mode and left-click: add a point 
			construct();
		} else if (this.mouseMode.equals("dragSelect")) {
			dragSelectHandler();
		} 
		
	}

	@Override
	public void onLeftRelease() {
		Log.debug("Left Release");
		if (movingGeometric != null) { // We're moving a point, and mouse button up: stop the point here
			if (movingGeometric instanceof Point)
				dropMovingPoint(true);
			else if (movingGeometric instanceof Item)
				dropMovingItem(true);
		}	
	}

	@Override
	public void onRightClick() {
		Log.debug("Right Click");
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
