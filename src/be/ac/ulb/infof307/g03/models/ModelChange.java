package be.ac.ulb.infof307.g03.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Carry a set of modifications on the model
 * @author Titouan Christophe
 */
public class ModelChange {
	private Set<Geometric> _create, _update, _delete;
	
	/**
	 * Create a new empty set of changes
	 */
	public ModelChange() {
		_create = new HashSet<Geometric>();
		_update = new HashSet<Geometric>();
		_delete = new HashSet<Geometric>();
	}
	
	/**
	 * @return An iterator on all creations that occured
	 */
	public Iterable<Geometric> getCreates(){
		return _create;
	}
	
	/**
	 * @return An iterator on all updates that occured
	 */
	public Iterable<Geometric> getUpdates(){
		return _update;
	}
	
	/**
	 * @return An iterator on all deletions that occured
	 */
	public Iterable<Geometric> getDeletes(){
		return _delete;
	}
	
	/**
	 * Insert a new creation in the changeset
	 * @param geometric
	 */
	public void create(Geometric geometric){
		_create.add(geometric);
	}
	
	/**
	 * Insert a new update in the changeset
	 * @param geometric
	 */
	public void update(Geometric geometric){
		_update.add(geometric);
	}
	
	/**
	 * Insert a new deletion in the changeset
	 * @param geometric
	 */
	public void delete(Geometric geometric){
		_delete.add(geometric);
	}
	
	/**
	 * @return The number of changes in this changeset
	 */
	public Integer size(){
		return _create.size() + _update.size() + _delete.size();
	}
	
	/**
	 * @return True if there are no changes
	 */
	public Boolean isEmpty(){
		return size() == 0;
	}
}
