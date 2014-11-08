package be.ac.ulb.infof307.g03.models;

/**
 * Interface for all objects managed by GeometryDAO
 * @author Titouan Christophe
 */
public interface Geometric {
	/**
	 * Every Geometric object is part of a tree. 
	 * Does this one have children ?
	 * @return True if this node has no child
	 */
	public Boolean isLeaf();
	
	/**
	 * Every Geometric object has a unique identifier.
	 * @return
	 */
	public String getUID();
}
