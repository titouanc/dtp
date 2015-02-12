package be.ac.ulb.infof307.g03.models;



/**
 * Represent a change in the model
 * @author Titouan Christophe
 */
public class Change {

	/**
	 * Characterize the type of change.
	 * Define the integer 1 as create
	 */
	public final static int CREATE=1;
	/**
	 * Characterize the type of change.
	 * Define the integer 2 as update
	 */
	public final static int UPDATE=2;
	/**
	 * Characterize the type of change.
	 * Define the integer 4 as delete
	 */
	public final static int DELETE=4;
	private Integer type;
	private Geometric item;
	
	/**
	 * Create a new change of a Geometric object
	 * @param type The change type (one of the above constants)
	 * @param item The geometric item to wrap
	 */
	public Change(Integer type, Geometric item){
		this.type = type;
		this.item = item;
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
	public Geometric getItem(){return this.item;}
	
	/**
	 * Replace the referenced changed item
	 * @param item The new changed item
	 */
	public void setItem(Geometric item){this.item = item;}
	
	/**
	 * @return The change type (one of CREATE, UPDATE, DELETE)
	 */
	public Integer getType(){return this.type;}
	
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
		return prefix + getItem().toString();
	}
}
