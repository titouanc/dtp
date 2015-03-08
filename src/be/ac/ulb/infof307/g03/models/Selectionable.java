/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * @author Titou, Phlaurantein
 *
 */
public interface Selectionable {
	/**
	 * @return A unique identifier for this item
	 */
	public String getUID();
	
	/**
	 * @return True if the object is selected
	 */
	public Boolean isSelected();
	
	/**
	 * Select the object
	 */
	public void select();
	
	/**
	 * Unselect the object
	 */
	public void unselect();
	
	/**
	 * Toggle selection
	 */
	public void toggleSelect();
}
