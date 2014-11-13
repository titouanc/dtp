package be.ac.ulb.infof307.g03.models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Represent a change in the model
 * @author Titouan Christophe
 */
public class Change {
	/**
	 * Change types
	 */
	public final static int CREATE=1, UPDATE=2, DELETE=4;
	private Integer _type;
	private Geometric _item;
	
	/**
	 * Create a new change of a Geometric object
	 * @param type The change type (one of the above constants)
	 * @param item The geometric item to wrap
	 */
	public Change(Integer type, Geometric item){
		_type = type;
		_item = item;
	}
	
	/**
	 * Shortcut for Change(CREATE, item)
	 * @param item The geometric item to wrap
	 * @return a new Change object
	 */
	public static Change create(Geometric item){
		return new Change(CREATE, item);
	}
	
	/**
	 * Shortcut for Change(UPDATE, item)
	 * @param item The geometric item to wrap
	 * @return a new Change object
	 */
	public static Change update(Geometric item){
		return new Change(UPDATE, item);
	}
	
	/**
	 * Shortcut for Change(DELETE, item)
	 * @param item The geometric item to wrap
	 * @return a new Change object
	 */
	public static Change delete(Geometric item){
		return new Change(DELETE, item);
	}
	
	/**
	 * @return The wrapped Geometric item
	 */
	public Geometric getItem(){return _item;}
	
	/**
	 * @return The change type (one of CREATE, UPDATE, DELETE)
	 */
	public Integer getType(){return _type;}
	
	/**
	 * @return True if change type is CREATE
	 */
	public Boolean isCreation(){return getType() == CREATE;}
	
	/**
	 * @return True if change type is UPDATE
	 */
	public Boolean isUpdate(){return getType() == UPDATE;}
	
	/**
	 * @return True if change type is DELETE
	 */
	public Boolean isDeletion(){return getType() == DELETE;}
	
	public String toString(){
		String prefix = isCreation() ? "Creation " : isDeletion() ? "Deletion " : "Update "; 
		return prefix + getItem().getUID();
	}
}
