/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * @author Titouan Christophe
 */
public class Ground extends Groupable {
	public Ground(){
		super();
	}
	
	public Ground(Group group){
		super(group);
	}
	
	public String toString(){
		return "Floor" + getGroup().toString();
	}
}
