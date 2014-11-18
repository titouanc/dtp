/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * A roof is a surface delimited by the height of wall
 * @author Walter Moulart
 */
public class Roof extends Grouped {
	/**
	 * Create a new empty roof object, and create a new group for it
	 */
	public Roof() {
		super();
	}

	/**
	 * Create a new roof object associated to a group
	 * @param group The group to wrap in the Roof object
	 */
	public Roof(Group group) {
		super(group);
	}

	@Override
	protected final String innerToString() {
		return "Roof" + getGroup().toString();
	}

	@Override
	public String getUID() {
		return String.format("roof-%d", getId());
	}
}
