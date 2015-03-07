/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Selectionable;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author fhennecker
 * The SelectionManager makes sure the following constraints for selection are respected:
 * - maximum 1 room is selected
 * - maximum 1 object is selected
 * - a room and an object cannot be selected at the same time
 * - the current floor is the floor on which the last selected room/object was
 */
public class SelectionManager {
	Project project;
	MasterDAO master;
	Selectionable selected = null;
	Floor currentFloor = null;
	
	SelectionManager(Project project){
		this.project = project;
		try {
			this.master = project.getGeometryDAO();
			// TODO set this.currentFloor
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void select(Selectionable obj){
		// Unselecting the previously selected object/room
		this.unselect();
		
		this.selected = obj;
		this.selected.select();
		
		if (this.selected instanceof Room){
			Room room = (Room) this.selected;
			this.currentFloor = room.getFloor();
			Log.debug("Selected Room");
		}
		else if (this.selected instanceof Item){
			Item item = (Item) this.selected;
			this.currentFloor = item.getFloor();
			Log.debug("Selected Item");
		}

		// Updating the model with new changes
		try{
			Geometric geom = (Geometric) this.selected;
			GeometricDAO<? extends Geometric> dao = this.master.getDao(geom.getClass());
			dao.modify(geom);
			this.master.notifyAll();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void toggleSelect(Selectionable obj){
		if (this.selected == obj){
			this.unselect();
		}
		else{
			this.select(obj);
		}
	}
	
	public void unselect(){
		if (this.selected != null) {
			this.selected.unselect();
			try {
				Geometric geom = (Geometric) this.selected;
				GeometricDAO<? extends Geometric> dao = this.master.getDao(geom.getClass());
				dao.modify(geom);
				this.master.notifyAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.selected = null;
		}
	}

	public Selectionable selected() {
		return this.selected;
	}
	
	public Floor currentFloor(){
		return this.currentFloor;
	}

	public void setCurrentFloor(Floor current) {
		this.currentFloor = current;
	}
}
