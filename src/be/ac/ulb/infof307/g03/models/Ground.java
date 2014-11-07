/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * @author Titouan Christophe
 */
public class Ground extends Grouped {
	public Ground() {
		super();
	}

	public Ground(Group group) {
		super(group);
	}

	@Override
	public final String toString() {
		return "Ground" + getGroup().toString();
	}
}
