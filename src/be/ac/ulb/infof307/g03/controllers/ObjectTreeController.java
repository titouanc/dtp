/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.*;
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
			try {
				_dao.update(grp);
				_dao.notifyObservers(object);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Delete a Geometric node
	 * @param object
	 */
	public void deleteNode(Object object){
		if (object instanceof Geometric){
			Geometric item = (Geometric) object;
			try {
				System.out.println("DELETE " + item.toString());
				_dao.delete(item);
				_dao.notifyObservers(item);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Unset the select flag of a Grouped element
	 * @param element
	 */
	public void deselectElement(Object element) {
		if (element instanceof Grouped){
			Grouped grouped = (Grouped) element;
			System.out.println("Unselect " + grouped.getUID());
			grouped.deselect();
			try {
				_dao.update(grouped);
				_dao.notifyObservers(grouped);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set the select flag of a Grouped element
	 * @param element
	 */
	public void selectElement(Object element) {
		if (element instanceof Grouped){
			Grouped grouped = (Grouped) element;
			System.out.println("Select " + grouped.getUID());
			grouped.select();
			try {
				_dao.update(grouped);
				_dao.notifyObservers(grouped);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
