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
	MasterDAO master;
	Selectionable selected = null;
	Floor currentFloor = null;
	
	/** What do we manage here ? */
	public static Class[] selectionables = {Room.class, Item.class, Primitive.class};
	
	SelectionManager(Project project){
		try {
			this.master = project.getGeometryDAO();
			this.unselectAll();
			this.currentFloor = this.getFirstFloor();
			// TODO set this.currentFloor
		} catch (SQLException e) {
			Log.exception(e);
		}
	}
	
	private Floor getFirstFloor() throws SQLException{
		GeometricDAO<Floor> dao = this.master.getDao(Floor.class);
		return dao.queryForFirst(dao.queryBuilder().orderBy("index", true).prepare());
	}
	
	/**
	 * Unselect all the selected objects in database
	 * @throws SQLException
	 */
	public void unselectAll() {
		for (Class<? extends Geometric> klass : selectionables){
			try{
				GeometricDAO dao = this.master.getDao(klass);
				for (Object o : dao.queryForEq("selected", true)){
					Selectionable sel = (Selectionable) o;
					sel.unselect();
					dao.update(klass.cast(o));
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * - Sets an object as *the* selected object in the entire scene
	 * - Sets its selected flag to true
	 * - Saves its changes in the model, notifies observers
	 * - Sets the current floor to the floor of obj
	 * @param obj The object to select
	 */
	public void select(Selectionable obj){
		// Do not unselect/select the same object
		if (this.selected != null && this.selected.getUID().equals(obj.getUID()))
			return;
		
		// Unselecting the previously selected object/room
		this.unselect();
		
		this.selected = obj;
		this.selected.select();
		
		if (this.selected instanceof Room){
			Room room = (Room) this.selected;
			this.setCurrentFloor(room.getFloor());
		}
		else if (this.selected instanceof Item){
			Item item = (Item) this.selected;
			this.setCurrentFloor(item.getFloor());
		}

		// Updating the model with new changes
		try{
			Geometric geom = (Geometric) this.selected;
			GeometricDAO<? extends Geometric> dao = this.master.getDao(geom.getClass());
			dao.modify(geom);
			Log.debug("Selected %s", geom.getUID());
			this.master.notifyObservers();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @param obj The object to select if it wasn't selected, or to unselect if it was selected
	 * This method writes indirectly to the model and notifies all observers.
	 */
	public void toggleSelect(Selectionable obj){
		if (obj.isSelected()){
			this.unselect();
			if (obj instanceof Primitive)
				try {
					this.master.getDao(Primitive.class).refresh((Primitive) obj);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		else{
			this.select(obj);
		}
	}
	
	/**
	 * - Unselects the currently selected object in the scene
	 * - Sets its selected flag to false
	 * - Writes the changes to the model, notifies observers
	 */
	public void unselect(){
		if (this.selected != null) {
			this.selected.unselect();
			try {
				Geometric geom = (Geometric) this.selected;
				GeometricDAO<? extends Geometric> dao = this.master.getDao(geom.getClass());
				dao.modify(geom);
				Log.debug("Unselected %s", geom.getUID());
				this.master.notifyObservers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.selected = null;
		}
	}

	/**
	 * @return the currently selected object in the scene
	 */
	public Selectionable selected() {
		return this.selected;
	}
	
	/**
	 * @return the currently active floor
	 */
	public Floor currentFloor(){
		return this.currentFloor;
	}

	/**
	 * @param current the new current floor
	 */
	public void setCurrentFloor(Floor current) {
		this.currentFloor = current;
		Log.debug("Set current floor to %s", this.currentFloor.getUID());
	}
}
