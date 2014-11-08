/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Grouped;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController {
	
	private WorldView _view;
	private Project _project;
	private boolean _isViewCreated = false;
	CameraModeController _cameraModeController = new CameraModeController();
	
	/**
	 * Constructor of WorldController.
	 * It creates the controller view.
	 * @param settings The jMonkey application settings
	 * @throws SQLException 
	 */
	public WorldController(AppSettings settings, Project project) throws SQLException{
		_view = new WorldView(this, project.getGeometryDAO());
		_view.setSettings(settings);
		_view.createCanvas();
		
		_project = project;
	}
	
	/**
	 * @return the world view.
	 */
	public WorldView getView(){
		return _view;
	}
	
	/**
	 * @return The view context.
	 */
	public JmeContext getViewContext(){
		return _view.getContext();
	}
	
	/** 
	 * @return The camera mode controller.
	 */
	public CameraModeController getCameraModeController() {
		return _cameraModeController;
	}
	
	/**
	 * Start the view canvas.
	 */
	public void startViewCanvas(){
		_view.startCanvas();
	}
	
	/**
	 * Gets called when the view is initialized.
	 */
	public void onViewCreated(){
		_isViewCreated = true;
	}	
	
	private Grouped getGrouped(Geometry geom) throws SQLException{
		GeometryDAO dao = _project.getGeometryDAO();
		if (geom.getUserDataKeys().contains("type")){
			if (geom.getUserData("type").equals("wall")){
				return dao.getWall((Integer) geom.getUserData("id"));
			} else if (geom.getUserData("type").equals("ground")){
				return dao.getGround((Integer) geom.getUserData("id"));
			}
		}
		return null;
	}
	
	/**
	 * Method used to select an object when the user right-clicked on the canvas
	 * @param cursorPosition The position of the click on the canvas 
	 */
	public void selectObject(Vector2f cursorPosition) {
		float mouseX = cursorPosition.getX();
		float mouseY = cursorPosition.getY();
		
		Vector3f camPos = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 0f).clone();
		Vector3f camDir = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 1f).subtractLocal(camPos);
		Ray ray = new Ray(camPos, camDir);
		
		CollisionResults results = new CollisionResults();
		_view.getRootNode().collideWith(ray, results);
		
		if (results.size() > 0){
			Geometry selected = results.getClosestCollision().getGeometry();
			Grouped grouped = null;
			try {
				grouped = getGrouped(selected);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (grouped == null)
				return;
			
			if (grouped.isSelected()){
				grouped.deselect();
				selected.getMaterial().setColor("Color", ColorRGBA.Gray);
			}
			else {
				grouped.select();
				selected.getMaterial().setColor("Color", ColorRGBA.Green);
			}
			try {
				_project.getGeometryDAO().update(grouped);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	}

}
