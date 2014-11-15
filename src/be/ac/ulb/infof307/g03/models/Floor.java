/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * 
 * @author Titouan Christophe
 */
public class Floor extends Grouped {
	public Floor(Group group){
		super(group);
	}
	
	public Floor(){
		super();
	}
	
	@Override
	public String getUID() {
		return String.format("flr-%d", getId());
	}

	@Override
	protected String innerToString() {
		return "Floor " + getGroup().toString();
	}
}
