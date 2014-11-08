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
	
	/**
	 * Method used to select an object when the user right-clicked on the canvas
	 * @param cursorPosition The position of the click on the canvas 
	 */
	public void selectObject(Vector2f cursorPosition) {
		float mouseX = cursorPosition.getX();
		float mouseY = cursorPosition.getY();
		GeometryDAO dao = null;
		try {
			dao = _project.getGeometryDAO();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Vector3f camPos = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 0f).clone();
		Vector3f camDir = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 1f).subtractLocal(camPos);
		Ray ray = new Ray(camPos, camDir);
		
		CollisionResults results = new CollisionResults();
		_view.getRootNode().collideWith(ray, results);
		
		if (results.size() > 0){
			Geometry selected = results.getClosestCollision().getGeometry();
			Grouped grouped = (Grouped) dao.getByUID(selected.getName()); //TODO might not be a Grouped later
			if (grouped != null){
				grouped.toggleSelect();
				try {
					dao.update(grouped);
					dao.notifyObservers(grouped);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
