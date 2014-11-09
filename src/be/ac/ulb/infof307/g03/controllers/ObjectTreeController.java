/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.Component;
import java.sql.SQLException;

import com.jme3.scene.Geometry;

import be.ac.ulb.infof307.g03.models.Geometric;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Group;
import be.ac.ulb.infof307.g03.models.Grouped;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Shape;
import be.ac.ulb.infof307.g03.views.ObjectTreeView;



/**
 * @author pierre
 *
 */
public class ObjectTreeController {
	private ObjectTreeView _view;
	private GeometryDAO _dao;
	
	/**
	 * @param project Project object from model
	 * 
	 */
	public ObjectTreeController(Project project) {
		_view = new ObjectTreeView(this, project);
		try {
			_dao = project.getGeometryDAO();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return The controller's view
	 */
	public ObjectTreeView getView(){
		return _view;
	}
	
	/**
	 * @param object 
	 * @param name 
	 * 
	 */
	public void renameNode(Object object, String name){
		if (object instanceof Group) {
			Group grp = (Group) object;
			grp.setName(name);
			_dao.notifyObservers(object);
		}
	}
	
	public void deleteNode(Object object){
		if (object instanceof Geometric){
			try {
				_dao.delete((Geometric) object);
				_dao.notifyObservers(object);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
