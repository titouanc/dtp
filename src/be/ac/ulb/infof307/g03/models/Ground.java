/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * A ground is a surface delimited by a group of shapes
 * @author Titouan Christophe
 */
public class Ground extends Grouped {
	/**
	 * Create a new empty ground object, and create a new group for it
	 */
	public Ground() {
		super();
	}

	/**
	 * Create a new ground object associated to a group
	 * @param group The group to wrap in the Ground object
	 */
	public Ground(Group group) {
		super(group);
	}

	@Override
	public final String toString() {
		return "Ground" + getGroup().toString();
	}

	@Override
	public String getUID() {
		return String.format("gnd-%d", getId());
	}
}
