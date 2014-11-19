/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.views.ObjectTreeView;



/**
 * @author pierre
 *
 */
public class ObjectTreeController {
	private ObjectTreeView _view;
	private GeometryDAO _dao;
	private Project _project;
	
	/**
	 * @param project Project object from model
	 * 
	 */
	public ObjectTreeController(Project project) {
		_view = new ObjectTreeView(this, project);
		_project = project;
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
			System.out.println("[TreeController] Unselect " + grouped.getUID());
			grouped.deselect();
			try {
				for (Point p : (_dao.getPointsForShape(grouped.getGroup()))){
					p.deselect();
					_dao.update(p);
				}
				_dao.update(grouped);
				_dao.notifyObservers(grouped);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (element instanceof Shape){
			try {
				for (Point p : _dao.getPointsForShape((Shape) element)){
					_dao.refresh(p);
					p.deselect();
					_dao.update(p);
				}
				_dao.notifyObservers();
		
			} catch (SQLException err){
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
	}

	/**
	 * Set the select flag of a Grouped element
	 * @param element
	 */
	public void selectElement(Object element) {
		if (element instanceof Floor){
			Floor current = (Floor) element;
			_project.config("floor.current", current.getUID());
		} else if (element instanceof Grouped){
			Grouped grouped = (Grouped) element;
			System.out.println("[TreeController] Select " + grouped.getUID());
			grouped.select();
			try {
				for (Point p : (_dao.getPointsForShape(grouped.getGroup()))){
					p.select();
					_dao.update(p);
				}
				_dao.update(grouped);
				_dao.notifyObservers(grouped);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (element instanceof Shape){
			try {
				for (Point p : _dao.getPointsForShape((Shape) element)){
					_dao.refresh(p);
					p.select();
					_dao.update(p);
				}
				_dao.notifyObservers();
		
			} catch (SQLException err){
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
	}

	/**
	 * Unset the visible flag of a Grouped item
	 * @param grouped
	 */
	public void hideGrouped(Grouped grouped){
		grouped.hide();
		try {
			_dao.update(grouped);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the visible flag of a Grouped item
	 * @param grouped
	 */
	public void showGrouped(Grouped grouped){
		grouped.show();
		try {
			_dao.update(grouped);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setWidth(Wall wall, String userInput){
		double width = wall.getWidth();
		try {
			width = Double.parseDouble(userInput);
		} catch (NumberFormatException err){
			JOptionPane.showMessageDialog(_view, "Invalid width " + err.getMessage());
			return;
		}
		if (width <= 0){
			JOptionPane.showMessageDialog(_view, "Cannot set a non strictly positive width !");
			return;
		}
		wall.setWidth(width);
		try {
			_dao.update(wall);
			_dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the input of a user who entered a height for a floor, changes the height
	 * if the input is valid.
	 * @param floor the floor that will be updated
	 * @param userInput
	 */
	public void setHeight(Floor floor, String userInput){
		double height = floor.getHeight();
		try{
			height = Double.parseDouble(userInput);
		} catch (NumberFormatException err){
			JOptionPane.showMessageDialog(_view, "Invalid height "+ err.getMessage());
			return;
		}
		if (height <= 0){
			JOptionPane.showMessageDialog(_view, "A floor has to have a positive height!");
			return;
		}
		floor.setHeight(height);
		try{
			_dao.update(floor);
			_dao.notifyObservers();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
}
