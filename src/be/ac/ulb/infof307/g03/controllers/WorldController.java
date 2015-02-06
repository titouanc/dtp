/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre,wmoulart
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController extends CanvasController implements Observer {
    
	// Attributes
    private List<Point> inConstruction ;	
    private Floor currentFloor = null;
		
	private LinkedList<Change> queuedChanges = null;

    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param settings The jMonkey application settings
     * @param project The main project
     * @throws SQLException 
     */
    public WorldController(WorldView view, AppSettings settings){
    	super(view, settings);
    	
        this.inConstruction = new LinkedList <Point>();

        String floorUID = this.project.config("floor.current");
        List<Floor> listFloor = null;
        try {
			this.currentFloor = (Floor) this.project.getGeometryDAO().getByUID(floorUID);
			listFloor = project.getGeometryDAO().getFloors();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (this.currentFloor == null && listFloor.size()>0)
        	this.currentFloor = listFloor.get(0);
        
        view.getProject().addObserver(this);
        
        view.makeScene();
    }
    
    public Floor getCurrentFloor(){
    	return this.currentFloor;
    }
    
    /**
	 * @param p Get coordinates X and Y into Point
	 */
	public void getXYForMouse(Point p){
		Vector2f myVector = getXYForMouse((float) this.currentFloor.getBaseHeight());
		p.setX(myVector.getX());
		p.setY(myVector.getY());
	}
    
    /**
     * Drop the currently moving point:
     * - Compute final position
     * - Update in database and notify
     * - Set current moving point to null
     */
    public void dropMovingPoint(boolean finalMove){
    	Point movingPoint = (Point) movingGeometric;
    	if (movingPoint == null)
    		return;
    	
    	getXYForMouse(movingPoint);
        
        try {
        	GeometryDAO dao = this.project.getGeometryDAO();
        	dao.update(movingPoint);
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
		
		Vector2f pos = getXYForMouse(moving.getAbsolutePositionVector().z);
		moving.setPosition(new Vector3f(pos.x, pos.y, moving.getPositionVector().z));
		try {
    		GeometryDAO dao = this.project.getGeometryDAO();
    		dao.update(moving);
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
			GeometryDAO dao = this.project.getGeometryDAO();
			for (Point p : area.getPoints()){
				if (area.isSelected())
					p.select();
				else
					p.deselect();
				dao.update(p);
			}
			dao.update(area);
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
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(item);
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
    	Point lastPoint=new Point();
		lastPoint.setZ(0);
		getXYForMouse(lastPoint);
		lastPoint.select();
		
		try {
        	GeometryDAO dao = this.project.getGeometryDAO();
        	Point found = dao.findClosePoint(lastPoint, 1);
        	if (found != null){
        		lastPoint = found;
        		lastPoint.select();
        		dao.update(found);
        	} else {
        		dao.create(lastPoint);
        	}
        	dao.notifyObservers();
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
			GeometryDAO dao = this.project.getGeometryDAO();
	    	if (this.inConstruction.size() >= 3){
	    		dao.createRoom(this.currentFloor, this.inConstruction);
	    	} 
	    	for (Point p : this.inConstruction){
	    		if (p.getBindings().size() == 0){
	    			dao.delete(p);
	    		} else {
	    			p.deselect();
	    			dao.update(p);
	    		}
	    	}
	    	dao.notifyObservers();
	    	
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
            	GeometryDAO dao = this.project.getGeometryDAO();
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
    				this.currentFloor = (Floor) this.project.getGeometryDAO().getByUID(config.getValue());
    			} catch (SQLException ex) {
    				Log.exception(ex);
    			}
    		} else if (config.getName().equals("mouse.mode")) {
    			this.mouseMode = config.getValue();
    		}
    	}
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
		}
		else if (this.mouseMode.equals("dragSelect")){
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
