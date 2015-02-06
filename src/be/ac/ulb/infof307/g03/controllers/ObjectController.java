package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.Geometric;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Item;
import be.ac.ulb.infof307.g03.models.Meshable;
import be.ac.ulb.infof307.g03.models.Point;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class ObjectController extends CanvasController implements Observer {
	private Vector2f savedCenter = null;
	private Primitive builtPrimitive = null;
	private Entity currentEntity = null;
	private boolean leftClickPressed = false;
	
	public ObjectController(WorldView view, AppSettings appSettings){
		super(view, appSettings);

		try {
			this.currentEntity = (Entity) this.project.getGeometryDAO().getByUID(this.project.config("entity.current"));
			this.view.makeScene(this.currentEntity);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        view.getProject().addObserver(this);

	}

	private void updateShapeDisplay(boolean finalUpdate) {
    	Vector2f currPos = getXYForMouse(0);
    	float dist = currPos.distance(this.savedCenter);
		float dn = dist / FastMath.pow(3, 0.3333f);
    	this.builtPrimitive.setScale(new Vector3f(dn,dn,dn));
    	if (!this.builtPrimitive.getType().equals(Primitive.PYRAMID)) {
    		if (this.builtPrimitive.getType().equals(Primitive.SPHERE)) {
    			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,dn));
    		} else {
    			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,dn/2));
    		}
    	}
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(this.builtPrimitive);
			dao.notifyObservers(this.builtPrimitive);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    	
    	
    	if (finalUpdate){
    		this.builtPrimitive = null;
    		this.savedCenter = null;
    	}
 
    }
	
	public void dropMovingPrimitive(boolean finalMove) {
    	Primitive movingPrimitive = (Primitive) movingGeometric;
    	if (movingPrimitive == null)
    		return;
    	
    	Vector2f v = getXYForMouse(0);
    	movingPrimitive.setTranslation(new Vector3f(v.x,v.y,movingPrimitive.getTranslation().z));

    	try {
    		GeometryDAO dao = this.project.getGeometryDAO();
    		dao.update(movingPrimitive);
    		if (finalMove) 
    			movingGeometric = null;
    		dao.notifyObservers(movingPrimitive);
    	} catch (SQLException err){
    		Log.exception(err);
    	}
    }
	
	public void selectPrimitive(Primitive primitive) {
		try {
			primitive.toggleSelect();
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(primitive);
			dao.notifyObservers(primitive);
			Vector3f center = primitive.getTranslation();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initShape(String type) {
		this.savedCenter = getXYForMouse(0f);
		try {
			GeometryDAO dao = this.project.getGeometryDAO();
			this.builtPrimitive = new Primitive(this.currentEntity,type);
			this.builtPrimitive.setScale(new Vector3f(0,0,0));
			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,0));
			dao.create(this.builtPrimitive);
			dao.notifyObservers(this.builtPrimitive);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
	}
	
	private void dragSelectHandler() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	return;
        /* If it is a Primitive : select it */
        if (clicked instanceof Primitive) {
        	
        	this.movingGeometric = (Primitive) clicked;
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
	
	
	@Override
	public void mouseMoved(float value) {
		if (movingGeometric != null) {
    		if (movingGeometric instanceof Primitive)
    			dropMovingPrimitive(false);
    	} else if (this.builtPrimitive != null) {
    		if (this.leftClickPressed)
    			updateShapeDisplay(false);
    	}
	}

	@Override
	public void onLeftClick() {
		this.leftClickPressed = true;
		if (this.mouseMode.equals("dragSelect")) {
			dragSelectHandler();
		} else if (this.mouseMode.equals("pyramid")) {
			initShape(Primitive.PYRAMID);
		} else if (this.mouseMode.equals("cylinder")) {
			initShape(Primitive.CYLINDER);
		} else if (this.mouseMode.equals("sphere")) {
			initShape(Primitive.SPHERE);
		} else if (this.mouseMode.equals("cube")) {
			initShape(Primitive.CUBE);
		}
	}

	@Override
	public void onLeftRelease() {
		this.leftClickPressed = false;
		if (movingGeometric != null) { // We're moving a point, and mouse button up: stop the point here
			if (movingGeometric instanceof Primitive) 
				dropMovingPrimitive(true);
		} else if (this.builtPrimitive != null) {
			updateShapeDisplay(true);			
		}	
	}

	@Override
	public void onRightClick() {
		if (this.mouseMode.equals("dragSelect")){
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
